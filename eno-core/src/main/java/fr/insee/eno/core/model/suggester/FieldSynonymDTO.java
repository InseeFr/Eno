package fr.insee.eno.core.model.suggester;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FieldSynonymDTO {

    String source;
    List<String> target;

}
