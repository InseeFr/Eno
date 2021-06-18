package fr.insee.eno.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xmlunit.diff.Diff;

import fr.insee.eno.Constants;
import fr.insee.eno.postprocessing.lunaticxml.LunaticXMLVTLParserPostprocessor;
public class TestLunaticXMLPostProcessorVTLParser {
	
	private LunaticXMLVTLParserPostprocessor lunaticXMLVtlParserPostprocessor = new LunaticXMLVTLParserPostprocessor();
	private XMLDiff xmlDiff = new XMLDiff();
	
	@Test
	public void simpleWithFileTest() {
		try {
			String basePath = "src/test/resources/lunatic-xml-vtl-parsing";
			
			Path outPath = Paths.get(Constants.TEMP_FOLDER_PATH + "/test-vtl.xml");
			Files.deleteIfExists(outPath);
			Path outputFilePath = Files.createFile(outPath);
			File in = new File(String.format("%s/in.xml", basePath));
			File outPostProcessing = lunaticXMLVtlParserPostprocessor.process(in, null, "test");
			FileUtils.copyFile(outPostProcessing,outputFilePath.toFile());
			FileUtils.forceDelete(outPostProcessing);
			File expectedFile = new File(String.format("%s/out.xml", basePath));
			Diff diff = xmlDiff.getDiff(outputFilePath.toFile(),expectedFile);
			Assertions.assertFalse(diff::hasDifferences, ()->getDiffMessage(diff, basePath));
			
		} catch (IOException e) {
			e.printStackTrace();
			Assertions.fail();
		} catch (NullPointerException e) {
			e.printStackTrace();
			Assertions.fail();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			Assertions.fail();
		} 
	}
	
	private String getDiffMessage(Diff diff, String path) {
		return String.format("Transformed output for %s should match expected XML document:\n %s", path,
				diff.toString());
	}


}
