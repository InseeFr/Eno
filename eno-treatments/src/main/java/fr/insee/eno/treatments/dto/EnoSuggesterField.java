//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.3.2 
// Voir <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2023.05.11 à 10:17:07 AM CEST 
//


package fr.insee.eno.treatments.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.insee.lunatic.model.flat.SuggesterField;
import lombok.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Data
public class EnoSuggesterField {

    public static final String SOFT_RULE_VALUE = "soft";

    private String name;
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<String> rules;
    private String language;
    private BigInteger min;
    private Boolean stemmer;
    private List<EnoFieldSynonym> synonyms;

    @JsonCreator
    public EnoSuggesterField(@JsonProperty(value = "name", required = true) String name,
                             @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                             @JsonProperty("rules") List<String> rules,
                             @JsonProperty("language") String language,
                             @JsonProperty("min") BigInteger min,
                             @JsonProperty("stemmer") Boolean stemmer,
                             @JsonProperty("synonyms") List<EnoFieldSynonym> synonyms) {
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
        if(suggesterField.getSynonyms() != null) {
            lunaticField.getSynonyms().addAll(EnoFieldSynonym.toLunaticModelList(suggesterField.getSynonyms()));
        }

        List<String> rules = suggesterField.getRules();

        if (rules == null || rules.isEmpty()) {
            return lunaticField;
        }

        String rule = rules.get(0);
        if (rules.size() == 1 && EnoSuggesterField.SOFT_RULE_VALUE.equals(rule)) {
            lunaticField.setRules(rule);
        } else {
            lunaticField.getRulesA().addAll(rules);
        }

        return lunaticField;
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
