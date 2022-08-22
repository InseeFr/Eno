package fr.insee.eno.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

public abstract class EnoObject {

    @Getter
    @Setter
    @JsonIgnore
    private EnoObject parent;

}
