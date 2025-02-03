package fr.insee.eno.model.fo.alternative;

import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Document;

import java.util.Map;

@Getter
@Setter
public class Sequence {
    private Block block;

    public Sequence(Document document, String label) {
        block = new Block(document);
        block.getElement().setTextContent(label);
    }
}
