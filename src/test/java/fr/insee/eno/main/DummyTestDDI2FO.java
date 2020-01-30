package fr.insee.eno.main;

import java.io.File;

import org.junit.Test;

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

public class DummyTestDDI2FO {
		
	private DDIPreprocessor ddiPreprocessor = new DDIPreprocessor();
	
	private DDI2PDFGenerator ddi2pdf = new DDI2PDFGenerator();
	
	@Test
	public void mainTest() {		
		String basePathDDI2FO = "src/test/resources/ddi-to-fo";
		GenerationService genServiceDDI2PDF = new GenerationService(ddiPreprocessor, ddi2pdf,
				new Postprocessor[] {/*new NoopPostprocessor()*/
						new PDFMailingPostprocessor(),
						new PDFTableColumnPostprocessorFake(),
						new PDFInsertEndQuestionPostprocessor(),
						new PDFEditStructurePagesPostprocessor(),
						new PDFSpecificTreatmentPostprocessor(),
						new PDFInsertCoverPagePostprocessor(),
						new PDFInsertAccompanyingMailsPostprocessor()});
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

