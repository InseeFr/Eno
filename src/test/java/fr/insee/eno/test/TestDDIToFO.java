package fr.insee.eno.test;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.xmlunit.diff.Diff;

import fr.insee.eno.generation.DDI2PDFGenerator;
import fr.insee.eno.postprocessing.PDFEditStructurePagesPostprocessor;
import fr.insee.eno.postprocessing.PDFInsertAccompanyingMailsPostprocessor;
import fr.insee.eno.postprocessing.PDFInsertCoverPagePostprocessor;
import fr.insee.eno.postprocessing.PDFInsertEndQuestionPostprocessor;
import fr.insee.eno.postprocessing.PDFMailingPostprocessor;
import fr.insee.eno.postprocessing.PDFSpecificTreatmentPostprocessor;
import fr.insee.eno.postprocessing.PDFTableColumnPostprocessorFake;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.preprocessing.DDIPreprocessor;
import fr.insee.eno.service.GenerationService;

public class TestDDIToFO {
	
	private DDIPreprocessor ddiPreprocessor = new DDIPreprocessor();
	
	private DDI2PDFGenerator ddi2pdf = new DDI2PDFGenerator();

	private XMLDiff xmlDiff = new XMLDiff();

	@Test
	public void simpleDiffTest() {
		try {
			String basePath = "src/test/resources/ddi-to-fo";
			File in = new File(String.format("%s/in.xml", basePath));
			Diff diff = null;

			// Without plugins
			GenerationService genService = new GenerationService(ddiPreprocessor, ddi2pdf,
					new Postprocessor[] { 
							new PDFMailingPostprocessor(),
							new PDFTableColumnPostprocessorFake(),
							new PDFInsertEndQuestionPostprocessor(),
							new PDFEditStructurePagesPostprocessor(),
							new PDFSpecificTreatmentPostprocessor(),
							new PDFInsertCoverPagePostprocessor(),
							new PDFInsertAccompanyingMailsPostprocessor()});
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
