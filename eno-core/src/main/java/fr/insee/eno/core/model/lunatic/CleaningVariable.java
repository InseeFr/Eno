package fr.insee.eno.core.model.lunatic;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CleaningVariable {
    private String name;
    private List<CleaningConcernedVariable> concernedVariables;
}
