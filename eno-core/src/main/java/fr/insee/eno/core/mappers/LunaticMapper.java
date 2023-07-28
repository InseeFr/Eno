package fr.insee.eno.core.mappers;

import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.converter.LunaticConverter;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.utils.EnoSpelEngine;
import fr.insee.lunatic.model.flat.Questionnaire;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Mapper between Eno model and Lunatic questionnaire
 * (could be refactored as a general model to out format later).
 */
@Slf4j
public class LunaticMapper extends Mapper {

    public LunaticMapper() {
        this.format = Format.LUNATIC;
        spelEngine = new EnoSpelEngine(format);
    }

    /**
     * Fill the given Lunatic questionnaire with the content of the Eno questionnaire.
     * @param enoQuestionnaire A fulfilled Eno questionnaire.
     * @param lunaticQuestionnaire An empty Lunatic questionnaire.
     */
    public void mapQuestionnaire(EnoQuestionnaire enoQuestionnaire, Questionnaire lunaticQuestionnaire) {
        log.info("Starting mapping between Eno model and Lunatic questionnaire");
        spelEngine.resetContext();
        recursiveMapping(enoQuestionnaire, lunaticQuestionnaire);
        log.info("Finished mapping between Eno model and Lunatic questionnaire");
    }

    public void mapEnoObject(EnoObject enoObject, Object lunaticObject) {
        compatibilityCheck(lunaticObject, enoObject);
        spelEngine.resetContext();
        recursiveMapping(enoObject, lunaticObject);
    }

    private void recursiveMapping(EnoObject enoObject, Object lunaticObject) {

        log.debug("Start mapping for "+lunaticObject.getClass().getSimpleName()
                +" with model context type '"+enoObject.getClass().getSimpleName()+"'");

        // Use Spring BeanWrapper to iterate on property descriptors of the model object
        BeanWrapper beanWrapper = new BeanWrapperImpl(enoObject);
        for (Iterator<PropertyDescriptor> iterator = propertyDescriptorIterator(beanWrapper); iterator.hasNext();) {
            PropertyDescriptor propertyDescriptor = iterator.next();

            // Map property
            propertyMapping(enoObject, lunaticObject, beanWrapper, propertyDescriptor);
        }
    }

    public void propertyMapping(EnoObject enoObject, Object lunaticObject, BeanWrapper beanWrapper, PropertyDescriptor propertyDescriptor) {

        // Local variable used for logging purposes
        Class<?> modelContextType = enoObject.getClass();

        // Property name
        String propertyName = propertyDescriptor.getName();

        // Spring TypeDescriptor of the current property descriptor (several usages below)
        TypeDescriptor typeDescriptor = beanWrapper.getPropertyTypeDescriptor(propertyDescriptor.getName());
        assert typeDescriptor != null;

        // Identify the class type of the property
        Class<?> classType = propertyDescriptor.getPropertyType();

        // Use the Spring type descriptor to get the Lunatic annotation if any
        Lunatic lunaticAnnotation = typeDescriptor.getAnnotation(Lunatic.class);
        if (lunaticAnnotation != null) {

            log.debug("Processing property '"+ propertyName
                    +"' of class '"+ modelContextType.getSimpleName()+"'");

            // Instantiate a Spring expression with the annotation content
            Expression expression = new SpelExpressionParser().parseExpression(lunaticAnnotation.value());

            if (isSimpleType(classType)) {
                simpleTypeMapping(lunaticObject, beanWrapper, propertyName, expression);
            }

            else if (EnoObject.class.isAssignableFrom(classType)) {
                complexTypeMapping(lunaticObject, beanWrapper, propertyName, expression);
            }

            else if (Collection.class.isAssignableFrom(classType)) {
                collectionMapping(enoObject, lunaticObject, propertyDescriptor, expression);
            }

            else {
                unknownTypeException(classType, propertyDescriptor, modelContextType);
            }

        }
    }

    // Note: not useful to put debug log in below methods, 'in' mapper already gives enough.

    private void simpleTypeMapping(Object lunaticObject, BeanWrapper beanWrapper, String propertyName, Expression expression) {
        // Read the value in the Eno model class
        Object modelValue = beanWrapper.getPropertyValue(propertyName);
        if (modelValue != null) {
            // Evaluate the mapping expression that set the value
            spelEngine.resetContext(); // Not sure if it is required but feels safe
            spelEngine.getContext().setVariable("param", modelValue);
            spelEngine.evaluate(expression, lunaticObject, beanWrapper.getWrappedClass(), propertyName);
        }
    }

    private void complexTypeMapping(Object lunaticObject, BeanWrapper beanWrapper, String propertyName, Expression expression) {
        // Get the model object
        EnoObject enoObject2 = (EnoObject) beanWrapper.getPropertyValue(propertyName);
        if (enoObject2 != null) {
            // Instantiate the Lunatic target object
            Object lunaticObject2 = LunaticConverter.instantiateFromEnoObject(enoObject2);
            //
            if (lunaticObject2 == null) { // TODO: do this better using Optional (or even better: create a "conversion result" class)
                return;
            }
            // Evaluate the mapping expression that set the created instance in the Lunatic object attribute
            spelEngine.resetContext();
            spelEngine.getContext().setVariable("param", lunaticObject2); //TODO: other keyword for new instances setter?
            spelEngine.evaluate(expression, lunaticObject, beanWrapper.getWrappedClass(), propertyName);
            // Recursive call of the mapper to dive into this object
            recursiveMapping(enoObject2, lunaticObject2);
        }
    }

    private void collectionMapping(EnoObject enoObject, Object lunaticObject, PropertyDescriptor propertyDescriptor, Expression expression) {
        // Get the Lunatic collection to be filled
        @SuppressWarnings("unchecked")
        List<Object> lunaticCollection = (List<Object>) spelEngine.evaluateToList(
                expression, lunaticObject, enoObject.getClass(), propertyDescriptor.getName());
        assert lunaticCollection != null : "Lunatic collections are expected to be initialized.";
        // Get the model collection
        Collection<Object> enoCollection = readCollection(propertyDescriptor, enoObject);
        assert enoCollection != null : "Model collections are expected to be initialized.";
        if (! enoCollection.isEmpty()) {
            // Content type of the model collection
            Class<?> modelContentType = enoCollection.iterator().next().getClass();
            // Collection of simple type
            if (isSimpleType(modelContentType)) {
                lunaticCollection.addAll(enoCollection);
            }
            // Collection of complex type
            else if (EnoObject.class.isAssignableFrom(modelContentType)) {
                // Iterate on the model collection
                for (Object enoObject2 : enoCollection) {
                    // Instantiate corresponding Lunatic object
                    Object lunaticObject2 = LunaticConverter.instantiateFromEnoObject(enoObject2);
                    //
                    if (lunaticObject2 == null) { // TODO: do this better using Optional (or even better: create a "conversion result" class)
                        continue;
                    }
                    // Add it in the Lunatic collection
                    lunaticCollection.add(lunaticObject2);
                    // Recursive call on these instances
                    recursiveMapping((EnoObject) enoObject2, lunaticObject2);
                }
            }
        }
    }

}
