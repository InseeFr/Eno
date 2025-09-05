package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.ddi.lifecycle33.instance.DDIInstanceDocument;
import fr.insee.eno.core.PoguesDDIToEno;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.processing.ProcessingPipeline;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.eno.core.serialize.LunaticSerializer;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.Test;

import static fr.insee.eno.core.utils.LunaticUtils.findComponentById;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LunaticLoopFilterIntegrationTest {

    private EnoQuestionnaire enoQuestionnaire;
    private Questionnaire lunaticQuestionnaire;

    @Test
    void testLoopFilterResolution() throws ParsingException {
        // Given questionnaire
        // When mapping
        mapQuestionnaireToLunatic(
                "integration/pogues/pogues-loop-filter.json",
                "integration/ddi/ddi-loop-filter.xml");

        // Then
        assertEquals("(not(FILTRE))", findComponentById(lunaticQuestionnaire, "mf5etm57").get().getConditionFilter().getValue());
        assertThat(findComponentById(lunaticQuestionnaire, "mf5evs8l").get().getConditionFilter().getValue())
                .doesNotContain("not(AGES2 <> 10)");

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

        lunaticQuestionnaire = new Questionnaire();
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
        ProcessingPipeline<Questionnaire> processingPipeline = new ProcessingPipeline<>();
        processingPipeline.start(lunaticQuestionnaire)
                .then(new LunaticSortComponents(enoQuestionnaire))
                .then(new LunaticLoopResolution(enoQuestionnaire));
    }
}
