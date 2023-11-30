package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.PairwiseQuestion;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PairwiseQuestionTest {

    @Nested
    class UnitTestsPairwise {
        private PairwiseQuestion enoPairwiseQuestion;
        private PairwiseLinks lunaticPairwiseLinks;

        @BeforeEach
        void pairwiseObjects() {
            enoPairwiseQuestion = new PairwiseQuestion();
            lunaticPairwiseLinks = new PairwiseLinks();
        }

        @Test
        void lunaticComponentType() {
            //
            LunaticMapper lunaticMapper = new LunaticMapper();
            lunaticMapper.mapEnoObject(enoPairwiseQuestion, lunaticPairwiseLinks);
            //
            assertEquals(ComponentTypeEnum.PAIRWISE_LINKS, lunaticPairwiseLinks.getComponentType());
        }
    }

    @Test
    void pairwiseMapping_integrationTest() throws DDIParsingException {
        // Given
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-pairwise.xml"),
                EnoParameters.of(EnoParameters.Context.HOUSEHOLD, EnoParameters.ModeParameter.CAWI));
        Optional<PairwiseQuestion> enoPairwise = enoQuestionnaire.getSingleResponseQuestions().stream()
                .filter(PairwiseQuestion.class::isInstance)
                .map(PairwiseQuestion.class::cast)
                .findAny();
        assert enoPairwise.isPresent();

        // When
        PairwiseLinks lunaticPairwise = new PairwiseLinks();
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoPairwise.get(), lunaticPairwise);

        // Then
        assertEquals("lo9tyy1v", lunaticPairwise.getId());
        assertEquals("count(PAIRWISE_SOURCE)", lunaticPairwise.getXAxisIterations().getValue());
        assertEquals("count(PAIRWISE_SOURCE)", lunaticPairwise.getYAxisIterations().getValue());
        assertEquals(LabelTypeEnum.VTL, lunaticPairwise.getXAxisIterations().getTypeEnum());
        assertEquals(LabelTypeEnum.VTL, lunaticPairwise.getYAxisIterations().getTypeEnum());
        assertEquals(1, lunaticPairwise.getComponents().size());
        //
        ComponentType pairwiseInnerComponent = lunaticPairwise.getComponents().get(0);
        assertTrue(pairwiseInnerComponent instanceof Dropdown);
        assertEquals(ComponentTypeEnum.DROPDOWN, pairwiseInnerComponent.getComponentType());
        Dropdown lunaticPairwiseDropDown = (Dropdown) pairwiseInnerComponent;
        assertEquals("lo9tyy1v-pairwise-dropdown", lunaticPairwiseDropDown.getId());
        assertEquals("PAIRWISE_QUESTION", lunaticPairwiseDropDown.getResponse().getName());
        assertEquals(4, lunaticPairwiseDropDown.getOptions().size());
        assertEquals(List.of("linkA", "linkB", "linkC", "linkD"),
                lunaticPairwiseDropDown.getOptions().stream().map(Options::getValue).toList());
        // Note: for now the symLinks property is added (hard-coded values) by Lunatic-Model
    }

}
