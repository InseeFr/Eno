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

public class TestParameterizedGenerationServiceXFORMS {

	private ParameterizedGenerationService parameterizedGenerationService = new ParameterizedGenerationService();

	private XMLDiff xmlDiff = new XMLDiff();

	@Test
	public void defaultTest() {
		String basePathDDI = "src/test/resources/params/in-to-out/default";
		File input = new File(String.format("%s/ddi.xml", basePathDDI));
		File params = new File(String.format("%s/params-xforms.xml", basePathDDI));

		try {
			File outputFile = createTempEnoFile();
			ByteArrayOutputStream output = parameterizedGenerationService.generateQuestionnaire(
					new ByteArrayInputStream(FileUtils.readFileToByteArray(input)),
					new ByteArrayInputStream(FileUtils.readFileToByteArray(params)),
					null,
					null,
					null);
			try (FileOutputStream fos = new FileOutputStream(outputFile)) {
				fos.write(output.toByteArray());
			}
			output.close();
			File expectedFile = new File(String.format("%s/form.xhtml", basePathDDI));
			Diff diff = xmlDiff.getDiff(outputFile, expectedFile);
			Assertions.assertFalse(diff::hasDifferences, () -> getDiffMessage(diff, basePathDDI));
			FileUtils.delete(outputFile);
		} catch (Exception e) {
			Assertions.fail();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void householdTest() throws Exception {
		String basePathDDI = "src/test/resources/params/in-to-out/household";
		File input = new File(String.format("%s/ddi.xml", basePathDDI));
		File params = new File(String.format("%s/params-xforms.xml", basePathDDI));
		File metadata = new File(String.format("%s/metadata.xml", basePathDDI));

		File outputFile = createTempEnoFile();
		ByteArrayOutputStream output = parameterizedGenerationService.generateQuestionnaire(
				new ByteArrayInputStream(FileUtils.readFileToByteArray(input)),
				new ByteArrayInputStream(FileUtils.readFileToByteArray(params)),
				new ByteArrayInputStream(FileUtils.readFileToByteArray(metadata)),
				null,
				null);
		try (FileOutputStream fos = new FileOutputStream(outputFile)) {
			fos.write(output.toByteArray());
		}
		output.close();
		File expectedFile = new File(String.format("%s/form.xhtml", basePathDDI));
		Diff diff = xmlDiff.getDiff(outputFile, expectedFile);
		Assertions.assertFalse(diff::hasDifferences, () -> getDiffMessage(diff, basePathDDI));
		FileUtils.delete(outputFile);

	}

	@Test
	public void businessTest() {
		String basePathDDI = "src/test/resources/params/in-to-out/business";
		File input = new File(String.format("%s/ddi.xml", basePathDDI));
		File params = new File(String.format("%s/params-xforms.xml", basePathDDI));
		File metadata = new File(String.format("%s/metadata.xml", basePathDDI));
		File specificTreatment = new File(String.format("%s/xforms-specific-treatment.xsl", basePathDDI));

		try {
			File outputFile = createTempEnoFile();
			ByteArrayOutputStream output = parameterizedGenerationService.generateQuestionnaire(
					new ByteArrayInputStream(FileUtils.readFileToByteArray(input)),
					new ByteArrayInputStream(FileUtils.readFileToByteArray(params)),
					new ByteArrayInputStream(FileUtils.readFileToByteArray(metadata)),
					new ByteArrayInputStream(FileUtils.readFileToByteArray(specificTreatment)),
					null);
			try (FileOutputStream fos = new FileOutputStream(outputFile)) {
				fos.write(output.toByteArray());
			}
			output.close();
			File expectedFile = new File(String.format("%s/form.xhtml", basePathDDI));
			Diff diff = xmlDiff.getDiff(outputFile, expectedFile);
			Assertions.assertFalse(diff::hasDifferences, () -> getDiffMessage(diff, basePathDDI));
			FileUtils.delete(outputFile);
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
