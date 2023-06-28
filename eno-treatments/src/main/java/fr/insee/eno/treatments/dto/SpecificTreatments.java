package fr.insee.eno.treatments.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SpecificTreatments {

    private final List<EnoSuggesterType> suggesters;

    @JsonCreator
    public SpecificTreatments(@JsonProperty(value = "suggesters") List<EnoSuggesterType> suggesters) {
        this.suggesters = suggesters;
    }
}
