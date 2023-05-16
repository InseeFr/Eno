//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.3.2 
// Voir <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2023.05.11 à 10:17:07 AM CEST 
//


package fr.insee.eno.treatments.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.insee.lunatic.model.flat.SuggesterType;
import lombok.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Data
public class EnoSuggesterType {

    private List<String> responseNames;
    private String name;
    private List<EnoSuggesterField> fields;
    private BigInteger max;
    private List<String> stopWords;
    private EnoSuggesterOrder order;
    private EnoSuggesterQueryParser queryParser;
    private String url;
    private String version;

    @JsonCreator
    public EnoSuggesterType(@JsonProperty(value = "responseNames", required = true) List<String> responseNames,
                            @JsonProperty(value = "name", required = true) String name,
                            @JsonProperty(value = "fields", required = true) List<EnoSuggesterField> fields,
                            @JsonProperty("max") BigInteger max,
                            @JsonProperty("stopWords")  List<String> stopWords,
                            @JsonProperty("order") EnoSuggesterOrder order,
                            @JsonProperty(value = "queryParser", required = true) EnoSuggesterQueryParser queryParser,
                            @JsonProperty("url") String url,
                            @JsonProperty(value = "version", required = true) String version) {
        this.responseNames = responseNames;
        this.name = name;
        this.fields = fields;
        this.max = max;
        this.stopWords = stopWords;
        this.order = order;
        this.queryParser = queryParser;
        this.url = url;
        this.version = version;
    }

    public static SuggesterType toLunaticModel(EnoSuggesterType enoType) {
        if(enoType == null) {
            return null;
        }
        SuggesterType type = new SuggesterType();
        type.setMax(enoType.getMax());
        type.setName(enoType.getName());
        type.setOrder(EnoSuggesterOrder.toLunaticModel(enoType.getOrder()));
        type.setUrl(enoType.getUrl());
        type.setVersion(enoType.getVersion());
        type.getFields().addAll(EnoSuggesterField.toLunaticModelList(enoType.getFields()));
        if(enoType.getStopWords() != null) {
            type.getStopWords().addAll(enoType.getStopWords());
        }
        type.setQueryParser(EnoSuggesterQueryParser.toLunaticModel(enoType.getQueryParser()));
        return type;
    }

    public static List<SuggesterType> toLunaticModelList(List<EnoSuggesterType> enoTypes) {
        if(enoTypes == null) {
            return new ArrayList<>();
        }
        return enoTypes.stream().map(EnoSuggesterType::toLunaticModel).toList();
    }
}
