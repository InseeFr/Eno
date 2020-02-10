package fr.insee.eno.test;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.xmlunit.diff.Diff;

import fr.insee.eno.GenerationService;
import fr.insee.eno.generation.DDI2PDFGenerator;
import fr.insee.eno.postprocessing.pdf.PDFEditStructurePagesPostprocessor;
import fr.insee.eno.postprocessing.pdf.PDFInsertAccompanyingMailsPostprocessor;
import fr.insee.eno.postprocessing.pdf.PDFInsertCoverPagePostprocessor;
import fr.insee.eno.postprocessing.pdf.PDFInsertEndQuestionPostprocessor;
import fr.insee.eno.postprocessing.pdf.PDFMailingPostprocessor;
import fr.insee.eno.postprocessing.pdf.PDFSpecificTreatmentPostprocessor;
import fr.insee.eno.postprocessing.pdf.PDFTableColumnPostprocessorFake;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.preprocessing.DDICleaningPreprocessor;
import fr.insee.eno.preprocessing.DDIDereferencingPreprocessor;
import fr.insee.eno.preprocessing.DDITitlingPreprocessor;
import fr.insee.eno.preprocessing.Preprocessor;

public class TestDDIToFO {
		
	private DDI2PDFGenerator ddi2pdf = new DDI2PDFGenerator();

	private XMLDiff xmlDiff = new XMLDiff();

	@Test
	public void simpleDiffTest() {
		try {
			String basePath = "src/test/resources/ddi-to-fo";
			File in = new File(String.format("%s/in.xml", basePath));
			Diff diff = null;

			// Without plugins
			Preprocessor[] preprocessors = {
					new DDIDereferencingPreprocessor(),
					new DDICleaningPreprocessor(),
					new DDITitlingPreprocessor()};
			
			Postprocessor[] postprocessors = { 
					new PDFMailingPostprocessor(),
					new PDFTableColumnPostprocessorFake(),
					new PDFInsertEndQuestionPostprocessor(),
					new PDFEditStructurePagesPostprocessor(),
					new PDFSpecificTreatmentPostprocessor(),
					new PDFInsertCoverPagePostprocessor(),
					new PDFInsertAccompanyingMailsPostprocessor()};
			
			GenerationService genService = new GenerationService(preprocessors, ddi2pdf, postprocessors);
			File outputFile = genService.generateQuestionnaire(in, "simpsons");
			File expectedFile = new File(String.format("%s/out.fo", basePath));
			diff = xmlDiff.getDiff(outputFile, expectedFile);

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
