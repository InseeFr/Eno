package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.ddi.lifecycle33.instance.DDIInstanceDocument;
import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.model.sequence.AbstractSequence;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.parameter.LunaticParameters;
import fr.insee.eno.core.processing.ProcessingPipeline;
import fr.insee.eno.core.processing.out.steps.lunatic.pagination.LunaticAddPageNumbers;
import fr.insee.eno.core.processing.out.steps.lunatic.resizing.LunaticAddResizing;
import fr.insee.eno.core.processing.out.steps.lunatic.table.LunaticTableProcessing;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LunaticAddCleaningVariablesTest {

    LunaticAddCleaningVariables cleaningProcessing;
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
        List<String> finalBindingsDeps = cleaningProcessing.getFinalBindingReferences(calculatedFilter.getExpression());
        assertThat(finalBindingsDeps).containsExactly("AGE");

    }


    void prepareQuestionnaireTest(String questionnaireTestUrl) throws DDIParsingException {
        EnoParameters enoParameters = EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        DDIInstanceDocument ddiQuestionnaire = DDIDeserializer.deserialize(
                this.getClass().getClassLoader().getResourceAsStream(questionnaireTestUrl));
        enoQuestionnaire = DDIToEno.fromObject(ddiQuestionnaire).transform(enoParameters);
        cleaningProcessing = new LunaticAddCleaningVariables(enoQuestionnaire);
        lunaticQuestionnaire = new Questionnaire();
        preProcessQuestionnaire(lunaticQuestionnaire, enoQuestionnaire);
        applyProcessingBeforeCleaning(lunaticQuestionnaire, enoQuestionnaire);
        cleaningProcessing.preProcessVariables(lunaticQuestionnaire);
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
                .then(new LunaticEditLabelTypes()) // this step should be temporary
                .then(new LunaticSuggestersConfiguration(enoQuestionnaire))
                .then(new LunaticVariablesDimension(enoQuestionnaire))
                .then(new LunaticSuggesterOptionResponses())
                .then(new LunaticAddResizing(enoQuestionnaire))
                .then(new LunaticAddPageNumbers((LunaticParameters.LunaticPaginationMode.QUESTION)))
                .then(new LunaticResponseTimeQuestionPagination())
                .then(new LunaticAddCleaningVariables(enoQuestionnaire));
    }

}
