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

    /**
     *
     * @param enoQueryParser EnoQueryParser object to convert
     * @return the corresponding lunatic model object
     */
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
