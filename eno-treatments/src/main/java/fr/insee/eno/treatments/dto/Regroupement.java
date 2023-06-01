package fr.insee.eno.treatments.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.insee.eno.treatments.RegroupementDeserializer;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@JsonDeserialize(using = RegroupementDeserializer.class)
@Data
public class Regroupement {
    private List<String> variables;

    public Regroupement(@NonNull List<String> variables) {
        if(variables.isEmpty() || variables.size() < 2) {
            throw new IllegalArgumentException("Il faut définir au moins 2 variables pour un regroupement");
        }
        this.variables = variables;
    }
    public boolean isFirstVariable(String variable) {
        return variables.get(0).equals(variable);
    }

    public boolean hasVariable(String variable) {
        return variables.contains(variable);
    }
}
