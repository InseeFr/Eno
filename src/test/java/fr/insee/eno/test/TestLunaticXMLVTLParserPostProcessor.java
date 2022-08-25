package fr.insee.eno.test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import fr.insee.eno.exception.EnoGenerationException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xmlunit.diff.Diff;

import fr.insee.eno.Constants;
import fr.insee.eno.postprocessing.lunaticxml.LunaticXMLVTLParserPostprocessor;

public class TestLunaticXMLVTLParserPostProcessor {

	private LunaticXMLVTLParserPostprocessor lunaticXMLVtlParserPostprocessor = new LunaticXMLVTLParserPostprocessor();
	private XMLDiff xmlDiff = new XMLDiff();
		
	@Test
	public void simpleTest() {
		
		String test1 = "concat(x,y,z, \"concat(a,b,c)\")";
		String expected1 = "x || y || z ||  \"concat(a,b,c)\"";		

		String test2 = "substring(x,y,z)";
		String expected2 = "substr(x,y,z)";
		
		String test3 = "x!=y";
		String expected3 = "x &lt;&gt; y";
		
		String test4 = "x div y";
		String expected4 = "x / y";
		
		String test5 = "x != '1'";
		String expected5 ="x  &lt;&gt;  \"1\"";
		
		String test6 = "\"x != '1'\"";
		String expected6 ="\"x != '1'\"";
		
		String test7 = "concat(substring(x,y,z),a,concat(x,substring(x1,y1,z1)),b)!='abc'";
		String expected7 = "substr(x,y,z) || a || x || substr(x1,y1,z1) || b &lt;&gt; \"abc\"";

		String test8 = "cast(cast(ABCD,integer),string) = '3'";
		String expected8 = "cast(cast(ABCD,integer),string) = \"3\"";
		
		String test9 = "cast(cast(ABCD,string),integer) = '3'";
		String expected9 = "cast(cast(ABCD,string),integer) = 3";

		String test10 = "cast(cast(ABCD,string),integer) = null";
		String expected10 = "isnull(cast(cast(ABCD,string),integer))";
		
		Assertions.assertEquals(expected1, lunaticXMLVtlParserPostprocessor.parseToVTL(test1));
		Assertions.assertEquals(expected2, lunaticXMLVtlParserPostprocessor.parseToVTL(test2));
		Assertions.assertEquals(expected3, lunaticXMLVtlParserPostprocessor.parseToVTL(test3));
		Assertions.assertEquals(expected4, lunaticXMLVtlParserPostprocessor.parseToVTL(test4));
		Assertions.assertEquals(expected5, lunaticXMLVtlParserPostprocessor.parseToVTL(test5));
		Assertions.assertEquals(expected6, lunaticXMLVtlParserPostprocessor.parseToVTL(test6));
		Assertions.assertEquals(expected7, lunaticXMLVtlParserPostprocessor.parseToVTL(test7));
		Assertions.assertEquals(expected8, lunaticXMLVtlParserPostprocessor.parseToVTL(test8));
		Assertions.assertEquals(expected9, lunaticXMLVtlParserPostprocessor.parseToVTL(test9));
		Assertions.assertEquals(expected10, lunaticXMLVtlParserPostprocessor.parseToVTL(test10));
	}
	
	@Test
	public void simpleWithFileTest() {
		try {
			Path basePath = Path.of(TestLunaticXMLVTLParserPostProcessor.class.getResource("/lunatic-xml-vtl-parsing").toURI());

			Path outPath = Paths.get(Constants.TEMP_FOLDER_PATH + "/test-vtl.xml");
			Files.deleteIfExists(outPath);
			Path outputFilePath = Files.createFile(outPath);
			File in = basePath.resolve("in.xml").toFile();
			File outPostProcessing = lunaticXMLVtlParserPostprocessor.process(in, null, "test");
			FileUtils.copyFile(outPostProcessing,outputFilePath.toFile());
			FileUtils.forceDelete(outPostProcessing);
			File expectedFile = basePath.resolve("out.xml").toFile();
			Diff diff = xmlDiff.getDiff(outputFilePath.toFile(),expectedFile);
			Assertions.assertFalse(diff::hasDifferences, ()->getDiffMessage(diff, basePath.toString()));
			
		} catch (IOException | EnoGenerationException | URISyntaxException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Assertions.fail();
		}
	}
	
	private String getDiffMessage(Diff diff, String path) {
		return String.format("Transformed output for %s should match expected XML document:\n %s", path,
				diff.toString());
	}

	
}
