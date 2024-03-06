package fr.insee.eno.main;

import fr.insee.eno.generation.DDI2FOGenerator;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.postprocessing.fo.*;
import fr.insee.eno.preprocessing.*;
import fr.insee.eno.service.GenerationService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.*;

public class DummyTestDDI2FO {
	
	private DDI2FOGenerator ddi2fo = new DDI2FOGenerator();
	
	@Test
	public void mainTest() throws IOException {
		String basePathDDI2FO = "src/test/resources/ddi-to-fo";
		
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
		
		GenerationService genServiceDDI2PDF = new GenerationService(preprocessors, ddi2fo, postprocessors);
		File in = new File(String.format("%s/in.xml", basePathDDI2FO));
		ByteArrayInputStream inputStream = new ByteArrayInputStream(FileUtils.readFileToByteArray(in));
		try {
			ByteArrayOutputStream output = genServiceDDI2PDF.generateQuestionnaire(inputStream, "test");
			File file = File.createTempFile("eno-",".xml");
			try (FileOutputStream fos = new FileOutputStream(file)) {
				fos.write(output.toByteArray());
			}
			output.close();
			System.out.println(file.getAbsolutePath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
	}

}

