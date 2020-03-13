package fr.insee.eno.main;

import java.io.File;

import org.junit.Test;

import fr.insee.eno.generation.DDI2ODTGenerator;
import fr.insee.eno.postprocessing.NoopPostprocessor;
import fr.insee.eno.service.GenerationService;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.preprocessing.DDICleaningPreprocessor;
import fr.insee.eno.preprocessing.DDIDereferencingPreprocessor;
import fr.insee.eno.preprocessing.DDITitlingPreprocessor;
import fr.insee.eno.preprocessing.Preprocessor;

public class DummyTestDDI2ODT {
	
	private DDI2ODTGenerator ddi2odtGenerator = new DDI2ODTGenerator();

	@Test
	public void main() {
			
		String basePathDDI2ODT = "src/test/resources/ddi-to-odt";
		
		Preprocessor[] preprocessors = {
				new DDIDereferencingPreprocessor(),
				new DDICleaningPreprocessor(),
				new DDITitlingPreprocessor()};	
		
		Postprocessor[] postprocessors = {new NoopPostprocessor()};
		
		GenerationService genServiceDDI2ODT = new GenerationService(preprocessors, ddi2odtGenerator, postprocessors);
		File in = new File(String.format("%s/in.xml", basePathDDI2ODT));
		
		try {
			File output = genServiceDDI2ODT.generateQuestionnaire(in,"test");
			System.out.println(output.getAbsolutePath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
	}

}
