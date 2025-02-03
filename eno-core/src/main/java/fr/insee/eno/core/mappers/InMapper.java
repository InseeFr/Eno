package fr.insee.eno.core.mappers;

import fr.insee.eno.core.annotations.InAnnotationValues;
import fr.insee.eno.core.converter.InConverter;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.reference.EnoIndex;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Mapper template method class for the input to Eno mapping.
 */
@Slf4j
public abstract class InMapper extends Mapper {

    /** Converter class to convert input objects into Eno model objects. */
    final InConverter inConverter;

    /** Index created in the entry object of mapping functions. */
    private EnoIndex enoIndex;

    InMapper(Format format, InConverter inConverter) {
        super(format);
        this.inConverter = inConverter;
    }

    private void setup(Object inputObject, EnoObject enoObject) {
        // Init the context
        spelEngine.resetContext();
        // Eno index to be filled by the mapper
        enoIndex = new EnoIndex();
        enoObject.setIndex(enoIndex);
        //
        specificSetup(inputObject);
    }

    /**
     * Method called at the end of the setup method.
     * Designed to add setup steps that are specific to concrete mappers.
     */
    void specificSetup(Object inputObject) {}

    public void mapInputObject(Object inputObject, EnoObject enoObject) {
        //
        compatibilityCheck(inputObject, enoObject);
        //
        setup(inputObject, enoObject);
        recursiveMapping(inputObject, enoObject);
    }

    private void recursiveMapping(Object inputObject, EnoObject enoObject) {

        // Use Spring BeanWrapper to iterate on property descriptors of the model object
        BeanWrapper beanWrapper = new BeanWrapperImpl(enoObject);
        for (Iterator<PropertyDescriptor> iterator = propertyDescriptorIterator(beanWrapper); iterator.hasNext();) {
            PropertyDescriptor propertyDescriptor = iterator.next();
            // Map property
            propertyMapping(inputObject, enoObject, beanWrapper, propertyDescriptor);
        }

        // Add the object in the index (if it is an identifiable object)
        // Note: it is important to do this after that the mapping has been done
        // (otherwise the object id is not set).
        if (enoObject instanceof EnoIdentifiableObject enoIdentifiableObject) {
            enoIndex.put(enoIdentifiableObject.getId(), enoIdentifiableObject);
        }

    }

    private void propertyMapping(Object inputObject, EnoObject enoObject, BeanWrapper beanWrapper, PropertyDescriptor propertyDescriptor) {

        // Local variable used for logging purposes
        Class<?> modelContextType = enoObject.getClass();

        // Property name
        String propertyName = propertyDescriptor.getName();

        // Spring TypeDescriptor of the current property descriptor (several usages below)
        TypeDescriptor typeDescriptor = beanWrapper.getPropertyTypeDescriptor(propertyName);

        // Identify the class type of the property
        Class<?> classType = propertyDescriptor.getPropertyType();

        // Use the Spring type descriptor to get the mapping annotation if any
        InAnnotationValues inAnnotationValues = readAnnotation(typeDescriptor);
        if (inAnnotationValues != null) {

            boolean debug = inAnnotationValues.debug();
            if (debug)
                log.debug("Processing property '{}' of class '{}' ", propertyName, modelContextType.getSimpleName());

            // Instantiate a Spring expression with the annotation content
            Expression expression = new SpelExpressionParser().parseExpression(inAnnotationValues.expression());

            // Simple types
            if (isSimpleType(classType)) {
                simpleTypeMapping(inputObject, modelContextType, beanWrapper, propertyName, expression, debug);
            }

            // Complex types
            else if (EnoObject.class.isAssignableFrom(classType)) {
                complexTypeMapping(inputObject, modelContextType, beanWrapper, propertyName, expression, classType, debug);
            }

            // Collections
            else if (Collection.class.isAssignableFrom(classType)) {
                boolean allowNullList = inAnnotationValues.allowNullList();
                collectionMapping(inputObject, enoObject, propertyDescriptor, typeDescriptor, expression, allowNullList, debug);
            }

            else {
                unknownTypeException(classType, propertyDescriptor, modelContextType);
            }

        }
    }

    /**
     * Method that reads the appropriated annotation and returns its values.
     * Must return null if there is no annotation that correspond to the mapper format.
     * @param typeDescriptor Type descriptor of the current property.
     * @return The values of the mapping annotation on the property, null otherwise.
     */
    abstract InAnnotationValues readAnnotation(TypeDescriptor typeDescriptor);

    private void simpleTypeMapping(Object inputObject, Class<?> modelContextType, BeanWrapper beanWrapper, String propertyName, Expression expression, boolean debug) {
        // Get the value in the input object by evaluating mapping expression
        Object inputValue = spelEngine.evaluate(expression, inputObject, modelContextType, propertyName);
        // It is allowed to have null values (a property can be present or not depending on the case)
        if (inputValue == null && debug) {
            log.debug("null expression got from evaluating {} annotation expression {}",
                    format, propertyDescription(propertyName, modelContextType.getSimpleName()));
        }
        // Simply set the expression in the field
        beanWrapper.setPropertyValue(propertyName, inputValue);
        if (debug)
            log.debug("Value '{}' set {}", beanWrapper.getPropertyValue(propertyName),
                    propertyDescription(propertyName, modelContextType.getSimpleName()));
    }

    private void complexTypeMapping(Object inputObject, Class<?> modelContextType, BeanWrapper beanWrapper, String propertyName, Expression expression, Class<?> classType, boolean debug) {
        // Get the input object from annotation expression
        Object inputObject2 = spelEngine.evaluate(expression, inputObject, modelContextType, propertyName);
        // It is allowed to have a null input object on complex type properties
        if (inputObject2 == null) {
            if (debug)
                log.debug("{} object mapped by the annotation is null {}",
                        format, propertyDescription(propertyName, modelContextType.getName()));
            return;
        }
        // Instantiate the model target object
        EnoObject enoObject2 = convert(inputObject2, classType);
        // Attach it to the current object
        beanWrapper.setPropertyValue(propertyName, enoObject2);
        if (debug)
            log.debug("New instance of '{}' set {}", enoObject2.getClass().getSimpleName(),
                    propertyDescription(propertyName, modelContextType.getSimpleName()));
        // Recursive call of the mapper to dive into this object
        recursiveMapping(inputObject2, enoObject2);
    }

    private void collectionMapping(Object inputObject, EnoObject enoObject, PropertyDescriptor propertyDescriptor, TypeDescriptor typeDescriptor, Expression expression, boolean allowNullList, boolean debug) {
        // Local variables used for logging purposes
        Class<?> modelContextType = enoObject.getClass();
        String propertyName = propertyDescriptor.getName();
        // Get the input collection instance by evaluating the expression
        List<?> inputCollection = spelEngine.evaluateToList(expression, inputObject, modelContextType, propertyName);
        // If the input collection is null and null is not allowed by the annotation, exception
        if (inputCollection == null && !allowNullList) {
            log.error("Incoherent expression in field of {} annotation {}", format,
                    propertyDescription(propertyName, modelContextType.getName()));
            log.error("If the {} collection can actually be null, use the annotation property to allow it.", format);
            throw new MappingException(format+" collection mapped by the annotation is null "
                    + propertyDescription(propertyName, modelContextType.getName()));
        }
        // If the input collection is null and null is allowed, do nothing
        if (inputCollection == null)
            return;
        // Else
        int collectionSize = inputCollection.size();
        // Get the Eno model collection
        Collection<Object> modelCollection = readCollection(propertyDescriptor, enoObject);
        if (! inputCollection.isEmpty())
            modelCollection.clear(); // added for the case when two inputs are used (Pogues + DDI)
        // Get the content type of the model collection
        Class<?> modelTargetType = typeDescriptor.getResolvableType().getGeneric(0).getRawClass();
        // Collection of simple types
        if (isSimpleType(modelTargetType)) {
            modelCollection.addAll(inputCollection);
            if (debug)
                log.debug("{} values set {}", collectionSize,
                        propertyDescription(propertyName, modelContextType.getSimpleName()));
        }
        // Collection of complex types
        else if (EnoObject.class.isAssignableFrom(modelTargetType)) {
            // Iterate on the input collection
            for (Object inputObject2 : inputCollection) {
                if (debug)
                    log.debug("Iterating on {} {} objects {}", collectionSize, format,
                            propertyDescription(propertyName, modelContextType.getSimpleName()));
                // Instantiate an Eno object per input object and add it in the model collection
                EnoObject enoObject2 = convert(inputObject2, modelTargetType);
                // Add the created instance in the model collection
                modelCollection.add(enoObject2);
                // Recursive call on these instances
                recursiveMapping(inputObject2, enoObject2);
            }
        }
        //
        else {
            unknownTypeException(modelTargetType, propertyDescriptor, modelContextType);
        }
    }

    private EnoObject convert(Object ddiObject, Class<?> enoTargetType) {
        // If the Eno type is abstract call the converter
        if (Modifier.isAbstract(enoTargetType.getModifiers()))
            return inConverter.convertToEno(ddiObject, enoTargetType);
        // Else, call class constructor
        return callConstructor(enoTargetType);
    }

    EnoObject callConstructor(Class<?> classType) {
        try {
            return (EnoObject) classType.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            log.debug("Default constructor may be missing in class {}", classType);
            throw new MappingException("Unable to create instance for class " + classType, e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new MappingException("Unable to create instance for class " + classType, e);
        }
    }

    private static String propertyDescription(String propertyName, String className) {
        return "(Property '"+ propertyName +"' of class '"+ className +"')";
    }

}
