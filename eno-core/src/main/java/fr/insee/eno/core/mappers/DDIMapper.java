package fr.insee.eno.core.mappers;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.reference.DDIIndex;
import instance33.DDIInstanceDocument;
import logicalproduct33.VariableGroupType;
import logicalproduct33.impl.VariableGroupTypeImpl;
import logicalproduct33.impl.VariableTypeImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class DDIMapper extends Mapper {

    private final DDIInstanceDocument ddiInstanceDocument;
    private final DDIIndex ddiIndex;

    public DDIMapper(DDIInstanceDocument ddiInstanceDocument) {
        //
        this.ddiInstanceDocument = ddiInstanceDocument;
        //
        ddiIndex = new DDIIndex();
        ddiIndex.indexDDI(ddiInstanceDocument);
    }

    public void mapDDI(EnoQuestionnaire enoQuestionnaire) {
        log.info("Starting mapping between DDI document and Eno model");
        recursiveMapping(enoQuestionnaire, ddiInstanceDocument);
        log.info("Finished mapping between DDI document and Eno model");
    }

    private void recursiveMapping(Object modelItemInstance, Object ddiItemInstance) {

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

                // Instantiate a Spring expression with the annotation content
                Expression expression = new SpelExpressionParser().parseExpression(ddiAnnotation.field());

                // Put the DDI index in the context
                EvaluationContext context = new StandardEvaluationContext();
                context.setVariable("index", ddiIndex);

                // Simple types // TODO: only String nom but other simple types later (probably)
                if (String.class.isAssignableFrom(classType)) {
                    // Simply set the value in the field // TODO: control that result of expression is not null
                    beanWrapper.setPropertyValue(propertyDescriptor.getName(),
                            expression.getValue(context, ddiItemInstance, classType));
                }

                // Lists (of complex objects) // TODO: manage the case of simple type lists (if the case occurs)
                else if (List.class.isAssignableFrom(classType)) {
                    // Get the DDI collection instance by evaluating the expression
                    Collection<?> ddiCollection = expression.getValue(context, ddiItemInstance, Collection.class);
                    if (ddiCollection == null) {
                        log.debug(String.format(
                                "Incoherent expression in field of DDI annotation on property '%s' in class %s.",
                                propertyDescriptor.getName(), modelItemInstance.getClass()));
                        throw new RuntimeException(String.format(
                                "DDI list mapped by the annotation on property '%s' in class %s is null",
                                propertyDescriptor.getName(), modelItemInstance.getClass()));
                    }
                    // Get the model collection instance
                    try {
                        @SuppressWarnings("unchecked")
                        Collection<Object> modelCollection = (Collection<Object>) propertyDescriptor.getReadMethod()
                                .invoke(modelItemInstance);
                        // Iterate on the DDI collection
                        for (Object ddiItemInstance2 : ddiCollection) {
                            // Instantiate a model object per DDI object and add it in the model collection
                            Class<?> modelTargetType = typeDescriptor.getResolvableType()
                                    .getGeneric(0).getRawClass();
                            assert modelTargetType != null;
                            try {
                                Object modelItemInstance2 = modelTargetType.getDeclaredConstructor().newInstance();
                                modelCollection.add(modelItemInstance2);
                                // Recursive call on these instances
                                recursiveMapping(modelItemInstance2, ddiItemInstance2);
                            } catch (NoSuchMethodException | InstantiationException e) {
                                log.debug("Default constructor may be missing in class " + modelTargetType);
                                throw new RuntimeException("Unable to create instance for class " + modelTargetType);
                            }
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
                        Object modelInstance2 = typeDescriptor.getType().getDeclaredConstructor().newInstance();
                        // Attach it the parent object
                        beanWrapper.setPropertyValue(propertyDescriptor.getName(), modelInstance2);
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

}
