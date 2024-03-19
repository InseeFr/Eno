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
    private Boolean stemmer;

    @JsonCreator
    public EnoSuggesterQueryParserParams(@JsonProperty("language") String language,
                                         @JsonProperty("min") BigInteger min,
                                         @JsonProperty("pattern") String pattern,
                                         @JsonProperty("stemmer") Boolean stemmer) {
        this.language = language;
        this.min = min;
        this.pattern = pattern;
        this.stemmer = stemmer;
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
        params.setStemmer(enoParams.getStemmer());
        return params;
    }
}
