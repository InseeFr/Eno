package fr.insee.eno.core.converter;

import fr.insee.eno.core.exceptions.technical.ConversionException;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.pogues.model.QuestionType;
import fr.insee.pogues.model.QuestionTypeEnum;
import fr.insee.pogues.model.VariableType;

public class PoguesConverter implements InConverter {

    @Override
    public EnoObject convertToEno(Object poguesObject, Class<?> enoType) {
        if (poguesObject instanceof VariableType poguesVariable)
            return PoguesVariableConversion.instantiateFrom(poguesVariable);
        if (poguesObject instanceof QuestionType poguesQuestion
                && QuestionTypeEnum.SIMPLE.equals(poguesQuestion.getQuestionType()))
            return PoguesSingleResponseQuestionConversion.instantiateFrom(poguesQuestion);
        throw new ConversionException("Eno conversion for Pogues type " + poguesObject.getClass() + " not implemented.");
    }

}
