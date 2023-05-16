//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.3.2 
// Voir <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2023.05.11 à 10:17:07 AM CEST 
//


package fr.insee.eno.treatments.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.insee.lunatic.model.flat.SuggesterQueryParser;
import lombok.*;

@Data
public class EnoSuggesterQueryParser {

    private String type;
    private EnoSuggesterQueryParserParams params;

    @JsonCreator
    public EnoSuggesterQueryParser(@JsonProperty(value = "type", required = true) String type, @JsonProperty("params") EnoSuggesterQueryParserParams params) {
        this.type = type;
        this.params = params;
    }

    public static SuggesterQueryParser toLunaticModel(EnoSuggesterQueryParser enoQueryParser) {
        if(enoQueryParser == null) {
            return null;
        }
        SuggesterQueryParser queryParser = new SuggesterQueryParser();
        queryParser.setType(enoQueryParser.getType());
        if(enoQueryParser.getParams() != null) {
            queryParser.setParams(EnoSuggesterQueryParserParams.toLunaticModel(enoQueryParser.getParams()));
        }
        return queryParser;
    }
}
