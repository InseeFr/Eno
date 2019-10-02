package fr.insee.eno.test;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.xmlunit.diff.Diff;

import fr.insee.eno.GenerationService;
import fr.insee.eno.generation.DDI2ODTGenerator;
import fr.insee.eno.postprocessing.NoopPostprocessor;
import fr.insee.eno.preprocessing.DDIPreprocessor;

public class TestDDIToODT {
	
	private DDIPreprocessor ddiPreprocessor = new DDIPreprocessor();
	
	private DDI2ODTGenerator ddi2odt = new DDI2ODTGenerator();
	
	private XMLDiff xmlDiff = new XMLDiff();

	
	@Test
	public void simpleDiffTest() {
		try {
			String basePath = "src/test/resources/ddi-to-odt";
			GenerationService genService = new GenerationService(ddiPreprocessor, ddi2odt,
					new NoopPostprocessor());
			File in = new File(String.format("%s/in.xml", basePath));
			File outputFile = genService.generateQuestionnaire(in, "ddi-2-odt-test");
			File expectedFile = new File(String.format("%s/out.odt", basePath));
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
