package fr.insee.eno.core.mappers;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.converter.DDIConverter;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.reference.DDIIndex;
import fr.insee.eno.core.reference.EnoIndex;
import instance33.DDIInstanceDocument;
import instance33.DDIInstanceType;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import reusable33.AbstractIdentifiableType;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class DDIMapper extends Mapper {

    /** Index created in the entry object of mapping functions. */
    private EnoIndex index;

    public DDIMapper() {}

    private EvaluationContext setup(AbstractIdentifiableType ddiObject, EnoObject enoObject) {
        log.debug("DDI mapping entry object: " + DDIToString(ddiObject));
        // Index DDI object
        DDIIndex ddiIndex = new DDIIndex();
        ddiIndex.indexDDIObject(ddiObject);
        log.debug("DDI index: " + ddiIndex);
        // Init the context and put the DDI index
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("index", ddiIndex);
        // Eno index to be filled by the mapper
        index = new EnoIndex();
        enoObject.setIndex(index);
        //
        return context;
    }

    public void mapDDI(@NonNull DDIInstanceDocument ddiInstanceDocument, @NonNull EnoQuestionnaire enoQuestionnaire) {
        mapDDI(ddiInstanceDocument.getDDIInstance(), enoQuestionnaire);
    }

    public void mapDDI(@NonNull DDIInstanceType ddiInstanceType, @NonNull EnoQuestionnaire enoQuestionnaire) {
        log.info("Starting mapping between DDI instance and Eno questionnaire.");
        EvaluationContext context = setup(ddiInstanceType, enoQuestionnaire);
        recursiveMapping(ddiInstanceType, enoQuestionnaire, context);
        log.info("Finished mapping between DDI instance and Eno questionnaire.");
    }

    public void mapDDIObject(AbstractIdentifiableType ddiObject, EnoObject enoObject) {
        // TODO
        //DDIContext ddiContext = enoObject.getClass().getAnnotation(DDIContext.class);
        //List<Class<?>> contextTypes = new ArrayList<>(ddiContext.contextType());
        //if (! contextTypes.contains(ddiObject.getClass())) {
        //    throw new IllegalArgumentException(String.format(
        //            "DDI object of type '%s' is not compatible with Eno object of type '%s'",
        //            ddiObject.getClass(), enoObject.getClass()));
        //{
        //
        EvaluationContext context = setup(ddiObject, enoObject);
        recursiveMapping(ddiObject, enoObject, context);
    }

    private void recursiveMapping(Object ddiObject, EnoObject enoObject, EvaluationContext context) {

        log.debug("Start mapping for "+DDIToString(ddiObject)
                +" with model context type '"+enoObject.getClass().getSimpleName()+"'");

        // Use Spring BeanWrapper to iterate on property descriptors of the model object
        BeanWrapper beanWrapper = new BeanWrapperImpl(enoObject);
        for (Iterator<PropertyDescriptor> iterator = propertyDescriptorIterator(beanWrapper); iterator.hasNext();) {
            PropertyDescriptor propertyDescriptor = iterator.next();
            // Map property
            propertyMapping(ddiObject, enoObject, beanWrapper, propertyDescriptor, context);
        }

        // Add the object in the index (if it is an identifiable object)
        // Note: it is important to do this after that the mapping has been done
        // (otherwise the object id is not set).
        if (enoObject instanceof EnoIdentifiableObject enoIdentifiableObject) {
            index.put(enoIdentifiableObject.getId(), enoIdentifiableObject);
        }

    }

    public void propertyMapping(Object ddiObject, EnoObject enoObject, BeanWrapper beanWrapper, PropertyDescriptor propertyDescriptor, EvaluationContext context) {

        // Local variable used for logging purposes
        Class<?> modelContextType = enoObject.getClass();

        // Property name
        String propertyName = propertyDescriptor.getName();

        // Spring TypeDescriptor of the current property descriptor (several usages below)
        TypeDescriptor typeDescriptor = beanWrapper.getPropertyTypeDescriptor(propertyName);
        assert typeDescriptor != null;

        // Identify the class type of the property
        Class<?> classType = propertyDescriptor.getPropertyType();
        //Class<?> classType = typeDescriptor.getType();

        // Use the Spring type descriptor to get the DDI annotation if any
        DDI ddiAnnotation = typeDescriptor.getAnnotation(DDI.class);
        if (ddiAnnotation != null) {

            log.debug("Processing property '"+ propertyName
                    +"' of class '"+ modelContextType.getSimpleName()+"'");

            // Instantiate a Spring expression with the annotation content
            Expression expression = new SpelExpressionParser().parseExpression(ddiAnnotation.field());

            // Simple types
            if (isSimpleType(classType)) {
                simpleTypeMapping(ddiObject, modelContextType, beanWrapper, propertyName, context, expression);
            }

            // Complex types
            else if (EnoObject.class.isAssignableFrom(classType)) {
                complexTypeMapping(ddiObject, modelContextType, beanWrapper, propertyName, context, expression, classType);
            }

            // Lists
            else if (List.class.isAssignableFrom(classType)) {
                listMapping(ddiObject, enoObject, modelContextType, propertyDescriptor, propertyName, typeDescriptor, ddiAnnotation, context, expression);
            }

            else {
                unknownTypeException(classType, propertyDescriptor, modelContextType);
            }

        }
    }

    private void simpleTypeMapping(Object ddiObject, Class<?> modelContextType, BeanWrapper beanWrapper, String propertyName, EvaluationContext context, Expression expression) {
        // Simply set the value in the field
        Object ddiValue = expression.getValue(context, ddiObject);
        if (ddiValue != null) {
            beanWrapper.setPropertyValue(propertyName, ddiValue);
            log.debug("Value '"+ beanWrapper.getPropertyValue(propertyName)+"' set"
                    +" on property '"+ propertyName +"'"
                    +" of class '"+ modelContextType.getSimpleName()+"'");
        }
        // It is allowed to have null values (a DDI property can be present or not depending on the case)
        else {
            log.debug("null value got from evaluating DDI annotation expression" +
                    " on property '"+ propertyName +"'" +
                    " of class '"+ modelContextType.getSimpleName()+"'");
        }
    }

    private void complexTypeMapping(Object ddiObject, Class<?> modelContextType, BeanWrapper beanWrapper, String propertyName, EvaluationContext context, Expression expression, Class<?> classType) {
        // Instantiate the model target object
        EnoObject enoObject2 = callConstructor(classType);
        // Attach it to the current object
        beanWrapper.setPropertyValue(propertyName, enoObject2);
        log.debug("New instance of '"+enoObject2.getClass().getSimpleName()+"' set"
                +" on property '"+ propertyName +"'"
                +" of class '"+ modelContextType.getSimpleName()+"'");
        // Recursive call of the mapper to dive into this object
        Object ddiObject2 = expression.getValue(context, ddiObject);
        if (ddiObject2 != null) {
            recursiveMapping(ddiObject2, enoObject2, context);
        }
        // For now, it is not allowed to have a null DDI object on complex type properties
        else {
            throw new RuntimeException(String.format(
                    "DDI object mapped by the annotation on property '%s' in class %s is null",
                    propertyName, modelContextType));
        }
    }

    private void listMapping(Object ddiObject, EnoObject enoObject, Class<?> modelContextType, PropertyDescriptor propertyDescriptor, String propertyName, TypeDescriptor typeDescriptor, DDI ddiAnnotation, EvaluationContext context, Expression expression) {
        // Get the DDI collection instance by evaluating the expression
        List<?> ddiCollection = expression.getValue(context, ddiObject, List.class);
        // If the DDI collection is null and null is not allowed by the annotation, exception
        if (ddiCollection == null && !ddiAnnotation.allowNullList()) {
            log.debug(String.format(
                    "Incoherent expression in field of DDI annotation on property '%s' in class %s.",
                    propertyName, modelContextType));
            log.debug("If the DDI list can actually be null, use the annotation property to allow it.");
            throw new RuntimeException(String.format(
                    "DDI list mapped by the annotation on property '%s' in class %s is null",
                    propertyName, modelContextType));
        }
        // If the DDI collection is null and null is allowed, do nothing, else:
        else if (ddiCollection != null) {
            int collectionSize = ddiCollection.size();
            // Get the Eno model collection
            Collection<Object> modelCollection = readCollection(propertyDescriptor, enoObject);
            // Get the content type of the model collection
            Class<?> modelTargetType = typeDescriptor.getResolvableType()
                    .getGeneric(0).getRawClass();
            assert modelTargetType != null;
            // List of simple types
            if (isSimpleType(modelTargetType)) {
                modelCollection.addAll(ddiCollection);
                log.debug(collectionSize+" values set"
                        +" on property '"+ propertyName +"'"
                        +" of class '"+ modelContextType.getSimpleName()+"'");
            }
            // Lists of complex types
            else if (EnoObject.class.isAssignableFrom(modelTargetType)) {
                // Iterate on the DDI collection
                for (int i=0; i<collectionSize; i++) {
                    log.debug("Iterating on "+collectionSize+" DDI objects"
                            +" on property '"+ propertyName +"'"
                            +" of class '"+ modelContextType.getSimpleName()+"'");
                    Object ddiObject2 = ddiCollection.get(i);
                    // Put current list index in context TODO: I don't really like this but... :(((
                    context.setVariable("listIndex", i);
                    // Instantiate a model object per DDI object and add it in the model collection
                    EnoObject enoObject2;
                    // If the list content type is abstract call the converter
                    if (Modifier.isAbstract(modelTargetType.getModifiers())) {
                        enoObject2 = DDIConverter.instantiateFromDDIObject(ddiObject2); //TODO: remove usage of this (conversion using annotations)
                    }
                    // Else, call class constructor
                    else {
                        enoObject2 = callConstructor(modelTargetType);
                    }
                    // Add the created instance in the model list
                    modelCollection.add(enoObject2);
                    // Recursive call on these instances
                    recursiveMapping(ddiObject2, enoObject2, context);
                }
            }
            //
            else {
                unknownTypeException(modelTargetType, propertyDescriptor, modelContextType);
            }
        }
    }

    EnoObject callConstructor(Class<?> classType) {
        try {
            return (EnoObject) classType.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            log.debug("Default constructor may be missing in class " + classType);
            throw new RuntimeException("Unable to create instance for class " + classType, e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Unable to create instance for class " + classType, e);
        }
    }

    private static String DDIToString(@NonNull Object ddiInstance) {
        return ddiInstance.getClass().getSimpleName()+"["
                +((ddiInstance instanceof AbstractIdentifiableType) ?
                "id="+((AbstractIdentifiableType)ddiInstance).getIDArray(0).getStringValue() :
                "")
                +"]";
    }

}
