package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.processing.out.steps.lunatic.table.LunaticTableProcessing;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.variable.CalculatedVariableType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LunaticShapeFromTest {

    @Test
    void unitTest() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.setId("questionnaire-id");
        Loop loop = new Loop();
        loop.setId("loop-id");
        loop.setComponentType(ComponentTypeEnum.LOOP);
        Input input = new Input();
        input.setId("input-id");
        input.setComponentType(ComponentTypeEnum.INPUT);
        input.setResponse(new ResponseType());
        input.getResponse().setName("FOO");
        CheckboxGroup checkboxGroup = new CheckboxGroup();
        checkboxGroup.setId("checkbox-group-id");
        checkboxGroup.setComponentType(ComponentTypeEnum.CHECKBOX_GROUP);
        checkboxGroup.getResponses().add(new ResponsesCheckboxGroup());
        checkboxGroup.getResponses().add(new ResponsesCheckboxGroup());
        checkboxGroup.getResponses().get(0).setResponse(new ResponseType());
        checkboxGroup.getResponses().get(0).getResponse().setName("BAR1");
        checkboxGroup.getResponses().get(1).setResponse(new ResponseType());
        checkboxGroup.getResponses().get(1).getResponse().setName("BAR2");
        loop.getComponents().add(input);
        loop.getComponents().add(checkboxGroup);
        lunaticQuestionnaire.getComponents().add(loop);
        //
        CalculatedVariableType calculatedVariable = new CalculatedVariableType();
        calculatedVariable.setIterationReference("loop-id");
        lunaticQuestionnaire.getVariables().add(calculatedVariable);

        //
        new LunaticShapeFrom().apply(lunaticQuestionnaire);

        //
        assertEquals(List.of("FOO", "BAR1", "BAR2"), calculatedVariable.getShapeFromList());
    }

    @Test
    void integrationTest_fromDDI() throws DDIParsingException {
        //
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-dimensions.xml"),
                EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC));
        LunaticMapper lunaticMapper = new LunaticMapper();
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
        new LunaticSortComponents(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticLoopResolution(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticTableProcessing(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticVariablesDimension(enoQuestionnaire).apply(lunaticQuestionnaire);
        //
        new LunaticShapeFrom().apply(lunaticQuestionnaire);
        //
        assertNotNull(lunaticQuestionnaire);
        CalculatedVariableType calc1 = getCalculatedFromName("CALC1", lunaticQuestionnaire);
        CalculatedVariableType calc2 = getCalculatedFromName("CALC2", lunaticQuestionnaire);
        CalculatedVariableType calc3 = getCalculatedFromName("CALC3", lunaticQuestionnaire);
        assertTrue(calc1.getShapeFromList().isEmpty());
        assertEquals(List.of("Q21"), calc2.getShapeFromList());
        assertEquals(List.of("Q311", "Q312"), calc3.getShapeFromList());
    }

    private static CalculatedVariableType getCalculatedFromName(String name, Questionnaire lunaticQuestionnaire) {
        return lunaticQuestionnaire.getVariables().stream()
                .filter(CalculatedVariableType.class::isInstance)
                .map(CalculatedVariableType.class::cast)
                .filter(calculatedVariableType -> name.equals(calculatedVariableType.getName()))
                .findAny().orElse(null);
    }

}
