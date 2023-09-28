package fr.insee.eno.treatments.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.insee.eno.treatments.RegroupementDeserializer;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

/**
 * Regroupement of variables that needs to be on a same page. We assume variables defined in the list
 * are in the same order as their corresponding components in the questionnaire and consecutive
 */
@JsonDeserialize(using = RegroupementDeserializer.class)
@Getter
public class Regroupement {
    private final List<String> variables;

    /**
     *
     * @param variables variables that nedd to be on a same page
     */
    public Regroupement(@NonNull List<String> variables) {
        if(variables.isEmpty() || variables.size() < 2) {
            throw new IllegalArgumentException("Il faut définir au moins 2 variables pour un regroupement");
        }
        this.variables = variables;
    }

    /**
     * @param variable variable to check
     * @return true if the variable is the first in the list, false otherwise
     */
    public boolean isFirstVariable(String variable) {
        return variables.get(0).equals(variable);
    }

    public boolean hasVariable(String variable) {
        return variables.contains(variable);
    }
}
