package fr.insee.eno.core.model.lunatic;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Cleaning entry for the 'cleaning' block of a Lunatic questionnaire.
 * Note: to be deported in Lunatic-Model later on.
 */
@Data
@AllArgsConstructor
public class CleaningEntry {

    /** Variable name (key of the resizing entry). */
    private String variableName;

    /** List of variable names (value of the resizing entry). */
    private List<CleaningConcernedVariable> concernedVariables;

}
