package fr.insee.eno.postprocessing.lunaticxml;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class LunaticXMLVTLParserPostprocessorTest {
	
	@Test
	public void parseToVTLTest_noXPathisSame() {
		
		LunaticXMLVTLParserPostprocessor lunaticXMLVTLParserPostprocessor = new LunaticXMLVTLParserPostprocessor();
		
		assertEquals("Bonjour", lunaticXMLVTLParserPostprocessor.parseToVTL("Bonjour"));
		
		
	}

}
