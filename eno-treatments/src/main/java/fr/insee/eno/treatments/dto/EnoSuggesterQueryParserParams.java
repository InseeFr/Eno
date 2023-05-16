//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.3.2 
// Voir <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2023.05.11 à 10:17:07 AM CEST 
//


package fr.insee.eno.treatments.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.insee.lunatic.model.flat.SuggesterQueryParserParams;
import lombok.Data;

import java.math.BigInteger;

@Data
public class EnoSuggesterQueryParserParams {

    private String language;
    private BigInteger min;
    private String pattern;

    @JsonCreator
    public EnoSuggesterQueryParserParams(@JsonProperty("language") String language,
                                         @JsonProperty("min") BigInteger min,
                                         @JsonProperty("pattern") String pattern) {
        this.language = language;
        this.min = min;
        this.pattern = pattern;
    }

    /**
     *
     * @param enoParams EnoSuggesterQueryParserParams object to convert
     * @return the corresponding lunatic model object
     */
    public static SuggesterQueryParserParams toLunaticModel(EnoSuggesterQueryParserParams enoParams) {
        if(enoParams == null) {
            return null;
        }
        SuggesterQueryParserParams params = new SuggesterQueryParserParams();
        params.setLanguage(enoParams.getLanguage());
        params.setMin(enoParams.getMin());
        params.setPattern(enoParams.getPattern());
        return params;
    }
}
