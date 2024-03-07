package fr.insee.eno.core.model.suggester;

import fr.insee.eno.core.model.EnoObject;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Root class for xml content written in a CDATA in DDI user attribute value.
 */
@Getter
@Setter
public class SuggesterConfigurationDTO extends EnoObject {

    List<SuggesterFieldDTO> fields = new ArrayList<>();
    Boolean meloto;
    Integer max;
    List<String> stopWords = new ArrayList<>();
    SuggesterOrderDTO order;
    SuggesterQueryParserDTO queryParser;
    String url;
    Integer version;

}
