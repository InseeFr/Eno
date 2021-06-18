package fr.insee.eno.postprocessing.lunaticxml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import fr.insee.eno.utils.Xpath2VTLParser;
import org.junit.jupiter.api.Test;

public class LunaticXMLVTLParserPostprocessorTest {
	
	@Test
	public void parseToVTLTest_noXPathisSame() {
		
	
		assertEquals("Bonjour", Xpath2VTLParser.parseToVTL("Bonjour"));
		
		
	}

}
