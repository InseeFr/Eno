package fr.insee.eno.model.fo.alternative;

import fr.insee.eno.factory.DocumentFactory;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Document;

import java.util.List;

@Getter
@Setter
public class Questionnaire {

    private Document document;
    private Root root;

    private List<Sequence> sequences;
    private Title title;

    public Questionnaire(){
        document = DocumentFactory.FACTORY.newDocument();
        root = new Root(document);
        document.appendChild(root.getElement());
    }

    public void computeDocument(){
        Flow flow = new Flow(document, "xsl-region-body", "collapse", "10pt");
        flow.addChildren((FoObject) sequences.stream().map(Sequence::getBlock));
        root.addChild(flow);
    }

}
