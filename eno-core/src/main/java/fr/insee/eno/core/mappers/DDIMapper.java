package fr.insee.eno.core.mappers;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.converter.DDIConverter;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.reference.DDIIndex;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.eno.core.utils.DDIUtils;
import fr.insee.eno.core.utils.EnoSpelEngine;
import instance33.DDIInstanceDocument;
import instance33.DDIInstanceType;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import reusable33.AbstractIdentifiableType;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Mapper implementation for the DDI input format.
 * While mapping a DDI object to an Eno object, the mapper builds an index of Eno objects.
 */
@Slf4j
public class DDIMapper extends Mapper {

    /** Index created in the entry object of mapping functions. */
    private EnoIndex enoIndex;
    private DDIIndex ddiIndex;

    public DDIMapper() {
        this.format = Format.DDI;
    }

    private void setup(AbstractIdentifiableType ddiObject, EnoObject enoObject) {
        log.debug("DDI mapping entry object: " + DDIUtils.ddiToString(ddiObject));
        //
        indexDDIObject(ddiObject);
        // Init the context and put the DDI index
        spelEngine = new EnoSpelEngine(Format.DDI);
        spelEngine.getContext().setVariable("index", ddiIndex);
        // Eno index to be filled by the mapper
        enoIndex = new EnoIndex();
        enoObject.setIndex(enoIndex);
        // Set static methods to be used during mapping
        DDIBindings.setMethods(spelEngine.getContext());
    }

    private void indexDDIObject(AbstractIdentifiableType ddiObject) {
        ddiIndex = new DDIIndex();
        ddiIndex.indexDDIObject(ddiObject);
    }

    public void mapDDI(@NonNull DDIInstanceDocument ddiInstanceDocument, @NonNull EnoQuestionnaire enoQuestionnaire) {
        mapDDI(ddiInstanceDocument.getDDIInstance(), enoQuestionnaire);
    }

    public void mapDDI(@NonNull DDIInstanceType ddiInstanceType, @NonNull EnoQuestionnaire enoQuestionnaire) {
        log.info("Starting mapping between DDI instance and Eno questionnaire.");
        setup(ddiInstanceType, enoQuestionnaire);
        recursiveMapping(ddiInstanceType, enoQuestionnaire);
        log.info("Finished mapping between DDI instance and Eno questionnaire.");
    }

    public void mapDDIObject(AbstractIdentifiableType ddiObject, EnoObject enoObject) {
        //
        compatibilityCheck(ddiObject, enoObject);
        //
        setup(ddiObject, enoObject);
        recursiveMapping(ddiObject, enoObject);
    }

    private void recursiveMapping(Object ddiObject, EnoObject enoObject) {

        log.debug("Start mapping for "+ DDIUtils.ddiToString(ddiObject)
                +" with model context type '"+enoObject.getClass().getSimpleName()+"'");

        // Use Spring BeanWrapper to iterate on property descriptors of the model object
        BeanWrapper beanWrapper = new BeanWrapperImpl(enoObject);
        for (Iterator<PropertyDescriptor> iterator = propertyDescriptorIterator(beanWrapper); iterator.hasNext();) {
            PropertyDescriptor propertyDescriptor = iterator.next();
            // Map property
            propertyMapping(ddiObject, enoObject, beanWrapper, propertyDescriptor);
        }

        // Add the object in the index (if it is an identifiable object)
        // Note: it is important to do this after that the mapping has been done
        // (otherwise the object id is not set).
        if (enoObject instanceof EnoIdentifiableObject enoIdentifiableObject) {
            enoIndex.put(enoIdentifiableObject.getId(), enoIdentifiableObject);
        }

    }

    public void propertyMapping(Object ddiObject, EnoObject enoObject, BeanWrapper beanWrapper, PropertyDescriptor propertyDescriptor) {

        // Local variable used for logging purposes
        Class<?> modelContextType = enoObject.getClass();

        // Property name
        String propertyName = propertyDescriptor.getName();

        // Spring TypeDescriptor of the current property descriptor (several usages below)
        TypeDescriptor typeDescriptor = beanWrapper.getPropertyTypeDescriptor(propertyName);
        assert typeDescriptor != null;

        // Identify the class type of the property
        Class<?> classType = propertyDescriptor.getPropertyType();

        // Use the Spring type descriptor to get the DDI annotation if any
        DDI ddiAnnotation = typeDescriptor.getAnnotation(DDI.class);
        if (ddiAnnotation != null) {

            log.debug("Processing property '"+ propertyName
                    +"' of class '"+ modelContextType.getSimpleName()+"'");

            // Instantiate a Spring expression with the annotation content
            Expression expression = new SpelExpressionParser().parseExpression(ddiAnnotation.value());

            // Simple types
            if (isSimpleType(classType)) {
                simpleTypeMapping(ddiObject, modelContextType, beanWrapper, propertyName, expression);
            }

            // Complex types
            else if (EnoObject.class.isAssignableFrom(classType)) {
                complexTypeMapping(ddiObject, modelContextType, beanWrapper, propertyName, expression, classType);
            }

            // Collections
            else if (Collection.class.isAssignableFrom(classType)) {
                collectionMapping(ddiObject, enoObject, propertyDescriptor, typeDescriptor, ddiAnnotation, expression);
            }

            else {
                unknownTypeException(classType, propertyDescriptor, modelContextType);
            }

        }
    }

    private void simpleTypeMapping(Object ddiObject, Class<?> modelContextType, BeanWrapper beanWrapper, String propertyName, Expression expression) {
        // Read DDI value by evaluating mapping expression
        Object ddiValue = spelEngine.evaluate(expression, ddiObject, modelContextType, propertyName);
        // It is allowed to have null values (a DDI property can be present or not depending on the case)
        if (ddiValue == null) {
            log.debug("null value got from evaluating DDI annotation expression "
                    + propertyDescription(propertyName, modelContextType.getSimpleName()));
        }
        // Simply set the value in the field
        beanWrapper.setPropertyValue(propertyName, ddiValue);
        log.debug("Value '"+ beanWrapper.getPropertyValue(propertyName)+"' set "
                + propertyDescription(propertyName, modelContextType.getSimpleName()));
    }

    private void complexTypeMapping(Object ddiObject, Class<?> modelContextType, BeanWrapper beanWrapper, String propertyName, Expression expression, Class<?> classType) {
        // Get the DDI object from annotation expression
        Object ddiObject2 = spelEngine.evaluate(expression, ddiObject, modelContextType, propertyName);
        // It is now allowed to have a null DDI object on complex type properties
        if (ddiObject2 == null) {
            log.debug("DDI object mapped by the annotation is null "
                    + propertyDescription(propertyName, modelContextType.getName()));
            return;
        }
        // Instantiate the model target object
        EnoObject enoObject2 = convert(ddiObject2, classType);
        // Attach it to the current object
        beanWrapper.setPropertyValue(propertyName, enoObject2);
        log.debug("New instance of '"+enoObject2.getClass().getSimpleName()+"' set "
                + propertyDescription(propertyName, modelContextType.getSimpleName()));
        // Recursive call of the mapper to dive into this object
        recursiveMapping(ddiObject2, enoObject2);
    }

    private void collectionMapping(Object ddiObject, EnoObject enoObject, PropertyDescriptor propertyDescriptor, TypeDescriptor typeDescriptor, DDI ddiAnnotation, Expression expression) {
        // Local variables used for logging purposes
        Class<?> modelContextType = enoObject.getClass();
        String propertyName = propertyDescriptor.getName();
        // Get the DDI collection instance by evaluating the expression
        List<?> ddiCollection = spelEngine.evaluateToList(expression, ddiObject, modelContextType, propertyName);
        // If the DDI collection is null and null is not allowed by the annotation, exception
        if (ddiCollection == null && !ddiAnnotation.allowNullList()) {
            log.error("Incoherent expression in field of DDI annotation "
                    + propertyDescription(propertyName, modelContextType.getName()));
            log.error("If the DDI collection can actually be null, use the annotation property to allow it.");
            throw new MappingException("DDI collection mapped by the annotation is null "
                    + propertyDescription(propertyName, modelContextType.getName()));
        }
        // If the DDI collection is null and null is allowed, do nothing
        if (ddiCollection == null)
            return;
        // Else
        int collectionSize = ddiCollection.size();
        // Get the Eno model collection
        Collection<Object> modelCollection = readCollection(propertyDescriptor, enoObject);
        // Get the content type of the model collection
        Class<?> modelTargetType = typeDescriptor.getResolvableType().getGeneric(0).getRawClass();
        assert modelTargetType != null;
        // Collection of simple types
        if (isSimpleType(modelTargetType)) {
            modelCollection.addAll(ddiCollection);
            log.debug(collectionSize+" values set "
                    + propertyDescription(propertyName, modelContextType.getSimpleName()));
        }
        // Collection of complex types
        else if (EnoObject.class.isAssignableFrom(modelTargetType)) {
            // Iterate on the DDI collection
            for (int i=0; i<collectionSize; i++) {
                log.debug("Iterating on "+collectionSize+" DDI objects "
                        + propertyDescription(propertyName, modelContextType.getSimpleName()));
                Object ddiObject2 = ddiCollection.get(i);
                // Put current DDI list index in context TODO: I don't really like this but... :(((
                spelEngine.getContext().setVariable("listIndex", i);
                // Instantiate a model object per DDI object and add it in the model collection
                EnoObject enoObject2 = convert(ddiObject2, modelTargetType);
                // Add the created instance in the model collection
                modelCollection.add(enoObject2);
                // Recursive call on these instances
                recursiveMapping(ddiObject2, enoObject2);
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
            return DDIConverter.instantiateFromDDIObject(ddiObject, ddiIndex); //TODO: remove usage of this (conversion using annotations)
        // Else, call class constructor
        return callConstructor(enoTargetType);
    }

    EnoObject callConstructor(Class<?> classType) {
        try {
            return (EnoObject) classType.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            log.debug("Default constructor may be missing in class " + classType);
            throw new MappingException("Unable to create instance for class " + classType, e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new MappingException("Unable to create instance for class " + classType, e);
        }
    }

    private static String propertyDescription(String propertyName, String className) {
        return "(Property '"+ propertyName +"' of class '"+ className +"')";
    }

}
