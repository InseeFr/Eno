package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.PoguesDDIToEno;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticSortComponents;
import fr.insee.eno.core.serialize.LunaticSerializer;
import fr.insee.lunatic.model.flat.ComponentSimpleResponseType;
import fr.insee.lunatic.model.flat.PairwiseLinks;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MandatoryQuestionTest {

    @Test
    void integrationTestFromPoguesDDI() throws ParsingException {
        // Given + When
        ClassLoader classLoader = this.getClass().getClassLoader();
        EnoQuestionnaire enoQuestionnaire = PoguesDDIToEno.fromInputStreams(
                classLoader.getResourceAsStream("integration/pogues/pogues-mandatory.json"),
                classLoader.getResourceAsStream("integration/ddi/ddi-mandatory.xml"))
                .transform(EnoParameters.of(
                        EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI));
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        new LunaticMapper().mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
        new LunaticSortComponents(enoQuestionnaire).apply(lunaticQuestionnaire); // (sort so that we can get components by index)
        // Then
        assertFalse(((ComponentSimpleResponseType) lunaticQuestionnaire.getComponents().get(1)).getMandatory());
        assertTrue(((ComponentSimpleResponseType) lunaticQuestionnaire.getComponents().get(2)).getMandatory());
    }

    @Test
    void integrationTestFromPoguesDDIWithPairwiseWithNullMandatory() throws ParsingException {
        // Given + When
        ClassLoader classLoader = this.getClass().getClassLoader();
        EnoQuestionnaire enoQuestionnaire = PoguesDDIToEno.fromInputStreams(
                        classLoader.getResourceAsStream("integration/pogues/pogues-pairwise.json"),
                        classLoader.getResourceAsStream("integration/ddi/ddi-pairwise.xml"))
                .transform(EnoParameters.of(
                        EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI));
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        new LunaticMapper().mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
        new LunaticSortComponents(enoQuestionnaire).apply(lunaticQuestionnaire); // (sort so that we can get components by index)

        assertFalse(((ComponentSimpleResponseType)
                ((PairwiseLinks) lunaticQuestionnaire.getComponents().get(3)).getComponents().getFirst()).getMandatory());
    }

}
