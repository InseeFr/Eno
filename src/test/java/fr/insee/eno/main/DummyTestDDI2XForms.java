package fr.insee.eno.main;

import fr.insee.eno.generation.DDI2XFORMSGenerator;
import fr.insee.eno.postprocessing.NoopPostprocessor;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.preprocessing.*;
import fr.insee.eno.service.GenerationService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.*;

public class DummyTestDDI2XForms {
	
	private DDI2XFORMSGenerator ddi2xformsGenerator = new DDI2XFORMSGenerator();
	
	@Test
	public void mainTest() throws IOException {

		String basePathDDI2XFORMS = "src/test/resources/ddi-to-xforms";
		
		Preprocessor[] preprocessors = {

				new DDIMultimodalSelectionPreprocessor(),
				new DDIMarkdown2XhtmlPreprocessor(),
				new DDIDereferencingPreprocessor(),
				new DDICleaningPreprocessor(),
				new DDITitlingPreprocessor()};
		
		Postprocessor[] postprocessors = {new NoopPostprocessor()};
		
		GenerationService genServiceDDI2XFORMS = new GenerationService(preprocessors, ddi2xformsGenerator, postprocessors);
		File in = new File(String.format("%s/in.xml", basePathDDI2XFORMS));

		ByteArrayInputStream inputStream = new ByteArrayInputStream(FileUtils.readFileToByteArray(in));
		try {
			ByteArrayOutputStream output = genServiceDDI2XFORMS.generateQuestionnaire(inputStream, "test");
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
