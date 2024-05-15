package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.lunatic.model.flat.InputNumber;
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
        assertEquals("Format attendu : un nombre entre 0 et 100",
                lunaticQuestionnaire.getComponents().getFirst().getDescription().getValue());
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
        assertEquals("Format attendu : un nombre en € entre 0,0 et 10,0",
                lunaticQuestionnaire.getComponents().getFirst().getDescription().getValue());
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
        assertEquals("Format attendu : un nombre en k€ entre 20 et 1000",
                lunaticQuestionnaire.getComponents().getFirst().getDescription().getValue());
    }

}
