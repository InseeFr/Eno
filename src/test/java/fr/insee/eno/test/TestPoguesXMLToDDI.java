package fr.insee.eno.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.xmlunit.diff.Diff;

import fr.insee.eno.GenerationService;
import fr.insee.eno.generation.PoguesXML2DDIGenerator;
import fr.insee.eno.postprocessing.DDIPostprocessor;
import fr.insee.eno.preprocessing.PoguesXMLPreprocessor;

public class TestPoguesXMLToDDI {

	private XMLDiff xmlDiff = new XMLDiff();

	@Test
	public void simpleDiffTest() {
		try {
			String basePath = "src/test/resources/pogues-xml-to-ddi";
			GenerationService genService = new GenerationService(new PoguesXMLPreprocessor(), new PoguesXML2DDIGenerator(),
					new DDIPostprocessor());
			File in = new File(String.format("%s/in.xml", basePath));
			File output = genService.generateQuestionnaire(in, null);
			InputStream out = new FileInputStream(output);
			String expectedFilePath = String.format("%s/out.xml", basePath);
			Diff diff = xmlDiff.getDiff(out,expectedFilePath);
			Assert.assertFalse(getDiffMessage(diff, basePath), diff.hasDifferences());
			
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (NullPointerException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private String getDiffMessage(Diff diff, String path) {
		return String.format("Transformed output for %s should match expected DDI document:\n %s", path,
				diff.toString());
	}
}
