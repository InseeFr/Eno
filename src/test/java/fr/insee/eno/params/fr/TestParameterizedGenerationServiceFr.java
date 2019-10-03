package fr.insee.eno.params.fr;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.xmlunit.diff.Diff;

import fr.insee.eno.ParameterizedGenerationService;
import fr.insee.eno.test.XMLDiff;

public class TestParameterizedGenerationServiceFr {

	private ParameterizedGenerationService parameterizedGenerationService = new ParameterizedGenerationService();
	
	private XMLDiff xmlDiff = new XMLDiff();
	
	
	@Test
	public void householdTest() {
		String basePathDDI = "src/test/resources/params/in-to-out/household";
		File input = new File(String.format("%s/ddi.xml", basePathDDI));
		File params = new File(String.format("%s/params.xml", basePathDDI));
		File metadata = new File(String.format("%s/metadata.xml", basePathDDI));
		File specificTreatment = new File(String.format("%s/fr-specific-treatment.xsl", basePathDDI));
		
		try {
			File outputFile = parameterizedGenerationService.generateQuestionnaire(input, params, metadata, specificTreatment);
			File expectedFile = new File(String.format("%s/form.xhtml", basePathDDI));
			Diff diff = xmlDiff.getDiff(outputFile, expectedFile);
			Assert.assertFalse(getDiffMessage(diff, basePathDDI), diff.hasDifferences());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void businessTest() {
		String basePathDDI = "src/test/resources/params/in-to-out/business";
		File input = new File(String.format("%s/ddi.xml", basePathDDI));
		File params = new File(String.format("%s/params.xml", basePathDDI));
		File metadata = new File(String.format("%s/metadata.xml", basePathDDI));
		File specificTreatment = new File(String.format("%s/fr-specific-treatment.xsl", basePathDDI));
		
		try {
			File outputFile = parameterizedGenerationService.generateQuestionnaire(input, params, metadata, specificTreatment);
			File expectedFile = new File(String.format("%s/form.xhtml", basePathDDI));
			Diff diff = xmlDiff.getDiff(outputFile, expectedFile);
			Assert.assertFalse(getDiffMessage(diff, basePathDDI), diff.hasDifferences());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getDiffMessage(Diff diff, String path) {
		return String.format("Transformed output for %s should match expected XML document:\n %s", path,
				diff.toString());
	}
	
}
