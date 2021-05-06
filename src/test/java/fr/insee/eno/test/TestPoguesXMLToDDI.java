package fr.insee.eno.test;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xmlunit.diff.Diff;

import fr.insee.eno.generation.PoguesXML2DDIGenerator;
import fr.insee.eno.postprocessing.NoopPostprocessor;
import fr.insee.eno.preprocessing.PoguesXMLPreprocessorGoToTreatment;
import fr.insee.eno.service.GenerationService;

public class TestPoguesXMLToDDI {

	private PoguesXMLPreprocessorGoToTreatment poguesXMLPreprocessorGoToTreatment = new PoguesXMLPreprocessorGoToTreatment();
	
	private PoguesXML2DDIGenerator poguesXML2DDI = new PoguesXML2DDIGenerator();
	
	private NoopPostprocessor noopPostprocessor = new NoopPostprocessor();
	
	private XMLDiff xmlDiff = new XMLDiff();

	@Test
	public void simpleDiffTest() {
		try {
			String basePath = "src/test/resources/pogues-xml-to-ddi";
			GenerationService genService = new GenerationService(poguesXMLPreprocessorGoToTreatment, poguesXML2DDI, noopPostprocessor);

			File in = new File(String.format("%s/in.xml", basePath));
			File outputFile = genService.generateQuestionnaire(in, "xml-pogues-2-ddi-test");
			File expectedFile = new File(String.format("%s/out.xml", basePath));
			Diff diff = xmlDiff.getDiff(outputFile, expectedFile);
			Assertions.assertFalse(diff::hasDifferences, ()->getDiffMessage(diff, basePath));

		} catch (IOException e) {
			e.printStackTrace();
			Assertions.fail();
		} catch (NullPointerException e) {
			e.printStackTrace();
			Assertions.fail();
		} catch (Exception e) {
			e.printStackTrace();
			Assertions.fail();
		}
	}

	private String getDiffMessage(Diff diff, String path) {
		return String.format("Transformed output for %s should match expected XML document:\n %s", path,
				diff.toString());
	}
}
