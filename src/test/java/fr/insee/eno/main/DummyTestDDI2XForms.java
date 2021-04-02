package fr.insee.eno.main;

import java.io.File;

import org.junit.jupiter.api.Test;

import fr.insee.eno.generation.DDI2XFORMSGenerator;
import fr.insee.eno.postprocessing.NoopPostprocessor;
import fr.insee.eno.service.GenerationService;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.preprocessing.DDICleaningPreprocessor;
import fr.insee.eno.preprocessing.DDIDereferencingPreprocessor;
import fr.insee.eno.preprocessing.DDITitlingPreprocessor;
import fr.insee.eno.preprocessing.Preprocessor;

public class DummyTestDDI2XForms {
	
	private DDI2XFORMSGenerator ddi2xformsGenerator = new DDI2XFORMSGenerator();
	
	@Test
	public void mainTest() {

		String basePathDDI2XFORMS = "src/test/resources/ddi-to-xforms";
		
		Preprocessor[] preprocessors = {
				new DDIDereferencingPreprocessor(),
				new DDICleaningPreprocessor(),
				new DDITitlingPreprocessor()};
		
		Postprocessor[] postprocessors = {new NoopPostprocessor()};
		
		GenerationService genServiceDDI2XFORMS = new GenerationService(preprocessors, ddi2xformsGenerator, postprocessors);
		File in = new File(String.format("%s/in.xml", basePathDDI2XFORMS));

		try {
			File output = genServiceDDI2XFORMS.generateQuestionnaire(in, "test");
			System.out.println(output.getAbsolutePath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
