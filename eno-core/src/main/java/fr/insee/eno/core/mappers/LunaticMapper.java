package fr.insee.eno.core.mappers;

import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.converter.LunaticConverter;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.lunatic.model.flat.Questionnaire;
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

/**
 * Mapper between Eno model and Lunatic questionnaire
 * (could be refactored as a general model to out format later).
 */
@Slf4j
public class LunaticMapper extends Mapper {

    /**
     * Fill the given Lunatic questionnaire with the content of the Eno questionnaire.
     * @param enoQuestionnaire A fulfilled Eno questionnaire.
     * @param lunaticQuestionnaire An empty Lunatic questionnaire.
     */
    public static void map(EnoQuestionnaire enoQuestionnaire, Questionnaire lunaticQuestionnaire) {
        log.info("Starting mapping between Eno model and Lunatic questionnaire");
        recursiveMapping(enoQuestionnaire, lunaticQuestionnaire);
        log.info("Finished mapping between Eno model and Lunatic questionnaire");
    }

    private static void recursiveMapping(Object enoInstance, Object lunaticInstance) {

        BeanWrapper beanWrapper = new BeanWrapperImpl(enoInstance);

        for (Iterator<PropertyDescriptor> iterator = propertyDescriptorIterator(beanWrapper); iterator.hasNext();) {

            PropertyDescriptor propertyDescriptor = iterator.next();
            TypeDescriptor typeDescriptor = beanWrapper.getPropertyTypeDescriptor(propertyDescriptor.getName());
            assert typeDescriptor != null;
            Class<?> classType = propertyDescriptor.getPropertyType();

            Lunatic lunaticAnnotation = typeDescriptor.getAnnotation(Lunatic.class);
            if(lunaticAnnotation != null) {
                Expression expression = new SpelExpressionParser().parseExpression(lunaticAnnotation.field());

                if (isSimpleType(classType)) {
                    Object modelValue = beanWrapper.getPropertyValue(propertyDescriptor.getName());
                    if (modelValue != null) {
                        EvaluationContext context = new StandardEvaluationContext();
                        context.setVariable("param", modelValue);
                        expression.getValue(context, lunaticInstance);
                    }
                }

                else if (Collection.class.isAssignableFrom(classType)) {
                    Collection<Object> lunaticCollection = expression.getValue(lunaticInstance, Collection.class);
                    Collection<Object> enoCollection = null;
                    try {
                        enoCollection = (Collection<Object>) propertyDescriptor.getReadMethod().invoke(enoInstance);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    for (Object enoInstance2 : enoCollection) {
                        //Object lunaticInstance2 = lunaticAnnotation.instanceType().getDeclaredConstructor().newInstance();
                        Object lunaticInstance2 = LunaticConverter.instantiateFromEnoObject(enoInstance2);
                        lunaticCollection.add(lunaticInstance2);
                        recursiveMapping(enoInstance2, lunaticInstance2);
                    }
                }

                else {
                    //
                    Object enoInstance2 = beanWrapper.getPropertyValue(propertyDescriptor.getName());
                    if (enoInstance2 != null) {
                        //
                        Object lunaticInstance2 = LunaticConverter.instantiateFromEnoObject(enoInstance2);
                        //
                        EvaluationContext context = new StandardEvaluationContext();
                        context.setVariable("param", lunaticInstance2); //TODO: other keyword for new instances setter?
                        expression.getValue(context, lunaticInstance);
                        //
                        recursiveMapping(enoInstance2, lunaticInstance2);
                    }
                }

            }
        }
    }

}
