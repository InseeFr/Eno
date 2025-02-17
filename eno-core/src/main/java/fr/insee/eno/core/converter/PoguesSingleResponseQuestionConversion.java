package fr.insee.eno.core.converter;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.question.*;
import fr.insee.pogues.model.*;

class PoguesSingleResponseQuestionConversion {

    private PoguesSingleResponseQuestionConversion() {}

    static EnoObject instantiateFrom(QuestionType poguesQuestion) {
        assert poguesQuestion.getResponse().size() == 1;
        QuestionTypeEnum questionType = poguesQuestion.getQuestionType();
        if (QuestionTypeEnum.SIMPLE.equals(questionType))
            return convertSimpleQuestion(poguesQuestion);
        if (QuestionTypeEnum.SINGLE_CHOICE.equals(questionType))
            return convertUniqueChoiceQuestion(poguesQuestion);
        if (QuestionTypeEnum.PAIRWISE.equals(questionType))
            return new PairwiseQuestion();
        throw new MappingException("Unexpected single response question of type " + questionType + ".");
    }

    private static EnoObject convertSimpleQuestion(QuestionType poguesQuestion) {
        ResponseType response = poguesQuestion.getResponse().getFirst();
        DatatypeTypeEnum datatype = response.getDatatype().getTypeName();
        return switch (datatype) {
            case TEXT -> new TextQuestion();
            case NUMERIC -> new NumericQuestion();
            case BOOLEAN -> new BooleanQuestion();
            case DATE -> new DateQuestion();
            case DURATION -> new DurationQuestion();
        };
    }

    private static EnoObject convertUniqueChoiceQuestion(QuestionType poguesQuestion) {
        if (poguesQuestion.getResponse().getFirst().getDatatype().getVisualizationHint()
                .equals(VisualizationHintEnum.SUGGESTER))
            return new SuggesterQuestion();
        return new UniqueChoiceQuestion();
    }

}
