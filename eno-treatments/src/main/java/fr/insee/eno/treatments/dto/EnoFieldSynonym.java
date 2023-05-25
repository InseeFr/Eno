package fr.insee.eno.treatments.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.insee.lunatic.model.flat.FieldSynonym;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
public class EnoFieldSynonym {

    private String source;
    private List<String> target;

    @JsonCreator
    public EnoFieldSynonym(@JsonProperty(value = "source", required = true) String source,
                           @JsonProperty(value = "target", required = true) List<String> target) {
        this.source = source;
        this.target = target;
    }

    /**
     *
     * @param enoFieldSynonym EnoFieldSynonym object
     * @return the corresponding lunatic model object
     */
    public static FieldSynonym toLunaticModel(EnoFieldSynonym enoFieldSynonym) {
        if(enoFieldSynonym == null) {
            return null;
        }
        FieldSynonym fieldSynonym = new FieldSynonym();
        fieldSynonym.setSource(enoFieldSynonym.getSource());
        fieldSynonym.getTarget().addAll(enoFieldSynonym.getTarget());
        return fieldSynonym;
    }

    /**
     *
     * @param enoFieldSynonyms EnoFieldSynonym list object
     * @return the corresponding lunatic model list
     */
    public static List<FieldSynonym> toLunaticModelList(List<EnoFieldSynonym> enoFieldSynonyms) {
        if(enoFieldSynonyms == null) {
            return new ArrayList<>();
        }
        return enoFieldSynonyms.stream().map(EnoFieldSynonym::toLunaticModel).toList();
    }
}

