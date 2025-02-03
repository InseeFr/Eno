package fr.insee.eno.model.fo;

import lombok.Getter;
import lombok.Setter;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.w3.x1999.xsl.format.BlockDocument;

import static fr.insee.eno.factory.FOUtils.valueOf;

@Getter
@Setter
public class Sequence {
    private BlockDocument.Block block;

    public Sequence(String label){
        block = BlockDocument.Block.Factory.newInstance();
        block.setFontSize(valueOf("14pt"));
        XmlObject textContent = XmlString.Factory.newValue(label);
        block.set(textContent);
    }
}
