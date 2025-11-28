package fr.insee.eno.core.processing.out.steps.lunatic.cleaning;

import fr.insee.ddi.lifecycle33.instance.DDIInstanceDocument;
import fr.insee.eno.core.PoguesDDIToEno;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.processing.ProcessingPipeline;
import fr.insee.eno.core.processing.out.steps.lunatic.*;
import fr.insee.eno.core.processing.out.steps.lunatic.table.LunaticTableProcessing;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.cleaning.CleaningExpression;
import fr.insee.lunatic.model.flat.cleaning.CleaningVariableEntry;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LunaticAddCleaningTest {

    private EnoQuestionnaire enoQuestionnaire;
    private EnoIndex enoIndex;
    private Questionnaire lunaticQuestionnaire;

    @Test
    void testCleaningOfCodeFilters() throws ParsingException {
        // Given
        mapQuestionnaireToLunatic(
                "functional/pogues/codes-filtered/pogues-m8hgkyw0.json",
                "functional/ddi/codes-filtered/ddi-m8hgkyw0.xml");

        // When
        var cleaningProcessing = new LunaticAddCleaning(enoQuestionnaire, enoIndex);
        cleaningProcessing.preProcessCleaning(lunaticQuestionnaire);
        cleaningProcessing.processCodeFiltered(lunaticQuestionnaire);

        // Then
        assertNotNull(lunaticQuestionnaire.getCleaning().getCleaningEntry("AGE"));
        assertThat(lunaticQuestionnaire.getCleaning()
                .getCleaningEntry("AGE")
                .getCleanedVariable("RADIO_OUI_NON")
                .getCleaningExpressions())
                .hasSize(2);
        assertEquals("(nvl(AGE, 0) > 18) or (RADIO_OUI_NON <> \"3\")", lunaticQuestionnaire.getCleaning()
                .getCleaningEntry("AGE")
                .getCleanedVariable("RADIO_OUI_NON")
                .getCleaningExpressions().get(0).getExpression());
        assertEquals("(nvl(AGE, 0) > 50) or (RADIO_OUI_NON <> \"4\")", lunaticQuestionnaire.getCleaning()
                .getCleaningEntry("AGE")
                .getCleanedVariable("RADIO_OUI_NON")
                .getCleaningExpressions().get(1).getExpression());

        assertThat(lunaticQuestionnaire.getCleaning()
                .getCleaningEntry("AGE")
                .getCleanedVariable("DROPDOWN_OUI_NON")
                .getCleaningExpressions())
                .hasSize(1);
        assertEquals("(nvl(AGE, 0) > 18) or (DROPDOWN_OUI_NON <> \"3\")", lunaticQuestionnaire.getCleaning()
                .getCleaningEntry("AGE")
                .getCleanedVariable("DROPDOWN_OUI_NON")
                .getCleaningExpressions().getFirst().getExpression());

        assertThat(lunaticQuestionnaire.getCleaning()
                .getCleaningEntry("AGE")
                .getCleanedVariable("CHEXBOXMULTI_OUI_NON3")
                .getCleaningExpressions())
                .hasSize(1);
        assertEquals("nvl(AGE, 0) > 18", lunaticQuestionnaire.getCleaning()
                .getCleaningEntry("AGE")
                .getCleanedVariable("CHEXBOXMULTI_OUI_NON3")
                .getCleaningExpressions().getFirst().getExpression());
    }

    @Test
    void testCleaningOfDetailResponse() throws ParsingException {
        // Given
        mapQuestionnaireToLunatic(
                "integration/pogues/pogues-other-specify.json",
                "integration/ddi/ddi-other-specify.xml");

        // When
        var cleaningProcessing = new LunaticAddCleaning(enoQuestionnaire, enoIndex);
        cleaningProcessing.preProcessCleaning(lunaticQuestionnaire);
        cleaningProcessing.processClarificationFiltered(lunaticQuestionnaire);

        // Then
        CleaningVariableEntry cleaningEntryUCQ = lunaticQuestionnaire.getCleaning()
                .getCleaningEntry("UCQ_RADIO");
        assertNotNull(cleaningEntryUCQ);
        assertThat(cleaningEntryUCQ
                .getCleanedVariable("UCQ_codeC_RADIO")
                .getCleaningExpressions())
                .hasSize(1);
        CleaningExpression cleaningExpression1 =  cleaningEntryUCQ
                .getCleanedVariable("UCQ_codeC_RADIO")
                .getCleaningExpressions().getFirst();
        assertEquals("UCQ_RADIO = \"codeC\"", cleaningExpression1.getExpression());
        assertThat(cleaningEntryUCQ
                .getCleanedVariable("UCQ_codeD_RADIO")
                .getCleaningExpressions())
                .hasSize(1);
        CleaningExpression cleaningExpression2 =  cleaningEntryUCQ
                .getCleanedVariable("UCQ_codeD_RADIO")
                .getCleaningExpressions().getFirst();
        assertEquals("UCQ_RADIO = \"codeD\"", cleaningExpression2.getExpression());

        CleaningVariableEntry cleaningEntryMCQ3 = lunaticQuestionnaire.getCleaning()
                .getCleaningEntry("MCQ3");
        assertNotNull(cleaningEntryMCQ3);
        assertThat(cleaningEntryMCQ3
                .getCleanedVariable("MCQ_codeC")
                .getCleaningExpressions())
                .hasSize(1);
        CleaningExpression cleaningExpressionQCM2 =  cleaningEntryMCQ3
                .getCleanedVariable("MCQ_codeC")
                .getCleaningExpressions().getFirst();
        assertEquals("nvl(MCQ3, false)", cleaningExpressionQCM2.getExpression());

        CleaningVariableEntry cleaningEntryMCQ4 = lunaticQuestionnaire.getCleaning()
                .getCleaningEntry("MCQ4");
        assertNotNull(cleaningEntryMCQ4);
        assertThat(cleaningEntryMCQ4
                .getCleanedVariable("MCQ_codeD")
                .getCleaningExpressions())
                .hasSize(1);
        CleaningExpression cleaningExpressionQCM3 =  cleaningEntryMCQ4
                .getCleanedVariable("MCQ_codeD")
                .getCleaningExpressions().getFirst();
        assertEquals("nvl(MCQ4, false)", cleaningExpressionQCM3.getExpression());
    }

    @Test
    void testCleaningOfCellsFiltered() throws ParsingException {
        // Given
        mapQuestionnaireToLunatic(
                "integration/pogues/pogues-table-cell-filter.json",
                "integration/ddi/ddi-table-cell-filter.xml");

        // When
        var cleaningProcessing = new LunaticAddCleaning(enoQuestionnaire, enoIndex);
        cleaningProcessing.preProcessCleaning(lunaticQuestionnaire);
        cleaningProcessing.processCellsFiltered(lunaticQuestionnaire);

        // Then
        CleaningVariableEntry cleaningEntry = lunaticQuestionnaire.getCleaning()
                .getCleaningEntry("DYNAMIC_TABLE1");
        assertNotNull(cleaningEntry);
        List<CleaningExpression> cleaningExpressions = cleaningEntry
                .getCleanedVariable("DYNAMIC_TABLE2")
                .getCleaningExpressions();
        assertThat(cleaningExpressions).hasSize(1);
        CleaningExpression cleaningExpression =  cleaningExpressions.getFirst();
        assertEquals("DYNAMIC_TABLE1", cleaningExpression.getExpression());
        assertEquals("DYNAMIC_TABLE1", cleaningExpression.getShapeFrom());
    }

    @Test
    void testCleaningOfPairwise() throws ParsingException {
        // Given
        mapQuestionnaireToLunatic(
                "integration/pogues/pogues-pairwise.json", "integration/ddi/ddi-pairwise.xml");

        // When
        var cleaningProcessing = new LunaticAddCleaning(enoQuestionnaire, enoIndex);
        cleaningProcessing.preProcessCleaning(lunaticQuestionnaire);
        cleaningProcessing.processPairwiseCleaning(lunaticQuestionnaire);

        // Then
        assertThat(lunaticQuestionnaire.getCleaning()
                .getCleaningEntry("PAIRWISE_SOURCE")
                .getCleanedVariable("PAIRWISE_QUESTION")
                .getCleaningExpressions())
                .hasSize(1);
        CleaningExpression cleaningExpression =  lunaticQuestionnaire.getCleaning()
                .getCleaningEntry("PAIRWISE_SOURCE")
                .getCleanedVariable("PAIRWISE_QUESTION")
                .getCleaningExpressions().getFirst();

        assertEquals("nvl(PAIRWISE_SOURCE, \"\") <> \"\"", cleaningExpression.getExpression());
        assertEquals("PAIRWISE_SOURCE", cleaningExpression.getShapeFrom());
    }

    @Test
    void testNotCleaningItSelf() throws ParsingException {
        // Given
        mapQuestionnaireToLunatic(
                "integration/pogues/pogues-self-cleaning.json", "integration/ddi/ddi-self-cleaning.xml");

        // When
        var cleaningProcessing = new LunaticAddCleaning(enoQuestionnaire, enoIndex);
        cleaningProcessing.apply(lunaticQuestionnaire);

        // Then : no self cleaning
        assertNull(lunaticQuestionnaire.getCleaning()
                .getCleaningEntry("PRENOM")
                .getCleanedVariable("PRENOM"));
    }


    /** Utility test method to map given Pogues & DDI resource files to Lunatic,
     * and apply only processing steps that are required before the cleaning one. */
    private void mapQuestionnaireToLunatic(String poguesResourcePath, String ddiResourcePath) throws ParsingException {

        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        fr.insee.pogues.model.Questionnaire poguesQuestionnaire = PoguesDeserializer.deserialize(
                this.getClass().getClassLoader().getResourceAsStream(poguesResourcePath));
        DDIInstanceDocument ddiQuestionnaire = DDIDeserializer.deserialize(
                this.getClass().getClassLoader().getResourceAsStream(ddiResourcePath));
        enoQuestionnaire = PoguesDDIToEno.fromObjects(poguesQuestionnaire, ddiQuestionnaire)
                .transform(enoParameters);
        enoIndex = enoQuestionnaire.getIndex();

        lunaticQuestionnaire = new Questionnaire();
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
        ProcessingPipeline<Questionnaire> processingPipeline = new ProcessingPipeline<>();
        processingPipeline.start(lunaticQuestionnaire)
                .then(new LunaticSortComponents(enoQuestionnaire))
                .then(new LunaticLoopResolution(enoQuestionnaire))
                .then(new LunaticTableProcessing(enoQuestionnaire))
                .then(new LunaticInsertUniqueChoiceDetails(enoQuestionnaire))
                .then(new LunaticInsertCodeFilters(enoQuestionnaire))
                .then(new LunaticVariablesDimension(enoQuestionnaire));
    }

}
