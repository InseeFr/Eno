package fr.insee.eno.main;

import java.io.File;

import org.junit.Test;

import fr.insee.eno.GenerationService;
import fr.insee.eno.generation.DDI2PDFGenerator;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.postprocessing.pdf.PDFEditStructurePagesPostprocessor;
import fr.insee.eno.postprocessing.pdf.PDFInsertAccompanyingMailsPostprocessor;
import fr.insee.eno.postprocessing.pdf.PDFInsertCoverPagePostprocessor;
import fr.insee.eno.postprocessing.pdf.PDFInsertEndQuestionPostprocessor;
import fr.insee.eno.postprocessing.pdf.PDFMailingPostprocessor;
import fr.insee.eno.postprocessing.pdf.PDFSpecificTreatmentPostprocessor;
import fr.insee.eno.postprocessing.pdf.PDFTableColumnPostprocessorFake;
import fr.insee.eno.preprocessing.DDICleaningPreprocessor;
import fr.insee.eno.preprocessing.DDIDereferencingPreprocessor;
import fr.insee.eno.preprocessing.DDITitlingPreprocessor;
import fr.insee.eno.preprocessing.Preprocessor;

public class DummyTestDDI2FO {
	
	private DDI2PDFGenerator ddi2pdf = new DDI2PDFGenerator();
	
	@Test
	public void mainTest() {		
		String basePathDDI2FO = "src/test/resources/ddi-to-fo";
		
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
		
		GenerationService genServiceDDI2PDF = new GenerationService(preprocessors, ddi2pdf, postprocessors);
		File in = new File(String.format("%s/in.xml", basePathDDI2FO));
		try {
			File output = genServiceDDI2PDF.generateQuestionnaire(in, "test");
			System.out.println(output.getAbsolutePath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
	}

}

