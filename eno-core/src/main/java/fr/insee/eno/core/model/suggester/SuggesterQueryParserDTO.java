package fr.insee.eno.core.model.suggester;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuggesterQueryParserDTO {

    String type;
    QueryParserParamsDTO params;

}
