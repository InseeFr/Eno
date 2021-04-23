package fr.insee.eno.params.generation;

import java.io.File;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xmlunit.diff.Diff;

import fr.insee.eno.service.ParameterizedGenerationService;
import fr.insee.eno.test.XMLDiff;

public class TestParameterizedGenerationServiceXFORMS {
	

	private ParameterizedGenerationService parameterizedGenerationService = new ParameterizedGenerationService();
	
	private XMLDiff xmlDiff = new XMLDiff();
	
	
	@Test
	public void defaultTest() {
		String basePathDDI = "src/test/resources/params/in-to-out/default";
		File input = new File(String.format("%s/ddi.xml", basePathDDI));
		File params = new File(String.format("%s/params-xforms.xml", basePathDDI));
		
		try {
			File outputFile = parameterizedGenerationService.generateQuestionnaire(input, params, null, null, null);
			File expectedFile = new File(String.format("%s/form.xhtml", basePathDDI));
			Diff diff = xmlDiff.getDiff(outputFile, expectedFile);
			Assertions.assertFalse(diff::hasDifferences, ()->getDiffMessage(diff, basePathDDI));
		} catch (Exception e) {
			Assertions.fail();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void householdTest() {
		String basePathDDI = "src/test/resources/params/in-to-out/household";
		File input = new File(String.format("%s/ddi.xml", basePathDDI));
		File params = new File(String.format("%s/params-xforms.xml", basePathDDI));
		File metadata = new File(String.format("%s/metadata.xml", basePathDDI));
		
		try {
			File outputFile = parameterizedGenerationService.generateQuestionnaire(input, params, metadata, null, null);
			File expectedFile = new File(String.format("%s/form.xhtml", basePathDDI));
			Diff diff = xmlDiff.getDiff(outputFile, expectedFile);
			Assertions.assertFalse(diff::hasDifferences, ()->getDiffMessage(diff, basePathDDI));
		} catch (Exception e) {
			Assertions.fail();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void businessTest() {
		String basePathDDI = "src/test/resources/params/in-to-out/business";
		File input = new File(String.format("%s/ddi.xml", basePathDDI));
		File params = new File(String.format("%s/params-xforms.xml", basePathDDI));
		File metadata = new File(String.format("%s/metadata.xml", basePathDDI));
		File specificTreatment = new File(String.format("%s/xforms-specific-treatment.xsl", basePathDDI));
		
		try {
			File outputFile = parameterizedGenerationService.generateQuestionnaire(input, params, metadata, specificTreatment, null);
			File expectedFile = new File(String.format("%s/form.xhtml", basePathDDI));
			Diff diff = xmlDiff.getDiff(outputFile, expectedFile);
			Assertions.assertFalse(diff::hasDifferences, ()->getDiffMessage(diff, basePathDDI));
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
