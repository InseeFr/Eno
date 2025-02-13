package fr.insee.eno.core.converter;

import fr.insee.eno.core.exceptions.technical.ConversionException;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.question.ComplexMultipleChoiceQuestion;
import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.eno.core.model.question.SimpleMultipleChoiceQuestion;
import fr.insee.eno.core.model.question.TableQuestion;
import fr.insee.pogues.model.*;

class PoguesMultipleChoiceQuestionConverter {

    private PoguesMultipleChoiceQuestionConverter() {}

    static EnoObject instantiateFrom(QuestionType poguesQuestion) {
        QuestionTypeEnum questionType = poguesQuestion.getQuestionType();
        if (QuestionTypeEnum.MULTIPLE_CHOICE.equals(questionType))
            return convertMultipleChoice(poguesQuestion);
        if (QuestionTypeEnum.TABLE.equals(questionType))
            return convertTable(poguesQuestion);
        throw new MappingException("Unexpected multiple response question of type " + questionType + ".");
    }

    private static EnoObject convertMultipleChoice(QuestionType poguesQuestion) {
        if (areAllResponsesBoolean(poguesQuestion))
            return new SimpleMultipleChoiceQuestion();
        return new ComplexMultipleChoiceQuestion();
    }
    private static boolean areAllResponsesBoolean(QuestionType poguesQuestion) {
        return poguesQuestion.getResponse().stream()
                .map(ResponseType::getDatatype)
                .map(DatatypeType::getTypeName)
                .allMatch(DatatypeTypeEnum.BOOLEAN::equals);
    }

    private static EnoObject convertTable(QuestionType poguesQuestion) {
        if (isStaticTable(poguesQuestion))
            return new TableQuestion();
        if (isDynamicTable(poguesQuestion))
            return new DynamicTableQuestion();
        throw new ConversionException("Unable to convert Pogues table '" + poguesQuestion.getId() + "'.");
    }
    private static boolean isStaticTable(QuestionType poguesQuestion) {
        // A Pogues table is a static table if all of its "dimensions" are non-dynamic.
        return poguesQuestion.getResponseStructure().getDimension().getFirst().getDynamic().equals("NON_DYNAMIC");
    }
    private static boolean isDynamicTable(QuestionType poguesQuestion) {
        return poguesQuestion.getResponseStructure().getDimension().getFirst().getDynamic().equals("DYNAMIC_LENGTH");
    }

}
