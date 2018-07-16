package fr.insee.eno.test;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.xmlunit.diff.Diff;

import fr.insee.eno.GenerationService;
import fr.insee.eno.generation.DDI2PDFGenerator;
import fr.insee.eno.postprocessing.NoopPostprocessor;
import fr.insee.eno.postprocessing.PDFPostprocessor;
import fr.insee.eno.preprocessing.DDIPreprocessor;

public class TestDDIToFO {

	private XMLDiff xmlDiff = new XMLDiff();

	
	//@Test
	public void simpleDiffTest() {
		try {
			String basePath = "src/test/resources/ddi-to-fo";
			File in = new File(String.format("%s/simpsons_old.xml", basePath));
			Diff diff = null;
			
			// Without plugins
			GenerationService genService = new GenerationService(new DDIPreprocessor(), new DDI2PDFGenerator(),
					new NoopPostprocessor());
			File outputFile = genService.generateQuestionnaire(in, null);
			File expectedFile = new File(String.format("%s/simpsons_old.fo", basePath));
			diff = xmlDiff.getDiff(outputFile,expectedFile);
			
			// With plugins
//			GenerationService genServiceWithPlugins = new GenerationService(new DDIPreprocessor(), new DDI2PDFGenerator(),
//					new PDFPostprocessor());
//			File outputFileWithPlugins = genServiceWithPlugins.generateQuestionnaire(in, null);
//			File expectedFileWithPlugins = new File(String.format("%s/simpsons_old-plugin.fo", basePath));
//			diff = xmlDiff.getDiff(outputFileWithPlugins,expectedFileWithPlugins);
			
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
