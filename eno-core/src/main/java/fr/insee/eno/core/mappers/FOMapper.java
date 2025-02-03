package fr.insee.eno.core.mappers;

import fr.insee.eno.core.annotations.FO;
import fr.insee.eno.core.converter.FOConverter;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.model.fo.Questionnaire;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.w3c.dom.Document;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Mapper between Eno model and FO questionnaire
 * (could be refactored as a general model to out format later).
 */
@Slf4j
public class FOMapper extends Mapper {

    public FOMapper() {
        super(Format.FO);
    }

    /**
     * Fill the given FO questionnaire with the content of the Eno questionnaire.
     * @param enoQuestionnaire A fulfilled Eno questionnaire.
     * @param foQuestionnaire An empty FO questionnaire.
     */
    public void mapQuestionnaire(EnoQuestionnaire enoQuestionnaire, Questionnaire foQuestionnaire) {
        log.info("Starting mapping between Eno model and FO questionnaire");
        spelEngine.resetContext();
        recursiveMapping(enoQuestionnaire, foQuestionnaire);
        log.info("Finished mapping between Eno model and FO questionnaire");
    }

    public void mapEnoObject(EnoObject enoObject, Object foObject) {
        compatibilityCheck(foObject, enoObject);
        spelEngine.resetContext();
        recursiveMapping(enoObject, foObject);
    }

    private void recursiveMapping(EnoObject enoObject, Object foObject) {

        log.debug("Start mapping for "+foObject.getClass().getSimpleName()
                +" with model context type '"+enoObject.getClass().getSimpleName()+"'");

        // Use Spring BeanWrapper to iterate on property descriptors of the model object
        BeanWrapper beanWrapper = new BeanWrapperImpl(enoObject);
        for (Iterator<PropertyDescriptor> iterator = propertyDescriptorIterator(beanWrapper); iterator.hasNext();) {
            PropertyDescriptor propertyDescriptor = iterator.next();

            // Map property
            propertyMapping(enoObject, foObject, beanWrapper, propertyDescriptor);
        }
    }

    public void propertyMapping(EnoObject enoObject, Object foObject, BeanWrapper beanWrapper, PropertyDescriptor propertyDescriptor) {

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
        FO foAnnotation = typeDescriptor.getAnnotation(FO.class);
        if (foAnnotation != null) {

            log.debug("Processing property '"+ propertyName
                    +"' of class '"+ modelContextType.getSimpleName()+"'");

            // Instantiate a Spring expression with the annotation content
            Expression expression = new SpelExpressionParser().parseExpression(foAnnotation.value());

            if (isSimpleType(classType)) {
                simpleTypeMapping(foObject, beanWrapper, propertyName, expression);
            }

            else if (EnoObject.class.isAssignableFrom(classType)) {
                complexTypeMapping(foObject, beanWrapper, propertyName, expression);
            }

            else if (Collection.class.isAssignableFrom(classType)) {
                collectionMapping(enoObject, foObject, propertyDescriptor, expression);
            }

            else {
                unknownTypeException(classType, propertyDescriptor, modelContextType);
            }

        }
    }

    // Note: not useful to put debug log in below methods, 'in' mapper already gives enough.

    private void simpleTypeMapping(Object foObject, BeanWrapper beanWrapper, String propertyName, Expression expression) {
        // Read the value in the Eno model class
        Object modelValue = beanWrapper.getPropertyValue(propertyName);
        if (modelValue != null) {
            log.info("ModelVlaue {}",modelValue);
            // Evaluate the mapping expression that set the value
            spelEngine.resetContext(); // Not sure if it is required but feels safe
            spelEngine.getContext().setVariable("param", modelValue);
            spelEngine.evaluate(expression, foObject, beanWrapper.getWrappedClass(), propertyName);
        }
    }

    private void complexTypeMapping(Object foObject, BeanWrapper beanWrapper, String propertyName, Expression expression) {
        // Get the model object
        EnoObject enoObject2 = (EnoObject) beanWrapper.getPropertyValue(propertyName);
        if (enoObject2 != null) {
            // Instantiate the FO target object
            Object foObject2 = FOConverter.instantiateFromEnoObject(enoObject2);
            //
            if (foObject2 == null) { // TODO: do this better using Optional (or even better: create a "conversion result" class)
                return;
            }
            // Evaluate the mapping expression that set the created instance in the FO object attribute
            spelEngine.resetContext();
            spelEngine.getContext().setVariable("param", foObject2); //TODO: other keyword for new instances setter?
            spelEngine.evaluate(expression, foObject, beanWrapper.getWrappedClass(), propertyName);
            // Recursive call of the mapper to dive into this object
            recursiveMapping(enoObject2, foObject2);
        }
    }

    private void collectionMapping(EnoObject enoObject, Object foObject, PropertyDescriptor propertyDescriptor, Expression expression) {
        // Get the FO collection to be filled
        @SuppressWarnings("unchecked")
        List<Object> foCollection = (List<Object>) spelEngine.evaluateToList(
                expression, foObject, enoObject.getClass(), propertyDescriptor.getName());
        assert foCollection != null : "FO collections are expected to be initialized.";
        // Get the model collection
        Collection<Object> enoCollection = readCollection(propertyDescriptor, enoObject);
        assert enoCollection != null : "Model collections are expected to be initialized.";
        if (! enoCollection.isEmpty()) {
            // Content type of the model collection
            Class<?> modelContentType = enoCollection.iterator().next().getClass();
            // Collection of simple type
            if (isSimpleType(modelContentType)) {
                foCollection.addAll(enoCollection);
            }
            // Collection of complex type
            else if (EnoObject.class.isAssignableFrom(modelContentType)) {
                // Iterate on the model collection
                for (Object enoObject2 : enoCollection) {
                    // Instantiate corresponding FO object
                    Object foObject2 = FOConverter.instantiateFromEnoObject(enoObject2);
                    //
                    if (foObject2 == null) { // TODO: do this better using Optional (or even better: create a "conversion result" class)
                        continue;
                    }
                    // Add it in the FO collection
                    foCollection.add(foObject2);
                    // Recursive call on these instances
                    recursiveMapping((EnoObject) enoObject2, foObject2);
                }
            }
        }
    }

}
