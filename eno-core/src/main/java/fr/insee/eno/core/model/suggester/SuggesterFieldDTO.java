package fr.insee.eno.core.model.suggester;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/** Data transfer object for suggester field. */
@Getter
@Setter
public class SuggesterFieldDTO {

    String name;
    List<String> rules;
    String language;
    Integer min;
    Boolean stemmer;
    List<FieldSynonymDTO> synonyms;

}
