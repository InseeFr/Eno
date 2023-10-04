package fr.insee.eno.core.model.lunatic;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LunaticResizingPairwiseEntry {
    private String name;
    private List<String> sizeForLinksVariables;
    private List<String> linksVariables;

}
