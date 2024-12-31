package fr.insee.eno.core.converter;

import fr.insee.eno.core.exceptions.technical.ConversionException;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.question.*;
import fr.insee.pogues.model.DatatypeTypeEnum;
import fr.insee.pogues.model.QuestionType;
import fr.insee.pogues.model.ResponseType;

class PoguesSingleResponseQuestionConversion {

    private PoguesSingleResponseQuestionConversion() {}

    static EnoObject instantiateFrom(QuestionType poguesQuestion) {
        assert poguesQuestion.getResponse().size() == 1;
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

}
