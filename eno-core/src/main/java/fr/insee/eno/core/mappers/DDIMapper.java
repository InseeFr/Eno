package fr.insee.eno.core.mappers;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.reference.DDIIndex;
import instance33.DDIInstanceDocument;
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class DDIMapper extends Mapper {

    private final DDIInstanceDocument ddiInstanceDocument;
    private final DDIIndex ddiIndex;

    public DDIMapper(@NonNull DDIInstanceDocument ddiInstanceDocument) {

        this.ddiInstanceDocument = ddiInstanceDocument;
        //
        ddiIndex = new DDIIndex();
        ddiIndex.indexDDI(ddiInstanceDocument);
        log.debug(this+ " instanciated");
    }

    public void mapDDI(@NonNull EnoQuestionnaire enoQuestionnaire) {
        log.info("Starting mapping between DDI document and Eno model");
        recursiveMapping(enoQuestionnaire, ddiInstanceDocument);
        log.info("Finished mapping between DDI document and Eno model");
    }

    private void recursiveMapping(Object modelItemInstance, Object ddiItemInstance) {

        log.debug("Start mapping for "+DDIToString(ddiItemInstance)+" to "+modelItemInstance);
        BeanWrapper beanWrapper = new BeanWrapperImpl(modelItemInstance);

        for (Iterator<PropertyDescriptor> iterator = propertyDescriptorIterator(beanWrapper); iterator.hasNext();) {

            PropertyDescriptor propertyDescriptor = iterator.next();
            TypeDescriptor typeDescriptor = beanWrapper.getPropertyTypeDescriptor(propertyDescriptor.getName());
            Class<?> classType = typeDescriptor.getType();

            DDI ddiAnnotation = typeDescriptor.getAnnotation(DDI.class);
            if (ddiAnnotation != null) {
                Expression expression = new SpelExpressionParser().parseExpression(ddiAnnotation.field());

                EvaluationContext context = new StandardEvaluationContext();
                context.setVariable("index", ddiIndex);

                if (String.class.isAssignableFrom(classType)) { //todo: || other simple types
                    beanWrapper.setPropertyValue(propertyDescriptor.getName(),
                            expression.getValue(context, ddiItemInstance, classType));
                }

                else if (List.class.isAssignableFrom(classType)) {
                    Collection<?> ddiCollection = expression.getValue(context, ddiItemInstance, Collection.class);
                    Collection<Object> modelCollection = null;
                    try {
                        modelCollection = (Collection<Object>) propertyDescriptor.getReadMethod().invoke(modelItemInstance);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    for (Object ddiItemInstance2 : ddiCollection) {
                        try {
                            Object modelItemInstance2 = beanWrapper.getPropertyTypeDescriptor(propertyDescriptor.getName())
                                    .getResolvableType().getGeneric(0).getRawClass()
                                    .getDeclaredConstructor().newInstance();
                            //
                            modelCollection.add(modelItemInstance2);
                            //
                            recursiveMapping(modelItemInstance2, ddiItemInstance2);
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                    }
                }

                else {
                    try {
                        Object modelInstance2 = typeDescriptor.getType().getDeclaredConstructor().newInstance();
                        recursiveMapping(modelInstance2, expression.getValue(context, ddiItemInstance));
                        beanWrapper.setPropertyValue(propertyDescriptor.getName(), modelInstance2);
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
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
