package fr.insee.eno.test;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.xmlunit.diff.Diff;

import fr.insee.eno.generation.DDI2JSGenerator;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.postprocessing.js.JSExternalizeVariablesPostprocessor;
import fr.insee.eno.postprocessing.js.JSInsertGenericQuestionsPostprocessor;
import fr.insee.eno.postprocessing.js.JSSortComponentsPostprocessor;
import fr.insee.eno.postprocessing.js.JSVTLParserPostprocessor;
import fr.insee.eno.service.GenerationService;
import fr.insee.eno.preprocessing.DDICleaningPreprocessor;
import fr.insee.eno.preprocessing.DDIDereferencingPreprocessor;
import fr.insee.eno.preprocessing.DDITitlingPreprocessor;
import fr.insee.eno.preprocessing.Preprocessor;

public class TestDDIToJS {
	
	private DDI2JSGenerator ddi2js = new DDI2JSGenerator();
	
	private XMLDiff xmlDiff = new XMLDiff();
	
	@Test
	public void simpleDiffTest() {
		try {
			String basePath = "src/test/resources/ddi-to-js";
			Preprocessor[] preprocessors = {
					new DDIDereferencingPreprocessor(),
					new DDICleaningPreprocessor(),
					new DDITitlingPreprocessor()};
			
			Postprocessor[] postprocessors =  {
					new JSSortComponentsPostprocessor(),
					new JSInsertGenericQuestionsPostprocessor(),
					new JSExternalizeVariablesPostprocessor(),
					new JSVTLParserPostprocessor()};
			GenerationService genService = new GenerationService(preprocessors, ddi2js, postprocessors);
			
			File in = new File(String.format("%s/in.xml", basePath));
			File outputFile = genService.generateQuestionnaire(in, "ddi-2-js-test");
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
