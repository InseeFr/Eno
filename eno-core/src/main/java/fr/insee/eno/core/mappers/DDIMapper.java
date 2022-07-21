package fr.insee.eno.core.mappers;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.converter.DDIConverter;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.reference.DDIIndex;
import instance33.DDIInstanceDocument;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.SpelEvaluationException;
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

    private final DDIInstanceDocument ddiInstanceDocument;
    private final DDIIndex ddiIndex;
    private final EvaluationContext context;

    public DDIMapper(@NonNull DDIInstanceDocument ddiInstanceDocument) {
        this.ddiInstanceDocument = ddiInstanceDocument;
        // Index DDI
        ddiIndex = new DDIIndex();
        ddiIndex.indexDDI(ddiInstanceDocument);
        // Put the DDI index in the context
        context = new StandardEvaluationContext();
        context.setVariable("index", ddiIndex);
        //
        //log.atDebug().log(()->this+ " instantiated"); //FIXME
    }

    public void mapDDI(@NonNull EnoQuestionnaire enoQuestionnaire) {
        log.info("Starting mapping between DDI document and Eno model");
        recursiveMapping(enoQuestionnaire, ddiInstanceDocument.getDDIInstance());
        log.info("Finished mapping between DDI document and Eno model");
    }

    private void recursiveMapping(EnoObject modelItemInstance, Object ddiItemInstance) {

        //log.atDebug().log(()->"Start mapping for "+DDIToString(ddiItemInstance)+" to "+modelItemInstance); // FIXME
        // Use Spring BeanWrapper to iterate on property descriptors of the model object
        BeanWrapper beanWrapper = new BeanWrapperImpl(modelItemInstance);
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

                //log.atDebug().log(()->"  Processing property "+propertyDescriptor.getName() +" for annotation "+ddiAnnotation); //FIXME

                // Instantiate a Spring expression with the annotation content
                Expression expression = new SpelExpressionParser().parseExpression(ddiAnnotation.field());

                // Simple types
                if (isSimpleType(classType)) {
                    // Simply set the value in the field
                    Object ddiValue = expression.getValue(context, ddiItemInstance);
                    if (ddiValue != null) {
                        beanWrapper.setPropertyValue(propertyDescriptor.getName(), ddiValue);
                    }
                    //log.atDebug().log(()->"  Value "+beanWrapper.getPropertyValue(propertyDescriptor.getName())+" setted"); //FIXME
                }

                // Lists (of complex objects) // TODO: manage the case of simple type lists (if the case occurs)
                else if (List.class.isAssignableFrom(classType)) {
                    // Get the DDI collection instance by evaluating the expression
                    List<?> ddiCollection = expression.getValue(context, ddiItemInstance, List.class);
                    if (ddiCollection == null) {
                        log.warn(String.format(
                                "Incoherent expression in field of DDI annotation on property '%s' in class %s.",
                                propertyDescriptor.getName(), modelItemInstance.getClass()));
                        throw new RuntimeException(String.format(
                                "DDI list mapped by the annotation on property '%s' in class %s is null",
                                propertyDescriptor.getName(), modelItemInstance.getClass()));
                    }
                    // Get the model collection instance
                    try {
                        @SuppressWarnings("unchecked")
                        Collection<EnoObject> modelCollection = (Collection<EnoObject>) propertyDescriptor.getReadMethod()
                                .invoke(modelItemInstance);
                        // Iterate on the DDI collection
                        for (int i=0; i<ddiCollection.size(); i++) {
                            Object ddiItemInstance2 = ddiCollection.get(i);
                            // Put current list index in context TODO: I don't really like this but... :(((
                            context.setVariable("listIndex", i);
                            // Instantiate a model object per DDI object and add it in the model collection
                            Class<?> modelTargetType = typeDescriptor.getResolvableType()
                                    .getGeneric(0).getRawClass();
                            assert modelTargetType != null;
                            EnoObject modelItemInstance2;
                            // If the list content type is abstract call the converter
                            if (Modifier.isAbstract(modelTargetType.getModifiers())) {
                                modelItemInstance2 = DDIConverter.instantiateFromDDIObject(ddiItemInstance2);
                            }
                            // Else, call class constructor
                            else {
                                try {
                                    modelItemInstance2 = (EnoObject) modelTargetType.getDeclaredConstructor().newInstance();
                                } catch (NoSuchMethodException | InstantiationException e) {
                                    log.warn("Default constructor may be missing in class " + modelTargetType);
                                    throw new RuntimeException("Unable to create instance for class " + modelTargetType);
                                }
                            }
                            // Keep parent reference
                            modelItemInstance2.setParent(modelItemInstance);
                            // Add the created instance in the model list
                            modelCollection.add(modelItemInstance2);
                            // Recursive call on these instances
                            recursiveMapping(modelItemInstance2, ddiItemInstance2);

                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(String.format(
                                "Error when calling read method from property descriptor '%s' in class %s.",
                                propertyDescriptor.getName(), modelItemInstance.getClass()),
                                e);
                    }
                }

                // Complex types
                else {
                    try {
                        // Instantiate the model object
                        EnoObject modelInstance2 = (EnoObject) typeDescriptor.getType().getDeclaredConstructor().newInstance();
                        // Attach it the parent object
                        beanWrapper.setPropertyValue(propertyDescriptor.getName(), modelInstance2);
                        // Keep parent reference
                        modelInstance2.setParent(modelItemInstance);
                        // Recursive call of the mapper to dive into this object // TODO: control that result of expression is not null
                        recursiveMapping(modelInstance2, expression.getValue(context, ddiItemInstance));
                    } catch (InvocationTargetException | NoSuchMethodException |
                             InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e); // TODO : refactor redundant code above
                    }
                }

            }
        }

    }


    private String DDIToString(@NonNull Object ddiItemInstance) {
        return ddiItemInstance.getClass().getSimpleName()+"["
                +((ddiItemInstance instanceof AbstractIdentifiableType)?"id="+((AbstractIdentifiableType)ddiItemInstance).getIDArray(0).getStringValue():"")
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
