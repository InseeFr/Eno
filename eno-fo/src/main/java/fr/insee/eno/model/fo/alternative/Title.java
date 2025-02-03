package fr.insee.eno.model.fo.alternative;

import org.w3c.dom.Document;


public class Title extends FoObject {
    public Title(Document document) {
        super(document);
    }
    public void setValue(String value){
        this.getElement().setTextContent(value);
    }
}
