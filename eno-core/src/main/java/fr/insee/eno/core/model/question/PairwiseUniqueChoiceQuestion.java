package fr.insee.eno.core.model.question;

import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.lunatic.model.flat.ComponentType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Eno model class to represent pairwise unique choice questions (UCQ).
 * In DDI, it corresponds to a QuestionItem.
 */
@Getter
@Setter
@Slf4j
public class PairwiseUniqueChoiceQuestion extends UniqueChoiceQuestion {

    @DDI(contextType = QuestionItemType.class, field = "T(fr.insee.eno.core.model.question.PairwiseUniqueChoiceQuestion).convertId(#this)")
    @Lunatic(contextType = ComponentType.class, field ="setId(#param)")
    String id;

    /**
     * in case of a pairwise question, the unique choice question identifier is generated differently.
     * In order to avoid
     * @param questionItemType question item type
     * @return identifier for this unique choice question
     */
    public static String convertId(QuestionItemType questionItemType) {
        /* CodeDomainType codeDomain = (CodeDomainType) questionItemType.getResponseDomain();
         String codeId = codeDomain.getCodeListReference().getIDArray(0).getStringValue();*/
        return questionItemType.getIDArray(0).getStringValue() + "-pairwise-dropdown";
    }
}
