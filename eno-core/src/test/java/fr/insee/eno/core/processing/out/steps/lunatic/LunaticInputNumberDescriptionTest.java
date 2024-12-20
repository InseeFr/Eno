package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.processing.out.steps.lunatic.table.LunaticTableProcessing;
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
        inputNumber.setUnit(new LabelType());
        inputNumber.getUnitLabel().setValue("\"€\"");
        lunaticQuestionnaire.getComponents().add(inputNumber);
        //
        LunaticInputNumberDescription processing = new LunaticInputNumberDescription(EnoParameters.Language.FR);
        processing.apply(lunaticQuestionnaire);
        //
        LabelType description = lunaticQuestionnaire.getComponents().getFirst().getDescription();
        assertEquals("\"Format attendu : un nombre en \" || \"€\" || \" entre 0,0 et 10,0\"",
                description.getValue());
        assertEquals(LabelTypeEnum.VTL, description.getType());
    }

    @Test
    void withUnitNoDecimal() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        InputNumber inputNumber = new InputNumber();
        inputNumber.setMin(20d);
        inputNumber.setMax(1000d);
        inputNumber.setDecimals(BigInteger.ZERO);
        inputNumber.setUnit(new LabelType());
        inputNumber.getUnitLabel().setValue("\"k€\"");
        lunaticQuestionnaire.getComponents().add(inputNumber);
        //
        LunaticInputNumberDescription processing = new LunaticInputNumberDescription(EnoParameters.Language.FR);
        processing.apply(lunaticQuestionnaire);
        //
        LabelType description = lunaticQuestionnaire.getComponents().getFirst().getDescription();
        assertEquals("\"Format attendu : un nombre en \" || \"k€\" || \" entre 20 et 1 000\"",
                description.getValue());
        assertEquals(LabelTypeEnum.VTL, description.getType());
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
        inputNumber.setUnit(new LabelType());
        inputNumber.getUnitLabel().setValue("\"€\"");
        lunaticQuestionnaire.getComponents().add(inputNumber);
        //
        LunaticInputNumberDescription processing = new LunaticInputNumberDescription(EnoParameters.Language.FR);
        processing.apply(lunaticQuestionnaire);
        //
        LabelType description = lunaticQuestionnaire.getComponents().getFirst().getDescription();
        assertEquals("\"Format attendu : un nombre en \" || \"€\" || \" entre 0 et 10 000 000 000\"",
                description.getValue());
        assertEquals(LabelTypeEnum.VTL, description.getType());
    }

    @Test
    void integrationTest() throws DDIParsingException {
        // Given
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-dynamic-unit.xml"),
                EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI));
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        new LunaticMapper().mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
        new LunaticSortComponents(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticTableProcessing(enoQuestionnaire).apply(lunaticQuestionnaire);

        // When
        new LunaticInputNumberDescription(EnoParameters.Language.FR).apply(lunaticQuestionnaire);

        // Then
        InputNumber inputNumber1 = (InputNumber) lunaticQuestionnaire.getComponents().get(1);
        InputNumber inputNumber2 = (InputNumber) lunaticQuestionnaire.getComponents().get(3);
        assertEquals("\"Format attendu : un nombre en \" || \"€\" || \" entre 1 et 10\"",
                inputNumber1.getDescription().getValue());
        assertEquals("\"Format attendu : un nombre en \" || WHICH_UNIT  || \" entre 1 et 10\"",
                inputNumber2.getDescription().getValue());
        assertEquals(LabelTypeEnum.VTL, inputNumber1.getDescription().getType());
        assertEquals(LabelTypeEnum.VTL, inputNumber2.getDescription().getType());
    }

}
