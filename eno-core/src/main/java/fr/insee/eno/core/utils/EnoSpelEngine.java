package fr.insee.eno.core.utils;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
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
    private final EvaluationContext context;

    public EnoSpelEngine(Format format) {
        this.format = format;
        context = new StandardEvaluationContext();
    }

    public Object evaluate(Expression expression, Object rootObject,
                           Class<?> enoContextType, String propertyName) {
        return evaluate(expression, rootObject, Object.class, enoContextType, propertyName);
    }

    public List<?> evaluateToList(Expression expression, Object rootObject,
                                  Class<?> enoContextType, String propertyName) {
        return evaluate(expression, rootObject, List.class, enoContextType, propertyName);
    }

    public <T> T evaluate(Expression expression, Object rootObject, Class<T> desiredResultType,
                           Class<?> enoContextType, String propertyName) {
        try {
            return expression.getValue(context, rootObject, desiredResultType);
        } catch (Exception e) {
            log.error("Evaluation of following SpEL expression failed:");
            log.error(expression.getExpressionString());
            throw new MappingException(errorMessage(rootObject, enoContextType, propertyName), e);
        }
    }

    private String errorMessage(Object rootObject, Class<?> enoContextType, String propertyName) {
        return String.format(
                "SpEL mapping expression defined on property '%s', for format %s, in %s, failed on object %s",
                propertyName, format, enoContextType, objectToString(rootObject));
    }

    private String objectToString(Object rootObject) {
        return Format.DDI.equals(format) ? DDIUtils.ddiToString(rootObject) : rootObject.toString();
    }

}
