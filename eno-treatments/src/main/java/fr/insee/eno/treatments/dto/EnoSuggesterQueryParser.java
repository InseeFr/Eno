package fr.insee.eno.treatments.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.insee.lunatic.model.flat.SuggesterQueryParser;

public record EnoSuggesterQueryParser(
        @JsonProperty(value = "type", required = true) String type,
        EnoSuggesterQueryParserParams params) {

    /**
     * @param enoQueryParser EnoQueryParser object to convert
     * @return the corresponding lunatic model object
     */
    public static SuggesterQueryParser toLunaticModel(EnoSuggesterQueryParser enoQueryParser) {
        if (enoQueryParser == null) {
            return null;
        }
        SuggesterQueryParser queryParser = new SuggesterQueryParser();
        queryParser.setType(enoQueryParser.type());
        if (enoQueryParser.params() != null) {
            queryParser.setParams(EnoSuggesterQueryParserParams.toLunaticModel(enoQueryParser.params()));
        }
        return queryParser;
    }
}
