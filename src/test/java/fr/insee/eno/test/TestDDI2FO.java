package fr.insee.eno.test;

import fr.insee.eno.generation.DDI2FOGenerator;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.postprocessing.fo.*;
import fr.insee.eno.preprocessing.*;
import fr.insee.eno.service.GenerationService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xmlunit.diff.Diff;

import java.io.*;

import static fr.insee.eno.Constants.createTempEnoFile;

public class TestDDI2FO {
		
	private DDI2FOGenerator ddi2fo = new DDI2FOGenerator();

	private XMLDiff xmlDiff = new XMLDiff();

	@Test
	public void simpleDiffTest() {
		try {
			String basePath = "src/test/resources/ddi-to-fo";
			File in = new File(String.format("%s/in.xml", basePath));

			// Without plugins
			Preprocessor[] preprocessors = {

					new DDIMultimodalSelectionPreprocessor(),
					new DDIMarkdown2XhtmlPreprocessor(),
					new DDIDereferencingPreprocessor(),
					new DDICleaningPreprocessor(),
					new DDITitlingPreprocessor()};
			
			Postprocessor[] postprocessors = { 
					new FOMailingPostprocessor(),
					new FOTableColumnPostprocessorFake(),
					new FOInsertEndQuestionPostprocessor(),
					new FOEditStructurePagesPostprocessor(),
					new FOSpecificTreatmentPostprocessor(),
					new FOInsertCoverPagePostprocessor(),
					new FOInsertAccompanyingMailsPostprocessor()};
			
			GenerationService genService = new GenerationService(preprocessors, ddi2fo, postprocessors);
			ByteArrayInputStream inputStream = new ByteArrayInputStream(FileUtils.readFileToByteArray(in));
			File outputFile = createTempEnoFile();
			ByteArrayOutputStream output = genService.generateQuestionnaire(inputStream, "simpsons");
			try (FileOutputStream fos = new FileOutputStream(outputFile)) {
				fos.write(output.toByteArray());
			}
			output.close();
			File expectedFile = new File(String.format("%s/out.fo", basePath));
			Diff diff = xmlDiff.getDiff(outputFile, expectedFile);

			// With plugins
			// GenerationService genServiceWithPlugins = new
			// GenerationService(new DDIPreprocessor(), new DDI2PDFGenerator(),
			// new PDFStep3TableColumnPostprocessor());
			// File outputFileWithPlugins =
			// genServiceWithPlugins.generateQuestionnaire(in, null);
			// File expectedFileWithPlugins = new
			// File(String.format("%s/simpsons_old-plugin.fo", basePath));
			// diff =
			// xmlDiff.getDiff(outputFileWithPlugins,expectedFileWithPlugins);

			Assertions.assertFalse(diff::hasDifferences, ()->getDiffMessage(diff, basePath));

		} catch (IOException e) {
			e.printStackTrace();
			Assertions.fail();
		} catch (NullPointerException e) {
			e.printStackTrace();
			Assertions.fail();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			Assertions.fail();
		}
	}

	private String getDiffMessage(Diff diff, String path) {
		return String.format("Transformed output for %s should match expected XML document:\n %s", path,
				diff.toString());
	}
}
