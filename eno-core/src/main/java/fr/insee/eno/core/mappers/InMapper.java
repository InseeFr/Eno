package fr.insee.eno.core.mappers;

import fr.insee.eno.core.annotations.InAnnotationValues;
import fr.insee.eno.core.converter.InConverter;
import fr.insee.eno.core.exceptions.technical.ConversionException;
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
        if (enoObject.getIndex() == null)
            enoObject.setIndex(new EnoIndex());
        enoIndex = enoObject.getIndex();
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
                complexTypeMapping(inputObject, modelContextType, beanWrapper, propertyDescriptor, expression, debug);
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

    private void complexTypeMapping(Object inputObject, Class<?> modelContextType, BeanWrapper beanWrapper, PropertyDescriptor propertyDescriptor, Expression expression, boolean debug) {
        // Get the input object from annotation expression
        String propertyName = propertyDescriptor.getName();
        Object inputObject2 = spelEngine.evaluate(expression, inputObject, modelContextType, propertyName);
        // It is allowed to have a null input object on complex type properties
        if (inputObject2 == null) {
            if (debug)
                log.debug("{} object mapped by the annotation is null {}",
                        format, propertyDescription(propertyName, modelContextType.getName()));
            return;
        }
        // Instantiate the model target object
        EnoObject enoObject2 = getEnoModelObject(beanWrapper.getWrappedInstance(), propertyDescriptor, inputObject);
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
        List<Object> modelCollection = readList(propertyDescriptor, enoObject);
        boolean createNewObjects = createNewObjectsCondition(modelCollection, inputCollection, propertyName);
        // Get the content type of the model collection
        Class<?> modelTargetType = typeDescriptor.getResolvableType().getGeneric(0).getRawClass();
        // Collection of simple types
        if (isSimpleType(modelTargetType)) {
            modelCollection.clear();
            modelCollection.addAll(inputCollection);
            if (debug)
                log.debug("{} values set {}", collectionSize,
                        propertyDescription(propertyName, modelContextType.getSimpleName()));
        }
        // Collection of complex types
        else if (EnoObject.class.isAssignableFrom(modelTargetType)) {
            // Iterate on the input collection
            for (int i = 0; i < inputCollection.size(); i ++) {
                Object inputObject2 = inputCollection.get(i);
                if (debug)
                    log.debug("Iterating on {} {} objects {}", collectionSize, format,
                            propertyDescription(propertyName, modelContextType.getSimpleName()));
                // Instantiate an Eno object per input object and add it in the model collection
                EnoObject converted = convert(inputObject2, modelTargetType);
                if (createNewObjects) {
                    // Add the created instance in the model collection
                    modelCollection.add(converted);
                    // Recursive call on these instances
                    recursiveMapping(inputObject2, converted);
                    continue;
                }
                // If the Eno model collection is already filled by a previous mapping, iterate on it
                EnoObject enoObject2 = (EnoObject) modelCollection.get(i);
                checkTypesEquality(enoObject2, converted);
                recursiveMapping(inputObject2, enoObject2);
            }
        }
        //
        else {
            unknownTypeException(modelTargetType, propertyDescriptor, modelContextType);
        }
    }

    /** List of properties that are allowed to have a different size between Pogues and DDI mapping. */
    private static final List<String> LIST_PROPERTIES_EXCEPTIONS = List.of("codeResponses", "variables");

    /** Returns the condition that determines if the mapper should iterate on existing objects of the Eno collection
     * or create new ones.
     * @throws MappingException if size of both collections differ and if the property has not a special rule. */
    private static boolean createNewObjectsCondition(
            Collection<Object> enoModelCollection, Collection<?> inputCollection, String propertyName) {
        if (enoModelCollection.isEmpty())
            return true; // if the Eno list is initially empty: create new objects
        if (enoModelCollection.size() == inputCollection.size())
            return false; // if it has the same size as the input collection: iterate
        // if the size is not the same, something is wrong: throw an exception
        if (! LIST_PROPERTIES_EXCEPTIONS.contains(propertyName))
            throw new MappingException("Inconsistent list size between inputs on property '" + propertyName + "'.");
        // except if it is allowed for this property, then remove the existing objects and create new ones
        enoModelCollection.clear();
        return true;
    }

    private EnoObject getEnoModelObject(Object enoParentObject, PropertyDescriptor propertyDescriptor, Object inObject) {
        assert enoParentObject instanceof EnoObject; // check but no need to cast
        Object propertyObject;
        try {
            propertyObject = propertyDescriptor.getReadMethod().invoke(enoParentObject);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MappingException(
                    String.format("Unable to call get method of property '%s' in class '%s'",
                            propertyDescriptor.getName(), enoParentObject.getClass().getSimpleName()),
                    e);
        }
        // If the Eno property object is null, create a new instance by converting the input object
        EnoObject converted = convert(inObject, propertyDescriptor.getPropertyType());
        if (propertyObject == null) {
            return converted;
        }
        // Otherwise return this object (but check that the type matches the converted type)
        if (! (propertyObject instanceof EnoObject enoObject2))
            throw new IllegalArgumentException(
                    String.format("Property '%s' of class '%s' is not an Eno object.",
                            propertyDescriptor.getName(), enoParentObject.getClass().getSimpleName()));
        checkTypesEquality(enoObject2, converted);
        return enoObject2;
    }

    private static void checkTypesEquality(EnoObject enoObject2, EnoObject converted) {
        if (! enoObject2.getClass().equals(converted.getClass()))
            throw new ConversionException("Inconsistent conversion types between inputs.");
    }

    private EnoObject convert(Object inObject, Class<?> enoTargetType) {
        // If the Eno type is abstract call the converter
        if (Modifier.isAbstract(enoTargetType.getModifiers()))
            return inConverter.convertToEno(inObject, enoTargetType);
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
