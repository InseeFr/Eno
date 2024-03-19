package fr.insee.eno.treatments.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SpecificTreatments(@JsonProperty(value = "suggesters") List<EnoSuggesterType> suggesters,
                                 @JsonProperty(value = "regroupements")  List<Regroupement> regroupements) {
}