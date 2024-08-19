package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.lunatic.model.flat.InputNumber;
import fr.insee.lunatic.model.flat.LabelType;
import fr.insee.lunatic.model.flat.LabelTypeEnum;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LunaticInputNumberDescriptionTest {

    @Test
    void noUnitNoDecimals() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        InputNumber inputNumber = new InputNumber();
        inputNumber.setMin(0d);
        inputNumber.setMax(100d);
        inputNumber.setDecimals(BigInteger.ZERO);
        lunaticQuestionnaire.getComponents().add(inputNumber);
        //
        LunaticInputNumberDescription processing = new LunaticInputNumberDescription(EnoParameters.Language.FR);
        processing.apply(lunaticQuestionnaire);
        //
        LabelType description = lunaticQuestionnaire.getComponents().getFirst().getDescription();
        assertEquals("Format attendu : un nombre entre 0 et 100", description.getValue());
        assertEquals(LabelTypeEnum.TXT, description.getType());
    }

    @Test
    void withUnitAndDecimal() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        InputNumber inputNumber = new InputNumber();
        inputNumber.setMin(0d);
        inputNumber.setMax(10d);
        inputNumber.setDecimals(BigInteger.ONE);
        inputNumber.setUnit("€");
        lunaticQuestionnaire.getComponents().add(inputNumber);
        //
        LunaticInputNumberDescription processing = new LunaticInputNumberDescription(EnoParameters.Language.FR);
        processing.apply(lunaticQuestionnaire);
        //
        LabelType description = lunaticQuestionnaire.getComponents().getFirst().getDescription();
        assertEquals("Format attendu : un nombre en € entre 0,0 et 10,0", description.getValue());
        assertEquals(LabelTypeEnum.TXT, description.getType());
    }

    @Test
    void withUnitNoDecimal() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        InputNumber inputNumber = new InputNumber();
        inputNumber.setMin(20d);
        inputNumber.setMax(1000d);
        inputNumber.setDecimals(BigInteger.ZERO);
        inputNumber.setUnit("k€");
        lunaticQuestionnaire.getComponents().add(inputNumber);
        //
        LunaticInputNumberDescription processing = new LunaticInputNumberDescription(EnoParameters.Language.FR);
        processing.apply(lunaticQuestionnaire);
        //
        LabelType description = lunaticQuestionnaire.getComponents().getFirst().getDescription();
        assertEquals("Format attendu : un nombre en k€ entre 20 et 1 000", description.getValue());
        assertEquals(LabelTypeEnum.TXT, description.getType());
    }

    @Test
    void negativeMin() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        InputNumber inputNumber = new InputNumber();
        inputNumber.setMin(-100d);
        inputNumber.setMax(100d);
        inputNumber.setDecimals(BigInteger.ZERO);
        lunaticQuestionnaire.getComponents().add(inputNumber);
        //
        LunaticInputNumberDescription processing = new LunaticInputNumberDescription(EnoParameters.Language.FR);
        processing.apply(lunaticQuestionnaire);
        //
        LabelType description = lunaticQuestionnaire.getComponents().getFirst().getDescription();
        assertEquals("Format attendu : un nombre entre -100 et 100", description.getValue());
        assertEquals(LabelTypeEnum.TXT, description.getType());
    }

    @Test
    void highBoundValue() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        InputNumber inputNumber = new InputNumber();
        inputNumber.setMin(0d);
        inputNumber.setMax(10_000_000_000d);
        inputNumber.setDecimals(BigInteger.ZERO);
        inputNumber.setUnit("€");
        lunaticQuestionnaire.getComponents().add(inputNumber);
        //
        LunaticInputNumberDescription processing = new LunaticInputNumberDescription(EnoParameters.Language.FR);
        processing.apply(lunaticQuestionnaire);
        //
        LabelType description = lunaticQuestionnaire.getComponents().getFirst().getDescription();
        assertEquals("Format attendu : un nombre en € entre 0 et 10 000 000 000",
                description.getValue());
        assertEquals(LabelTypeEnum.TXT, description.getType());
    }

}
