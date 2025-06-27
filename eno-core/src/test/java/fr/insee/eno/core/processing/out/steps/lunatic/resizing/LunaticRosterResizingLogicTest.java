package fr.insee.eno.core.processing.out.steps.lunatic.resizing;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticLoopResolution;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticSortComponents;
import fr.insee.eno.core.processing.out.steps.lunatic.table.LunaticTableProcessing;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.variable.CollectedVariableType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LunaticRosterResizingLogicTest {

    @Test
    @DisplayName("Dynamic table with size expression: should have a resizing entry.")
    void unitTest1() {
        // Given
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        DynamicTableQuestion enoDynamicTable = new DynamicTableQuestion();
        enoDynamicTable.setId("dynamic-table-id");
        CalculatedExpression sizeExpression = new CalculatedExpression();
        sizeExpression.setValue("<foo expression>");
        sizeExpression.getBindingReferences().add(new BindingReference("foo-ref","FOO_COLLECTED_VARIABLE"));
        enoDynamicTable.setMaxSizeExpression(sizeExpression);
        enoQuestionnaire.getMultipleResponseQuestions().add(enoDynamicTable);

        Questionnaire lunaticQuestionnaire = new Questionnaire();
        RosterForLoop lunaticRoster = new RosterForLoop();
        lunaticRoster.setId("dynamic-table-id");
        BodyCell column1Component = new BodyCell();
        column1Component.setResponse(new ResponseType());
        column1Component.getResponse().setName("TABLE_VAR1");
        BodyCell column2Component = new BodyCell();
        column2Component.setResponse(new ResponseType());
        column2Component.getResponse().setName("TABLE_VAR2");
        lunaticRoster.getComponents().add(column1Component);
        lunaticRoster.getComponents().add(column2Component);
        lunaticQuestionnaire.getComponents().add(lunaticRoster);

        CollectedVariableType lunaticVariable = new CollectedVariableType();
        lunaticVariable.setName("FOO_COLLECTED_VARIABLE");
        lunaticQuestionnaire.getVariables().add(lunaticVariable);

        // When
        ResizingType lunaticResizing = new ResizingType();
        new LunaticRosterResizingLogic(lunaticQuestionnaire, enoQuestionnaire)
                .buildResizingEntries(lunaticRoster, lunaticResizing);
        lunaticQuestionnaire.setResizing(lunaticResizing);

        // Then
        assertEquals(0, lunaticResizing.countResizingEntries());
    }

    @Test
    @DisplayName("Dynamic table with size expression: should have no resizing entries.")
    void unitTest2() {
        // Given
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        DynamicTableQuestion enoDynamicTable = new DynamicTableQuestion();
        enoDynamicTable.setId("dynamic-table-id");
        enoDynamicTable.setMinLines(BigInteger.ONE);
        enoDynamicTable.setMaxLines(BigInteger.TEN);
        enoQuestionnaire.getMultipleResponseQuestions().add(enoDynamicTable);

        Questionnaire lunaticQuestionnaire = new Questionnaire();
        RosterForLoop lunaticRoster = new RosterForLoop();
        lunaticRoster.setId("dynamic-table-id");
        BodyCell column1Component = new BodyCell();
        column1Component.setResponse(new ResponseType());
        column1Component.getResponse().setName("TABLE_VAR1");
        BodyCell column2Component = new BodyCell();
        column2Component.setResponse(new ResponseType());
        column2Component.getResponse().setName("TABLE_VAR2");
        lunaticRoster.getComponents().add(column1Component);
        lunaticRoster.getComponents().add(column2Component);
        lunaticQuestionnaire.getComponents().add(lunaticRoster);

        // When
        ResizingType lunaticResizing = new ResizingType();
        new LunaticRosterResizingLogic(lunaticQuestionnaire, enoQuestionnaire)
                .buildResizingEntries(lunaticRoster, lunaticResizing);

        // Then
        assertEquals(0, lunaticResizing.countResizingEntries());
    }

    @Test
    void integrationTest() throws DDIParsingException {
        // Given
        EnoQuestionnaire enoQuestionnaire = DDIToEno.fromInputStream(
                LunaticAddResizingTest.class.getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-dynamic-table-size.xml"))
                .transform(EnoParameters.of(Context.BUSINESS, ModeParameter.CAWI));
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
        new LunaticSortComponents(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticLoopResolution(enoQuestionnaire).apply(lunaticQuestionnaire);
        new LunaticTableProcessing(enoQuestionnaire).apply(lunaticQuestionnaire);

        // When
        new LunaticAddResizing(enoQuestionnaire).apply(lunaticQuestionnaire);

        // Then
        ResizingType lunaticResizing = lunaticQuestionnaire.getResizing();
        assertEquals(0, lunaticResizing.countResizingEntries());
    }

    @Test
    void nonCollectedColumnCase_functionalTest() throws DDIParsingException {
        // Given
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.HOUSEHOLD, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        InputStream ddiInputStream = this.getClass().getClassLoader().getResourceAsStream(
                "functional/ddi/ddi-m5o3qhu0.xml"); // DDI with non-collected column within a dynamic table
        // When + then
        DDIToLunatic ddiToLunatic = DDIToLunatic.fromInputStream(ddiInputStream);
        assertDoesNotThrow(() -> ddiToLunatic.transform(enoParameters));
    }

}
