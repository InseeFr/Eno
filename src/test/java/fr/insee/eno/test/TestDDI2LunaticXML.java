package fr.insee.eno.test;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.xmlunit.diff.Diff;

import fr.insee.eno.generation.DDI2LunaticXMLGenerator;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.postprocessing.lunaticxml.LunaticXMLExternalizeVariablesPostprocessor;
import fr.insee.eno.postprocessing.lunaticxml.LunaticXMLInsertGenericQuestionsPostprocessor;
import fr.insee.eno.postprocessing.lunaticxml.LunaticXMLSortComponentsPostprocessor;
import fr.insee.eno.postprocessing.lunaticxml.LunaticXMLVTLParserPostprocessor;
import fr.insee.eno.service.GenerationService;
import fr.insee.eno.preprocessing.DDICleaningPreprocessor;
import fr.insee.eno.preprocessing.DDIDereferencingPreprocessor;
import fr.insee.eno.preprocessing.DDITitlingPreprocessor;
import fr.insee.eno.preprocessing.Preprocessor;

public class TestDDI2LunaticXML {
	
	private DDI2LunaticXMLGenerator ddi2lunaticXML = new DDI2LunaticXMLGenerator();
	
	private XMLDiff xmlDiff = new XMLDiff();
	
	@Test
	public void simpleDiffTest() {
		try {
			String basePath = "src/test/resources/ddi-to-lunatic-xml";
			Preprocessor[] preprocessors = {
					new DDIDereferencingPreprocessor(),
					new DDICleaningPreprocessor(),
					new DDITitlingPreprocessor()};
			
			Postprocessor[] postprocessors =  {
					new LunaticXMLSortComponentsPostprocessor(),
					new LunaticXMLInsertGenericQuestionsPostprocessor(),
					new LunaticXMLExternalizeVariablesPostprocessor(),
					new LunaticXMLVTLParserPostprocessor()};
			GenerationService genService = new GenerationService(preprocessors, ddi2lunaticXML, postprocessors);
			
			File in = new File(String.format("%s/in.xml", basePath));
			File outputFile = genService.generateQuestionnaire(in, "ddi-2-lunatic-xml-test");
			File expectedFile = new File(String.format("%s/out.xml", basePath));
			Diff diff = xmlDiff.getDiff(outputFile,expectedFile);
			Assert.assertFalse(getDiffMessage(diff, basePath), diff.hasDifferences());
			
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (NullPointerException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			Assert.fail();
		}
	}
	
	private String getDiffMessage(Diff diff, String path) {
		return String.format("Transformed output for %s should match expected XML document:\n %s", path,
				diff.toString());
	}
}
