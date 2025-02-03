package fr.insee.eno;

import fr.insee.eno.factory.DocumentFactory;
import fr.insee.eno.model.fo.Questionnaire;
import fr.insee.eno.model.fo.alternative.TestElement;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;


public class QuestionnaireFoTest {

    @Test
    void createFoQuestionnaire(){
        Questionnaire questionnaire = new Questionnaire();
    }

    @Test
    void createEmpty(){
        Document document = DocumentFactory.FACTORY.newDocument();
        TestElement testElement = new TestElement(document, "test");


        document.appendChild(testElement.getElement());
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = tf.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        try {
            transformer.transform(new DOMSource(document), new StreamResult(writer));
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
        System.out.println(writer.getBuffer().toString());
    }
}
