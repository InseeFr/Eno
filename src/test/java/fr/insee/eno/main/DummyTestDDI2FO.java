package fr.insee.eno.main;

import java.io.File;

import org.junit.jupiter.api.Test;

import fr.insee.eno.generation.DDI2FOGenerator;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.postprocessing.fo.FOEditStructurePagesPostprocessor;
import fr.insee.eno.postprocessing.fo.FOInsertAccompanyingMailsPostprocessor;
import fr.insee.eno.postprocessing.fo.FOInsertCoverPagePostprocessor;
import fr.insee.eno.postprocessing.fo.FOInsertEndQuestionPostprocessor;
import fr.insee.eno.postprocessing.fo.FOMailingPostprocessor;
import fr.insee.eno.postprocessing.fo.FOSpecificTreatmentPostprocessor;
import fr.insee.eno.postprocessing.fo.FOTableColumnPostprocessorFake;
import fr.insee.eno.service.GenerationService;
import fr.insee.eno.preprocessing.DDICleaningPreprocessor;
import fr.insee.eno.preprocessing.DDIDereferencingPreprocessor;
import fr.insee.eno.preprocessing.DDIMarkdown2XhtmlPreprocessor;
import fr.insee.eno.preprocessing.DDITitlingPreprocessor;
import fr.insee.eno.preprocessing.Preprocessor;

public class DummyTestDDI2FO {
	
	private DDI2FOGenerator ddi2fo = new DDI2FOGenerator();
	
	@Test
	public void mainTest() {		
		String basePathDDI2FO = "src/test/resources/ddi-to-fo";
		
		Preprocessor[] preprocessors = {
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
		
		GenerationService genServiceDDI2PDF = new GenerationService(preprocessors, ddi2fo, postprocessors);
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

