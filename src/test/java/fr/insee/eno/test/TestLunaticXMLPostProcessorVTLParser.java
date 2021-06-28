package fr.insee.eno.test;

import fr.insee.eno.Constants;
import fr.insee.eno.postprocessing.lunaticxml.LunaticXMLVTLParserPostprocessor;
import fr.insee.eno.utils.Xpath2VTLParser;
import org.junit.jupiter.api.Test;
import org.xmlunit.diff.Diff;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TestLunaticXMLPostProcessorVTLParser {

	private final XMLDiff xmlDiff = new XMLDiff();

	@Test
	public void simpleWithFileTest() throws IOException {
			String basePath = "src/test/resources/lunatic-xml-vtl-parsing";
			
			Path outPath = Paths.get(Constants.TEMP_FOLDER_PATH + "/test-vtl.xml");
			Files.deleteIfExists(outPath);
			Path outputFilePath = Files.createFile(outPath);
			Path inPath=Path.of(basePath).resolve("in.xml");

		Xpath2VTLParser xpath2VTLParser=new Xpath2VTLParser(
				Set.of(
						LunaticXMLVTLParserPostprocessor.XML_NODE_CONDITIONFILTER,
						LunaticXMLVTLParserPostprocessor.XML_NODE_VALUE,
						LunaticXMLVTLParserPostprocessor.XML_NODE_LABEL,
						LunaticXMLVTLParserPostprocessor.XML_NODE_EXPRESSION
				), null);
		assertDoesNotThrow(()->xpath2VTLParser.parseXPathToVTLFromInputStreamInNodes(Files.newInputStream(inPath), Files.newOutputStream(outputFilePath)));

		File expectedFile = new File(String.format("%s/out.xml", basePath));
		try {
			Diff diff = xmlDiff.getDiff(outputFilePath.toFile(),expectedFile);
			assertFalse(diff::hasDifferences, ()->getDiffMessage(diff, basePath));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}
	
	private String getDiffMessage(Diff diff, String path) {
		return String.format("Transformed output for %s should match expected XML document:\n %s", path,
				diff.toString());
	}


}
