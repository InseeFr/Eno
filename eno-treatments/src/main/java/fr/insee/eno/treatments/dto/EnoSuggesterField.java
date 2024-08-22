package fr.insee.eno.treatments.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.insee.lunatic.model.flat.FieldRules;
import fr.insee.lunatic.model.flat.SuggesterField;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record EnoSuggesterField(
        @JsonProperty(value = "name", required = true)
        String name,
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        List<String> rules,
        String language,
        BigInteger min,
        Boolean stemmer,
        Map<String, List<String>> synonyms) {

    /**
     * @param suggesterField EnoSuggesterField object to convert
     * @return the corresponding lunatic model object
     */
    public static SuggesterField toLunaticModel(EnoSuggesterField suggesterField) {
        if (suggesterField == null) {
            return null;
        }

        SuggesterField lunaticField = new SuggesterField();
        lunaticField.setName(suggesterField.name());
        lunaticField.setLanguage(suggesterField.language());
        lunaticField.setMin(suggesterField.min());
        lunaticField.setStemmer(suggesterField.stemmer());

        if (suggesterField.rules() != null) {
            lunaticField.setRules(new FieldRules());
            if (isSoftRule(suggesterField.rules())) {
                lunaticField.getRules().setRule(FieldRules.SOFT_RULE);
            } else {
                suggesterField.rules().forEach(pattern -> lunaticField.getRules().addPattern(pattern));
            }
        }

        if (suggesterField.synonyms() != null) {
            suggesterField.synonyms().forEach((stringKey, stringsValue) ->
                    lunaticField.getSynonyms().put(stringKey, stringsValue));
        }

        return lunaticField;
    }

    private static boolean isSoftRule(List<String> enoFieldRules) {
        return enoFieldRules.size() == 1 && FieldRules.SOFT_RULE.equals(enoFieldRules.getFirst());
    }

    /**
     * @param enoFields EnoSuggesterField list object to convert
     * @return the corresponding lunatic model list object
     */
    public static List<SuggesterField> toLunaticModelList(List<EnoSuggesterField> enoFields) {
        if (enoFields == null) {
            return new ArrayList<>();
        }
        return enoFields.stream().map(EnoSuggesterField::toLunaticModel).toList();
    }
}
