package fr.insee.eno.core.mappers;

import datacollection33.QuestionItemType;
import fr.insee.eno.core.exceptions.technical.MethodBindingException;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
import org.springframework.expression.EvaluationContext;

import java.lang.reflect.Method;

/** Class to set context variables to be used by DDI mapper.
 * Reason for this class: Refactor -> Rename a class will not update mapping annotations,
 * but it will update strings in getMethod(...) calls. */
public class DDIBindings {

    // https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#expressions-ref-variables
    // https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#expressions-ref-functions

    public static void setMethods(EvaluationContext context) {

        try { //TODO: set all methods used in mapping here
            context.setVariable("convertDDIOutputFormat",
                    UniqueChoiceQuestion.class.getMethod("convertDDIOutputFormat", QuestionItemType.class));
        }

        catch (NoSuchMethodException e) {
            throw new MethodBindingException("Method not found.", e);
        }
    }

}
