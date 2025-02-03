package fr.insee.eno.model.fo.alternative;

import org.w3c.dom.Document;

import java.util.Map;

public class Root extends FoObject {
    public Root(Document document) {
        super(document, Map.of( "xmlns:fo","http://www.w3.org/1999/XSL/Format"));
    }
}
