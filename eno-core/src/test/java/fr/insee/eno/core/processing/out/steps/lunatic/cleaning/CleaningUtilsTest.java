package fr.insee.eno.core.processing.out.steps.lunatic.cleaning;

import fr.insee.ddi.lifecycle33.instance.DDIInstanceDocument;
import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.model.sequence.AbstractSequence;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.processing.ProcessingPipeline;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticLoopResolution;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticSortComponents;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticVariablesDimension;
import fr.insee.eno.core.processing.out.steps.lunatic.table.LunaticTableProcessing;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CleaningUtilsTest {

    private EnoIndex enoIndex;
    private LunaticAddCleaning cleaningProcessing;

    @BeforeAll
    void mapTestDDIToLunatic() throws DDIParsingException {
        // Map test DDI to Lunatic
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        DDIInstanceDocument ddiQuestionnaire = DDIDeserializer.deserialize(
                this.getClass().getClassLoader().getResourceAsStream("functional/ddi/cleaning/ddi-m7oqvx8y.xml"));
        EnoQuestionnaire enoQuestionnaire = DDIToEno.fromObject(ddiQuestionnaire).transform(enoParameters);
        enoIndex = enoQuestionnaire.getIndex();
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
        // For the "shape from" related method, some processing steps are required:
        ProcessingPipeline<Questionnaire> processingPipeline = new ProcessingPipeline<>();
        processingPipeline.start(lunaticQuestionnaire)
                .then(new LunaticSortComponents(enoQuestionnaire))
                .then(new LunaticLoopResolution(enoQuestionnaire))
                .then(new LunaticTableProcessing(enoQuestionnaire))
                .then(new LunaticVariablesDimension(enoQuestionnaire));

        cleaningProcessing = new LunaticAddCleaning(enoQuestionnaire, enoIndex);
        cleaningProcessing.preProcessCleaning(lunaticQuestionnaire);
    }

    @Test
    void testGetShapeFromOfVariable() {
        String variableInsideLoop = "TEMPS";
        assertEquals("PRENOMS", cleaningProcessing.getVariableShapeFromIndex().get(variableInsideLoop));
        String variableOutOfLoop = "TEST";
        assertNull(cleaningProcessing.getVariableShapeFromIndex().get(variableOutOfLoop));
    }

    @Test
    void testGetCollectedVarsInSequence() {
        AbstractSequence firstSeq = (AbstractSequence) enoIndex.get("m7oqve1l");
        List<String> collectedVarsInFirstSeq = cleaningProcessing.getCollectedVarsInSequence(firstSeq);
        assertThat(collectedVarsInFirstSeq).containsExactly( "TEST", "AGE","PRENOM");

        AbstractSequence secondSeq = (AbstractSequence) enoIndex.get("m7oqon12");
        List<String> collectedVarsInSecondSeq = cleaningProcessing.getCollectedVarsInSequence(secondSeq);
        assertThat(collectedVarsInSecondSeq).containsExactly( "OUIOUNON", "QUAR_PLUS");
    }

    @Test
    void testGetCollectedVarsInFilter() {
        Filter majFilter = (Filter) enoIndex.get("m7oqxnxe");
        List<String> collectedVarsMajFilter = cleaningProcessing.getCollectedVariablesInFilter(majFilter);
        assertThat(collectedVarsMajFilter).containsExactly( "PRENOM");

        Filter seqFilter = (Filter) enoIndex.get("m7oqml84");
        List<String> collectedVarsSeqFilter = cleaningProcessing.getCollectedVariablesInFilter(seqFilter);
        assertThat(collectedVarsSeqFilter).containsExactly( "OUIOUNON", "QUAR_PLUS");
    }

    @Test
    void testGetFinalBindingReferences() {
        Filter calculatedFilter = (Filter) enoIndex.get("m7oqxnxe");
        List<String> finalBindingsDependencies = CleaningUtils.removeCalculatedVariables(
                CleaningUtils.getFinalBindingReferencesWithCalculatedVariables(
                        calculatedFilter.getExpression(), cleaningProcessing.getVariableIndex()),
                cleaningProcessing.getVariableIndex());
        assertThat(finalBindingsDependencies).containsExactly("AGE");
    }

}
