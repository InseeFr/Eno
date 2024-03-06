package fr.insee.eno.main;

import fr.insee.eno.generation.DDI2FODTGenerator;
import fr.insee.eno.postprocessing.NoopPostprocessor;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.preprocessing.*;
import fr.insee.eno.service.GenerationService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.*;

public class DummyTestDDI2FODT {
	
	private DDI2FODTGenerator ddi2fodtGenerator = new DDI2FODTGenerator();

	@Test
	public void main() throws IOException {
			
		String basePathDDI2ODT = "src/test/resources/ddi-to-fodt";
		
		Preprocessor[] preprocessors = {

				new DDIMultimodalSelectionPreprocessor(),
				new DDIMarkdown2XhtmlPreprocessor(),
				new DDIDereferencingPreprocessor(),
				new DDICleaningPreprocessor(),
				new DDITitlingPreprocessor()};	
		
		Postprocessor[] postprocessors = {new NoopPostprocessor()};
		
		GenerationService genServiceDDI2ODT = new GenerationService(preprocessors, ddi2fodtGenerator, postprocessors);
		File in = new File(String.format("%s/in.xml", basePathDDI2ODT));
		ByteArrayInputStream inputStream = new ByteArrayInputStream(FileUtils.readFileToByteArray(in));
		try {
			ByteArrayOutputStream output = genServiceDDI2ODT.generateQuestionnaire(inputStream, "test");
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
