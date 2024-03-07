package fr.insee.eno.core.model.suggester;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryParserParamsDTO {

    String language;
    Integer min;
    String pattern;
    Boolean stemmer;

}
