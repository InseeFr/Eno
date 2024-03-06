package fr.insee.eno.params.generation;

import fr.insee.eno.service.ParameterizedGenerationService;
import fr.insee.eno.test.XMLDiff;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xmlunit.diff.Diff;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import static fr.insee.eno.Constants.createTempEnoFile;

public class TestParameterizedGenerationServiceLunaticXML {
	

	private ParameterizedGenerationService parameterizedGenerationService = new ParameterizedGenerationService();
	
	private XMLDiff xmlDiff = new XMLDiff();
	
	
	@Test
	public void defaultTest() {
		String basePathDDI = "src/test/resources/params/in-to-out/default";
		File input = new File(String.format("%s/ddi.xml", basePathDDI));
		File params = new File(String.format("%s/params-lunatic-xml.xml", basePathDDI));
		
		try {
			File outputFile = createTempEnoFile();
			ByteArrayOutputStream output = parameterizedGenerationService.generateQuestionnaire(
					new ByteArrayInputStream(FileUtils.readFileToByteArray(input)),
					new ByteArrayInputStream(FileUtils.readFileToByteArray(params)),
					null, null, null);
			try (FileOutputStream fos = new FileOutputStream(outputFile)) {
				fos.write(output.toByteArray());
			}
			output.close();
			File expectedFile = new File(String.format("%s/form.xml", basePathDDI));
			Diff diff = xmlDiff.getDiff(outputFile, expectedFile);
			Assertions.assertFalse(diff::hasDifferences, ()->getDiffMessage(diff, basePathDDI));
			FileUtils.delete(outputFile);
		} catch (Exception e) {
			Assertions.fail();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private String getDiffMessage(Diff diff, String path) {
		return String.format("Transformed output for %s should match expected XML document:\n %s", path,
				diff.toString());
	}
	
}
