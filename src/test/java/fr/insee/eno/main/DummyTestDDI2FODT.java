package fr.insee.eno.main;

import java.io.File;

import org.junit.jupiter.api.Test;

import fr.insee.eno.generation.DDI2FODTGenerator;
import fr.insee.eno.postprocessing.NoopPostprocessor;
import fr.insee.eno.service.GenerationService;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.preprocessing.DDICleaningPreprocessor;
import fr.insee.eno.preprocessing.DDIDereferencingPreprocessor;
<<<<<<< HEAD
import fr.insee.eno.preprocessing.DDIMarkdown2XhtmlPreprocessor;
=======
import fr.insee.eno.preprocessing.DDIMultimodalSelectionPreprocessor;
>>>>>>> dev-multi-decla_multimode
import fr.insee.eno.preprocessing.DDITitlingPreprocessor;
import fr.insee.eno.preprocessing.Preprocessor;

public class DummyTestDDI2FODT {
	
	private DDI2FODTGenerator ddi2fodtGenerator = new DDI2FODTGenerator();

	@Test
	public void main() {
			
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
		
		try {
			File output = genServiceDDI2ODT.generateQuestionnaire(in,"test");
			System.out.println(output.getAbsolutePath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
	}

}
