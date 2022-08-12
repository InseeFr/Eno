package fr.insee.eno.core.mappers;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.converter.DDIConverter;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.reference.DDIIndex;
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

    private DDIInstanceDocument ddiInstanceDocument; // TODO: to be removed
    private DDIIndex ddiIndex;
    private EvaluationContext context;

    public DDIMapper() {}

    @Deprecated
    public DDIMapper(@NonNull DDIInstanceDocument ddiInstanceDocument) {
        this.ddiInstanceDocument = ddiInstanceDocument;
        // Index DDI
        ddiIndex = new DDIIndex();
        ddiIndex.indexDDI(ddiInstanceDocument);
        // Put the DDI index in the context
        context = new StandardEvaluationContext();
        context.setVariable("index", ddiIndex);
        //
        log.debug(this+ " instantiated");
    }

    private void setup(AbstractIdentifiableType ddiObject) {
        // Index DDI object
        ddiIndex = new DDIIndex();
        ddiIndex.indexDDIObject(ddiObject);
        // Init the context and put the DDI index
        context = new StandardEvaluationContext();
        context.setVariable("index", ddiIndex);
    }

    @Deprecated
    public void mapDDI(@NonNull EnoQuestionnaire enoQuestionnaire) {
        log.info("Starting mapping between DDI document and Eno model");
        recursiveMapping(ddiInstanceDocument.getDDIInstance(), enoQuestionnaire);
        log.info("Finished mapping between DDI document and Eno model");
    }

    public void mapDDI(@NonNull DDIInstanceDocument ddiInstanceDocument, @NonNull EnoQuestionnaire enoQuestionnaire) {
        mapDDI(ddiInstanceDocument.getDDIInstance(), enoQuestionnaire);
    }

    public void mapDDI(@NonNull DDIInstanceType ddiInstanceType, @NonNull EnoQuestionnaire enoQuestionnaire) {
        log.info("Starting mapping between DDI instance and Eno questionnaire.");
        setup(ddiInstanceType);
        recursiveMapping(ddiInstanceType, enoQuestionnaire);
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
        setup(ddiObject);
        recursiveMapping(ddiObject, enoObject);
    }

    private void recursiveMapping(Object ddiObject, EnoObject enoObject) {
        //TODO: refactor simple type / complex type mapping

        log.debug("Start mapping for "+DDIToString(ddiObject)
                +" with model context type '"+enoObject.getClass().getSimpleName()+"'");

        // Use Spring BeanWrapper to iterate on property descriptors of the model object
        BeanWrapper beanWrapper = new BeanWrapperImpl(enoObject);
        for (Iterator<PropertyDescriptor> iterator = propertyDescriptorIterator(beanWrapper); iterator.hasNext();) {
            PropertyDescriptor propertyDescriptor = iterator.next();

            // Spring TypeDescriptor of the current property descriptor (several usages below)
            TypeDescriptor typeDescriptor = beanWrapper.getPropertyTypeDescriptor(propertyDescriptor.getName());
            assert typeDescriptor != null;

            // Identify the class type of the property
            Class<?> classType = propertyDescriptor.getPropertyType();
            //Class<?> classType = typeDescriptor.getType();

            // Use the Spring type descriptor to get the DDI annotation if any
            DDI ddiAnnotation = typeDescriptor.getAnnotation(DDI.class);
            if (ddiAnnotation != null) {

                log.debug("Processing property '"+propertyDescriptor.getName()
                        +"' of class '"+enoObject.getClass().getSimpleName()+"'");

                // Instantiate a Spring expression with the annotation content
                Expression expression = new SpelExpressionParser().parseExpression(ddiAnnotation.field());

                // Simple types
                if (isSimpleType(classType)) {
                    // Simply set the value in the field
                    Object ddiValue = expression.getValue(context, ddiObject);
                    if (ddiValue != null) {
                        beanWrapper.setPropertyValue(propertyDescriptor.getName(), ddiValue);
                        log.debug("Value '"+beanWrapper.getPropertyValue(propertyDescriptor.getName())+"'"
                                +" on property '"+propertyDescriptor.getName()+"'"
                                +" of class '"+enoObject.getClass().getSimpleName()+"' set.");
                    }
                    // It is allowed to have null values (a DDI property can be present or not depending on the case)
                    else {
                        log.debug("null value from DDI annotation expression"
                                +" on property '"+propertyDescriptor.getName()+"'"
                                +" of class '"+enoObject.getClass().getSimpleName()+"' set");
                    }
                }

                // Lists
                else if (List.class.isAssignableFrom(classType)) {
                    // Get the DDI collection instance by evaluating the expression
                    List<?> ddiCollection = expression.getValue(context, ddiObject, List.class);
                    // If the DDI collection is null and null is not allowed by the annotation, exception
                    if (ddiCollection == null && !ddiAnnotation.allowNullList()) {
                        log.debug(String.format(
                                "Incoherent expression in field of DDI annotation on property '%s' in class %s.",
                                propertyDescriptor.getName(), enoObject.getClass()));
                        log.debug("If the DDI list can actually be null, use the annotation property to allow it.");
                        throw new RuntimeException(String.format(
                                "DDI list mapped by the annotation on property '%s' in class %s is null",
                                propertyDescriptor.getName(), enoObject.getClass()));
                    }
                    // If the DDI collection is null and null is allowed, do nothing, else:
                    else if (ddiCollection != null) {
                        // Get the content type of the model collection
                        Class<?> modelTargetType = typeDescriptor.getResolvableType()
                                .getGeneric(0).getRawClass();
                        assert modelTargetType != null;
                        // List of simple types
                        if (isSimpleType(modelTargetType)) {
                            try {
                                @SuppressWarnings("unchecked")
                                Collection<Object> modelCollection = (Collection<Object>) propertyDescriptor.getReadMethod()
                                        .invoke(enoObject);
                                modelCollection.addAll(ddiCollection);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        // Lists of complex types // Get the model collection instance
                        else if (EnoObject.class.isAssignableFrom(modelTargetType)) {
                            try {
                                @SuppressWarnings("unchecked")
                                Collection<EnoObject> modelCollection = (Collection<EnoObject>) propertyDescriptor.getReadMethod()
                                        .invoke(enoObject);
                                // Iterate on the DDI collection
                                for (int i=0; i<ddiCollection.size(); i++) {
                                    Object ddiObject2 = ddiCollection.get(i);
                                    // Put current list index in context TODO: I don't really like this but... :(((
                                    context.setVariable("listIndex", i);
                                    // Instantiate a model object per DDI object and add it in the model collection
                                    EnoObject enoObject2;
                                    // If the list content type is abstract call the converter
                                    if (Modifier.isAbstract(modelTargetType.getModifiers())) {
                                        enoObject2 = DDIConverter.instantiateFromDDIObject(ddiObject2);
                                    }
                                    // Else, call class constructor
                                    else {
                                        try {
                                            enoObject2 = (EnoObject) modelTargetType.getDeclaredConstructor().newInstance();
                                        } catch (NoSuchMethodException | InstantiationException e) {
                                            log.warn("Default constructor may be missing in class " + modelTargetType);
                                            throw new RuntimeException("Unable to create instance for class " + modelTargetType);
                                        }
                                    }
                                    // Keep parent reference
                                    enoObject2.setParent(enoObject);
                                    // Add the created instance in the model list
                                    modelCollection.add(enoObject2);
                                    // Recursive call on these instances
                                    recursiveMapping(ddiObject2, enoObject2);
                                }
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException(String.format(
                                        "Error when calling read method from property descriptor '%s' in class %s.",
                                        propertyDescriptor.getName(), enoObject.getClass()),
                                        e);
                            }
                        }
                        //
                        else {
                            log.debug(ddiAnnotation.field());
                            throw new RuntimeException("Object is neither a simple type nor an Eno object."); //TODO: pass parameters in exception message
                        }
                    }

                }

                // Complex types
                else if (EnoObject.class.isAssignableFrom(classType)) { // TODO: if EnoObject.class.isAssignableFrom(...)
                    try {
                        // Instantiate the model object
                        EnoObject enoObject2 = (EnoObject) typeDescriptor.getType().getDeclaredConstructor().newInstance();
                        // Attach it the parent object
                        beanWrapper.setPropertyValue(propertyDescriptor.getName(), enoObject2);
                        // Keep parent reference
                        enoObject2.setParent(enoObject);
                        // Recursive call of the mapper to dive into this object // TODO: control that result of expression is not null
                        recursiveMapping(expression.getValue(context, ddiObject), enoObject2);
                    } catch (InvocationTargetException | NoSuchMethodException |
                             InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e); // TODO : refactor redundant code above
                    }
                }

                else {
                    throw new RuntimeException("Not a valid simple type or Eno object (or list of these)."); //TODO: more detailed message
                }

            }
        }

    }


    private String DDIToString(@NonNull Object ddiInstance) {
        return ddiInstance.getClass().getSimpleName()+"["
                +((ddiInstance instanceof AbstractIdentifiableType)?"id="+((AbstractIdentifiableType)ddiInstance).getIDArray(0).getStringValue():"")
                +"]";
    }

    @Override
    public String toString() {
        return "DDIMapper[" +
                "ddiInstanceDocument=" + toString(ddiInstanceDocument) +
                ", ddiIndex=" + ddiIndex +
                ']';
    }

    private String toString(DDIInstanceDocument ddiInstanceDocument){
        return """
    DDIInstanceDocument[id=%s]
    """.formatted(ddiInstanceDocument.getDDIInstance().getIDArray(0).getStringValue());

    }
}
