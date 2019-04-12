package fr.insee.eno.main;

import java.io.File;

import fr.insee.eno.GenerationService;
import fr.insee.eno.generation.DDI2PDFGenerator;
import fr.insee.eno.postprocessing.PDFStep1MailingPostprocessor;
import fr.insee.eno.postprocessing.PDFStep5SpecificTreatmentPostprocessor;
import fr.insee.eno.postprocessing.PDFStep2TableColumnPostprocessorFake;
import fr.insee.eno.postprocessing.PDFStep3InsertEndQuestionPostprocessor;
import fr.insee.eno.postprocessing.PDFStep4EditStructurePagesPostprocessor;
import fr.insee.eno.postprocessing.PDFStep6InsertCoverPagePostprocessor;
import fr.insee.eno.postprocessing.PDFStep7InsertAccompanyingMailsPostprocessor;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.preprocessing.DDIPreprocessor;

public class DummyTestDDI2FO {

	public static void main(String[] args) {
		
		String basePathDDI2FO = "src/test/resources/ddi-to-fo";
		GenerationService genServiceDDI2PDF = new GenerationService(new DDIPreprocessor(), new DDI2PDFGenerator(),
				new Postprocessor[] {/*new NoopPostprocessor()*/
						new PDFStep1MailingPostprocessor(),
						new PDFStep2TableColumnPostprocessorFake(),
						new PDFStep3InsertEndQuestionPostprocessor(),
						new PDFStep4EditStructurePagesPostprocessor(),
						new PDFStep5SpecificTreatmentPostprocessor(),
						new PDFStep6InsertCoverPagePostprocessor(),
						new PDFStep7InsertAccompanyingMailsPostprocessor()});
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

