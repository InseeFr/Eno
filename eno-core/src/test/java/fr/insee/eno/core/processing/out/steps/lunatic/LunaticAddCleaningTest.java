package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.ddi.lifecycle33.instance.DDIInstanceDocument;
import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.PoguesDDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.model.sequence.AbstractSequence;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.parameter.LunaticParameters;
import fr.insee.eno.core.processing.ProcessingPipeline;
import fr.insee.eno.core.processing.out.steps.lunatic.cleaning.CleaningUtils;
import fr.insee.eno.core.processing.out.steps.lunatic.cleaning.LunaticAddCleaning;
import fr.insee.eno.core.processing.out.steps.lunatic.pagination.LunaticAddPageNumbers;
import fr.insee.eno.core.processing.out.steps.lunatic.resizing.LunaticAddResizing;
import fr.insee.eno.core.processing.out.steps.lunatic.table.LunaticTableProcessing;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.cleaning.CleaningExpression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LunaticAddCleaningTest {

    LunaticAddCleaning cleaningProcessing;
    Questionnaire lunaticQuestionnaire;
    EnoQuestionnaire enoQuestionnaire;


    @BeforeEach
    void init() {
        cleaningProcessing = null;
        lunaticQuestionnaire = null;
        enoQuestionnaire = null;
    }

    @Test
    void testGetCollectedVarsInSequence() throws DDIParsingException {
        prepareQuestionnaireTest("functional/ddi/cleaning/ddi-m7oqvx8y.xml");
        AbstractSequence firstSeq = (AbstractSequence) enoQuestionnaire.getIndex().get("m7oqve1l");
        List<String> collectedVarsInFirstSeq = cleaningProcessing.getCollectedVarsInSequence(firstSeq);
        assertThat(collectedVarsInFirstSeq).containsExactly( "TEST", "AGE","PRENOM");

        AbstractSequence secondSeq = (AbstractSequence) enoQuestionnaire.getIndex().get("m7oqon12");
        List<String> collectedVarsInSecondSeq = cleaningProcessing.getCollectedVarsInSequence(secondSeq);
        assertThat(collectedVarsInSecondSeq).containsExactly( "OUIOUNON", "QUAR_PLUS");
    }


    @Test
    void testGetCollectedVarsInFilter() throws DDIParsingException {
        prepareQuestionnaireTest("functional/ddi/cleaning/ddi-m7oqvx8y.xml");
        Filter majFilter = (Filter) enoQuestionnaire.getIndex().get("m7oqxnxe");
        List<String> collectedVarsMajFilter = cleaningProcessing.getCollectedVariablesInFilter(majFilter);
        assertThat(collectedVarsMajFilter).containsExactly( "PRENOM");

        Filter seqFilter = (Filter) enoQuestionnaire.getIndex().get("m7oqml84");
        List<String> collectedVarsSeqFilter = cleaningProcessing.getCollectedVariablesInFilter(seqFilter);
        assertThat(collectedVarsSeqFilter).containsExactly( "OUIOUNON", "QUAR_PLUS");
    }

    @Test
    void testGetFinalBindingReferences() throws DDIParsingException {
        prepareQuestionnaireTest("functional/ddi/cleaning/ddi-m7oqvx8y.xml");
        Filter calculatedFilter = (Filter) enoQuestionnaire.getIndex().get("m7oqxnxe");
        List<String> finalBindingsDeps = CleaningUtils.removeCalculatedVariables(
                CleaningUtils.getFinalBindingReferencesWithCalculatedVariables(calculatedFilter.getExpression(), cleaningProcessing.getVariableIndex()),
                cleaningProcessing.getVariableIndex());
        assertThat(finalBindingsDeps).containsExactly("AGE");

    }

    @Test
    void testGetShapeFromOfVariable() throws DDIParsingException {
        prepareQuestionnaireTest("functional/ddi/cleaning/ddi-m7oqvx8y.xml");
        String variableInsideLoop = "TEMPS";
        assertEquals("PRENOMS", cleaningProcessing.getVariableShapeFromIndex().get(variableInsideLoop));
        String variableOutOfLoop = "TEST";
        assertNull(cleaningProcessing.getVariableShapeFromIndex().get(variableOutOfLoop));
    }

    @Test
    void testCleaningOfCodeFilters() throws DDIParsingException, PoguesDeserializationException {
        prepareQuestionnaireTest(
                "functional/pogues/codes-filtered/pogues-m8hgkyw0.json",
                "functional/ddi/codes-filtered/ddi-m8hgkyw0.xml");
        cleaningProcessing.processCodeFiltered(lunaticQuestionnaire);
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
                .getCleaningExpressions().get(0).getExpression());

        assertThat(lunaticQuestionnaire.getCleaning()
                .getCleaningEntry("AGE")
                .getCleanedVariable("CHEXBOXMULTI_OUI_NON3")
                .getCleaningExpressions())
                .hasSize(1);
        assertEquals("nvl(AGE, 0) > 18", lunaticQuestionnaire.getCleaning()
                .getCleaningEntry("AGE")
                .getCleanedVariable("CHEXBOXMULTI_OUI_NON3")
                .getCleaningExpressions().get(0).getExpression());
    }

    @Test
    void testCleaningOfCellsFiltered() throws DDIParsingException, PoguesDeserializationException {
        prepareQuestionnaireTest(
                "functional/pogues/cells-filtered/pogues-m92r209h.json",
                "functional/ddi/cells-filtered/ddi-m92r209h.xml");
        cleaningProcessing.processCellsFiltered(lunaticQuestionnaire);

        assertNotNull(lunaticQuestionnaire.getCleaning().getCleaningEntry("AGE"));
        assertThat(lunaticQuestionnaire.getCleaning()
                .getCleaningEntry("AGE")
                .getCleanedVariable("NB_BOULOT")
                .getCleaningExpressions())
                .hasSize(1);
        CleaningExpression cleaningExpression =  lunaticQuestionnaire.getCleaning()
                .getCleaningEntry("AGE")
                .getCleanedVariable("NB_BOULOT")
                .getCleaningExpressions().get(0);
        assertEquals("AGE >= 18", cleaningExpression.getExpression());
        assertEquals("PRENOM", cleaningExpression.getShapeFrom());
    }


    @Test
    void testCleaningOfPairwise() throws DDIParsingException, PoguesDeserializationException {
        prepareQuestionnaireTest("integration/ddi/ddi-pairwise.xml");
        cleaningProcessing.processPairwiseCleaning(lunaticQuestionnaire);

    }


    void prepareQuestionnaireTest(String poguesQuestionnaireTestUrl, String ddiQuestionnaireTestUrl) throws DDIParsingException, PoguesDeserializationException {
        EnoParameters enoParameters = EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        fr.insee.pogues.model.Questionnaire poguesQuestionnaire = PoguesDeserializer.deserialize(
                this.getClass().getClassLoader().getResourceAsStream(poguesQuestionnaireTestUrl));
        DDIInstanceDocument ddiQuestionnaire = DDIDeserializer.deserialize(
                this.getClass().getClassLoader().getResourceAsStream(ddiQuestionnaireTestUrl));
        enoQuestionnaire = PoguesDDIToEno.fromObjects(poguesQuestionnaire, ddiQuestionnaire).transform(enoParameters);
        cleaningProcessing = new LunaticAddCleaning(enoQuestionnaire);
        lunaticQuestionnaire = new Questionnaire();
        preProcessQuestionnaire(lunaticQuestionnaire, enoQuestionnaire);
        applyProcessingBeforeCleaning(lunaticQuestionnaire, enoQuestionnaire);
        cleaningProcessing.preProcessCleaning(lunaticQuestionnaire);
    }

    void prepareQuestionnaireTest(String ddiQuestionnaireTestUrl) throws DDIParsingException {
        EnoParameters enoParameters = EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        DDIInstanceDocument ddiQuestionnaire = DDIDeserializer.deserialize(
                this.getClass().getClassLoader().getResourceAsStream(ddiQuestionnaireTestUrl));
        enoQuestionnaire = DDIToEno.fromObject(ddiQuestionnaire).transform(enoParameters);
        cleaningProcessing = new LunaticAddCleaning(enoQuestionnaire);
        lunaticQuestionnaire = new Questionnaire();
        preProcessQuestionnaire(lunaticQuestionnaire, enoQuestionnaire);
        applyProcessingBeforeCleaning(lunaticQuestionnaire, enoQuestionnaire);
        cleaningProcessing.preProcessCleaning(lunaticQuestionnaire);
    }

    static void preProcessQuestionnaire(Questionnaire lunaticQuestionnaire, EnoQuestionnaire enoQuestionnaire){
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
    }

    static void applyProcessingBeforeCleaning(Questionnaire lunaticQuestionnaire, EnoQuestionnaire enoQuestionnaire) {
        EnoIndex enoIndex = enoQuestionnaire.getIndex();
        assert enoIndex != null;
        ProcessingPipeline<Questionnaire> processingPipeline = new ProcessingPipeline<>();
        processingPipeline.start(lunaticQuestionnaire)
                .then(new LunaticAddGeneratingDate())
                .then(new LunaticSortComponents(enoQuestionnaire))
                .then(new LunaticLoopResolution(enoQuestionnaire))
                .then(new LunaticTableProcessing(enoQuestionnaire))
                .then(new LunaticInsertUniqueChoiceDetails(enoQuestionnaire))
                .then(new LunaticInsertCodeFilters(enoQuestionnaire))
                .then(new LunaticEditLabelTypes()) // this step should be temporary
                .then(new LunaticSuggestersConfiguration(enoQuestionnaire))
                .then(new LunaticVariablesDimension(enoQuestionnaire))
                .then(new LunaticSuggesterOptionResponses())
                .then(new LunaticAddResizing(enoQuestionnaire))
                .then(new LunaticAddPageNumbers((LunaticParameters.LunaticPaginationMode.QUESTION)))
                .then(new LunaticResponseTimeQuestionPagination());
    }

}
