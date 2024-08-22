package fr.insee.eno.treatments.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.insee.lunatic.model.flat.SuggesterType;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public record EnoSuggesterType(
        @JsonProperty(value = "responseNames", required = true) List<String> responseNames,
        @JsonProperty(value = "name", required = true) String name,
        @JsonProperty(value = "fields", required = true) List<EnoSuggesterField> fields,
        @JsonProperty("meloto") Boolean meloto,
        @JsonProperty("max") BigInteger max,
        @JsonProperty("stopWords") List<String> stopWords,
        @JsonProperty("order") EnoSuggesterOrder order,
        @JsonProperty(value = "queryParser", required = true) EnoSuggesterQueryParser queryParser,
        @JsonProperty("url") String url,
        @JsonProperty(value = "version", required = true) BigInteger version) {

    /**
     * @param enoType EnoSuggester object to convert
     * @return the corresponding lunatic model object
     */
    public static SuggesterType toLunaticModel(EnoSuggesterType enoType) {
        if (enoType == null) {
            return null;
        }
        SuggesterType type = new SuggesterType();
        type.setMeloto(enoType.meloto());
        type.setMax(enoType.max());
        type.setName(enoType.name());
        type.setOrder(EnoSuggesterOrder.toLunaticModel(enoType.order()));
        type.setUrl(enoType.url());
        type.setVersion(enoType.version());
        type.getFields().addAll(EnoSuggesterField.toLunaticModelList(enoType.fields()));
        if (enoType.stopWords() != null) {
            type.getStopWords().addAll(enoType.stopWords());
        }
        type.setQueryParser(EnoSuggesterQueryParser.toLunaticModel(enoType.queryParser()));
        return type;
    }

    /**
     * @param enoTypes EnoSuggesterType list object to convert
     * @return the corresponding lunatic model list object
     */
    public static List<SuggesterType> toLunaticModelList(List<EnoSuggesterType> enoTypes) {
        if (enoTypes == null) {
            return new ArrayList<>();
        }
        return enoTypes.stream().map(EnoSuggesterType::toLunaticModel).toList();
    }
}
