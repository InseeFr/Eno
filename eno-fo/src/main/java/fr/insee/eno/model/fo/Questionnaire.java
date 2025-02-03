package fr.insee.eno.model.fo;

import com.fasterxml.jackson.databind.node.TextNode;
import fr.insee.eno.factory.DocumentFactory;
import fr.insee.eno.factory.FOUtils;
import fr.insee.eno.factory.LayoutMasterSetFactory;
import fr.insee.eno.factory.PageSequenceFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.w3.x1999.xsl.format.*;

import javax.xml.parsers.ParserConfigurationException;

import java.util.List;

import static fr.insee.eno.factory.FOUtils.*;

@Getter
@Setter
@Slf4j
public class Questionnaire {

    private RootDocument.Root rootElement;
    private PageSequenceDocument.PageSequence pageSequence;
    private List<Sequence> sequenceList;

    public Questionnaire() {
        rootElement = RootDocument.Root.Factory.newInstance();
        // rootElement.setLayoutMasterSet(LayoutMasterSetFactory.createLayoutMasterSetDocumentDefault());
        rootElement.getPageSequenceList().add(PageSequenceFactory.createEmptyPageSequence());
        pageSequence = rootElement.getPageSequenceArray(0);
        pageSequence.getTitle().setTextAlign(TitleDocument.Title.TextAlign.CENTER);
    }



}
