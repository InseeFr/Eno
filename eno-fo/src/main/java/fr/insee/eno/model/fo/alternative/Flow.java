package fr.insee.eno.model.fo.alternative;

import org.w3c.dom.Document;

import java.util.Map;

public class Flow extends FoObject {

    public Flow(Document document, String flowName, String borderCollapse, String fontSize) {
        super(document, Map.of(
                "flowName",flowName,
                "borderCollapse",borderCollapse,
                "fontSize",fontSize
        ));
    }
}
