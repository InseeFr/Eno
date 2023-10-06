package fr.insee.eno.core.model.lunatic;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
public class LunaticResizingPairwiseEntry implements LunaticResizingInterface {
    /** Collected variable he pairwise is based on. */
    private String name;
    /** List of size 2 containing size expressions for x and y-axis of the pairwise. */
    private List<String> sizeForLinksVariables;
    /** Resized variables. */
    private Collection<String> linksVariables;

}
