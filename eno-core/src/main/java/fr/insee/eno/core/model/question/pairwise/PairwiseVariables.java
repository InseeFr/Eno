package fr.insee.eno.core.model.question.pairwise;

import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.PairwiseLinks;
import fr.insee.pogues.model.SourceVariableReferences;
import lombok.Getter;
import lombok.Setter;

/** Variables associated with the pairwise question. */
@Context(format = Format.POGUES, type = SourceVariableReferences.class)
@Context(format = Format.LUNATIC, type = PairwiseLinks.SourceVariables.class)
@Getter
@Setter
public class PairwiseVariables extends EnoObject {

    @Pogues("#poguesIndex.get(#root.getName())?.getName()")
    @Lunatic("setName(#param)")
    private String name;

    @Pogues("#poguesIndex.get(#root.getGender())?.getName()")
    @Lunatic("setGender(#param)")
    private String gender;

}
