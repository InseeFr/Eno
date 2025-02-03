package fr.insee.eno.model.fo.alternative;

import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Document;

import java.util.Map;

@Getter
@Setter
public class TestElement extends FoObject {
    public TestElement(Document document, String testAttribute){
        super(document, Map.of("testAttribute", testAttribute));
    }
}
