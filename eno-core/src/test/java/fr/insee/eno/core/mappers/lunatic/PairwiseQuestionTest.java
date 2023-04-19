package fr.insee.eno.core.mappers.lunatic;

import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.question.PairwiseQuestion;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.PairwiseLinks;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PairwiseQuestionTest {

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
