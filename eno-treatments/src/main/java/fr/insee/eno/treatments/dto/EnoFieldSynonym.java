package fr.insee.eno.treatments.dto;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

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

    public static FieldSynonym toLunaticModel(EnoFieldSynonym enoFieldSynonym) {
        if(enoFieldSynonym == null) {
            return null;
        }
        FieldSynonym fieldSynonym = new FieldSynonym();
        fieldSynonym.setSource(enoFieldSynonym.getSource());
        fieldSynonym.getTarget().addAll(enoFieldSynonym.getTarget());
        return fieldSynonym;
    }

    public static List<FieldSynonym> toLunaticModelList(List<EnoFieldSynonym> enoFieldSynonyms) {
        if(enoFieldSynonyms == null) {
            return new ArrayList<>();
        }
        return enoFieldSynonyms.stream().map(EnoFieldSynonym::toLunaticModel).toList();
    }
}

