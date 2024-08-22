package fr.insee.eno.treatments.dto;

import fr.insee.lunatic.model.flat.SuggesterQueryParserParams;

import java.math.BigInteger;

public record EnoSuggesterQueryParserParams(String language, BigInteger min, String pattern, Boolean stemmer) {

    /**
     * @param enoParams EnoSuggesterQueryParserParams object to convert
     * @return the corresponding lunatic model object
     */
    public static SuggesterQueryParserParams toLunaticModel(EnoSuggesterQueryParserParams enoParams) {
        if (enoParams == null) {
            return null;
        }
        SuggesterQueryParserParams params = new SuggesterQueryParserParams();
        params.setLanguage(enoParams.language());
        params.setMin(enoParams.min());
        params.setPattern(enoParams.pattern());
        params.setStemmer(enoParams.stemmer());
        return params;
    }
}
