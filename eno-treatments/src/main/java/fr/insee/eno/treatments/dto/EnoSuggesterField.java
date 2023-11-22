package fr.insee.eno.treatments.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.insee.lunatic.model.flat.FieldRules;
import fr.insee.lunatic.model.flat.SuggesterField;
import lombok.Data;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class EnoSuggesterField {

    private String name;
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<String> rules;
    private String language;
    private BigInteger min;
    private Boolean stemmer;
    private Map<String, List<String>> synonyms;

    @JsonCreator
    public EnoSuggesterField(@JsonProperty(value = "name", required = true) String name,
                             @JsonProperty("rules") List<String> rules,
                             @JsonProperty("language") String language,
                             @JsonProperty("min") BigInteger min,
                             @JsonProperty("stemmer") Boolean stemmer,
                             @JsonProperty("synonyms") Map<String, List<String>> synonyms) {
        this.name = name;
        this.rules = rules;
        this.language = language;
        this.min = min;
        this.stemmer = stemmer;
        this.synonyms = synonyms;
    }

    /**
     *
     * @param suggesterField EnoSuggesterField object to convert
     * @return the corresponding lunatic model object
     */
    public static SuggesterField toLunaticModel(EnoSuggesterField suggesterField) {
        if(suggesterField == null) {
            return null;
        }

        SuggesterField lunaticField = new SuggesterField();
        lunaticField.setName(suggesterField.getName());
        lunaticField.setLanguage(suggesterField.getLanguage());
        lunaticField.setMin(suggesterField.getMin());
        lunaticField.setStemmer(suggesterField.getStemmer());

        if(suggesterField.getRules() != null) {
            lunaticField.setRules(new FieldRules());
            if (isSoftRule(suggesterField.getRules())) {
                lunaticField.getRules().setRule(FieldRules.SOFT_RULE);
            } else {
                suggesterField.getRules().forEach(pattern -> lunaticField.getRules().addPattern(pattern));
            }
        }

        if(suggesterField.getSynonyms() != null) {
            suggesterField.getSynonyms().forEach((stringKey, stringsValue) ->
                lunaticField.getSynonyms().put(stringKey, stringsValue));
        }

        return lunaticField;
    }

    private static boolean isSoftRule(List<String> enoFieldRules) {
        return enoFieldRules.size() == 1 && FieldRules.SOFT_RULE.equals(enoFieldRules.get(0));
    }

    /**
     * @param enoFields EnoSuggesterField list object to convert
     * @return the corresponding lunatic model list object
     */
    public static List<SuggesterField> toLunaticModelList(List<EnoSuggesterField> enoFields) {
        if(enoFields == null) {
            return new ArrayList<>();
        }
        return enoFields.stream().map(EnoSuggesterField::toLunaticModel).toList();
    }
}
