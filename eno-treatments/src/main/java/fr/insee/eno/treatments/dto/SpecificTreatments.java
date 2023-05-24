//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.3.2
// Voir <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2023.05.11 à 10:17:07 AM CEST
//


package fr.insee.eno.treatments.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SpecificTreatments {

    private List<EnoSuggesterType> suggesters;

    @JsonCreator
    public SpecificTreatments(@JsonProperty(value = "suggesters") List<EnoSuggesterType> suggesters) {
        this.suggesters = suggesters;
    }
}
