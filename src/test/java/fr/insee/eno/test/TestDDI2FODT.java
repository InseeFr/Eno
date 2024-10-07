package fr.insee.eno.test;

import fr.insee.eno.generation.DDI2FODTGenerator;
import fr.insee.eno.postprocessing.NoopPostprocessor;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.preprocessing.*;
import fr.insee.eno.service.GenerationService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlunit.diff.Diff;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import static fr.insee.eno.Constants.createTempEnoFile;

class TestDDI2FODT {

	final Logger logger = LoggerFactory.getLogger(TestDDI2FODT.class);
	
	private DDI2FODTGenerator ddi2fodt = new DDI2FODTGenerator();
	
	private XMLDiff xmlDiff = new XMLDiff();

	
	@Test
	void simpleDiffTest() {
		try {
			String basePath = "src/test/resources/ddi-to-fodt";
			
			Preprocessor[] preprocessors = {

					new DDIMultimodalSelectionPreprocessor(),
					new DDIMarkdown2XhtmlPreprocessor(),
					new DDIDereferencingPreprocessor(),
					new DDICleaningPreprocessor(),
					new DDITitlingPreprocessor()};
			
			Postprocessor[] postprocessors = {new NoopPostprocessor()};
			
			GenerationService genService = new GenerationService(preprocessors, ddi2fodt, postprocessors);
			File in = new File(String.format("%s/in.xml", basePath));
			ByteArrayInputStream inputStream = new ByteArrayInputStream(FileUtils.readFileToByteArray(in));
			File outputFile = createTempEnoFile();
			ByteArrayOutputStream output = genService.generateQuestionnaire(inputStream, "xml-pogues-2-ddi-test");
			try (FileOutputStream fos = new FileOutputStream(outputFile)) {
				fos.write(output.toByteArray());
			}
			output.close();
			File expectedFile = new File(String.format("%s/out.fodt", basePath));
			Diff diff = xmlDiff.getDiff(outputFile,expectedFile);
			Assertions.assertFalse(diff::hasDifferences, ()->getDiffMessage(diff, basePath));
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			Assertions.fail();
		}
	}

	private String getDiffMessage(Diff diff, String path) {
		return String.format("Transformed output for %s should match expected XML document:\n %s", path,
				diff.toString());
	}
}
