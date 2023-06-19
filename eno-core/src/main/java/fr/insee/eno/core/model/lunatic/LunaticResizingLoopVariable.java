package fr.insee.eno.core.model.lunatic;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LunaticResizingLoopVariable {
    private String name;
    private String size;
    private List<String> variables;

}