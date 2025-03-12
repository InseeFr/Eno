package fr.insee.eno.core.utils;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.parameter.Format;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.List;

/** Encapsulation of methods using the Spring Element Language (SpEL).
 * Contains methods to evaluate SpEL expressions while keeping a precise stack trace.
 * Note: The class name explicitly mentions Eno to avoid confusion with Spring classes. */
@Slf4j
public class EnoSpelEngine {

    private final Format format;
    
    @Getter
    private EvaluationContext context;

    public EnoSpelEngine(Format format) {
        this.format = format;
        context = new StandardEvaluationContext();
    }

    /** Clears bindings in the context by setting a new evaluation context. */
    public void resetContext() {
        context = new StandardEvaluationContext();
    }

    /* TODO: more precise parameters
        - Class<? extends EnoObject> for enoContextType
        - PropertyDescriptor propertyDescriptor object instead of String propertyName
        (needs some refactor in mappers to do so)
     */

    /**
     * Evaluates a SpEL expression on the given object. See mapper classes for usages.
     * @param expression SpEL expression.
     * @param rootObject Object against which the expression will be evaluated.
     * @param enoContextType Eno's context type (for logging purposes).
     * @param propertyName Name of the property of the Eno-model on which the SpEL expression is defined.
     * @return The result of the SpEL expression.
     */
    public Object evaluate(Expression expression, Object rootObject,
                           Class<?> enoContextType, String propertyName) {
        return evaluate(expression, rootObject, Object.class, enoContextType, propertyName);
    }

    /**
     * Evaluates a SpEL expression that should return a list. See mapper classes for usages.
     * @param expression SpEL expression.
     * @param rootObject Object against which the expression will be evaluated.
     * @param enoContextType Eno's context type (for logging purposes).
     * @param propertyName Name of the property of the Eno-model on which the SpEL expression is defined.
     * @return The list result of the SpEL expression.
     */
    public List<?> evaluateToList(Expression expression, Object rootObject,
                                  Class<?> enoContextType, String propertyName) {
        return evaluate(expression, rootObject, List.class, enoContextType, propertyName);
    }

    /**
     * Evaluates a SpEL expression on the given object, that should return the given return type.
     * See mapper classes for usages.
     * @param expression SpEL expression.
     * @param rootObject Object against which the expression will be evaluated.
     * @param desiredResultType The type that should be returned when evaluating the expression.
     * @param enoContextType Eno's context type (for logging purposes).
     * @param propertyName Name of the property of the Eno-model on which the SpEL expression is defined.
     * @return The result of the SpEL expression.
     * @param <T> Generic return type.
     */
    public <T> T evaluate(Expression expression, Object rootObject, Class<T> desiredResultType,
                           Class<?> enoContextType, String propertyName) {
        try {
            return expression.getValue(context, rootObject, desiredResultType);
        } catch (Exception e) {
            log.error(errorMessage(rootObject, enoContextType, propertyName));
            log.error("Evaluation of following SpEL expression that failed:");
            log.error(expression.getExpressionString());
            // The cause is generally closer to what's actually happening here:
            throw new MappingException(e.getMessage(), e);
        }
    }

    /**
     * Generates the error message to be logged when a SpEL evaluation fails.
     * @param rootObject Object against which the expression is evaluated.
     * @param enoContextType Eno-model context when the expression has been called.
     * @param propertyName Eno-model property on which the SpEL expression is defined.
     * @return A descriptive error message.
     */
    private String errorMessage(Object rootObject, Class<?> enoContextType, String propertyName) {
        return String.format(
                "SpEL mapping expression defined on property '%s', for format %s, in %s, failed on object %s",
                propertyName, format, enoContextType, objectToString(rootObject));
    }

    /**
     * DDI objects with the current lib doesn't have a useful "toString" method.
     * If the SpEL engine is instantiated with DDI format, calls the adapted method to print the object.
     * Otherwise, "toString" method is called.
     * @param rootObject Any object.
     * @return String representation of the object.
     */
    private String objectToString(Object rootObject) {
        return Format.DDI.equals(format) ? DDIUtils.ddiToString(rootObject) : rootObject.toString();
    }

}
