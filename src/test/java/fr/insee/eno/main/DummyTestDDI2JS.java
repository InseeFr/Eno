package fr.insee.eno.main;

import java.io.File;

import org.junit.Test;

import fr.insee.eno.GenerationService;
import fr.insee.eno.generation.DDI2JSGenerator;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.postprocessing.js.JSExternalizeVariablesPostprocessor;
import fr.insee.eno.postprocessing.js.JSInsertGenericQuestionsPostprocessor;
import fr.insee.eno.postprocessing.js.JSSortComponentsPostprocessor;
import fr.insee.eno.postprocessing.js.JSVTLParserPostprocessor;
import fr.insee.eno.preprocessing.DDICleaningPreprocessor;
import fr.insee.eno.preprocessing.DDIDereferencingPreprocessor;
import fr.insee.eno.preprocessing.DDITitlingPreprocessor;
import fr.insee.eno.preprocessing.Preprocessor;

public class DummyTestDDI2JS {
	
	private DDI2JSGenerator ddi2jsGenerator = new DDI2JSGenerator();	
	
	@Test
	public void mainTest() {
		
		String basePathDDI2JS = "src/test/resources/ddi-to-js";
		
		Preprocessor[] preprocessors = {
				new DDIDereferencingPreprocessor(),
				new DDICleaningPreprocessor(),
				new DDITitlingPreprocessor()};
		
		Postprocessor[] postprocessors =  {
				new JSSortComponentsPostprocessor(),
				new JSInsertGenericQuestionsPostprocessor(),
				new JSExternalizeVariablesPostprocessor(),
				new JSVTLParserPostprocessor()};
		
		GenerationService genServiceDDI2JS = new GenerationService(preprocessors, ddi2jsGenerator,postprocessors);
		File in = new File(String.format("%s/in.xml", basePathDDI2JS));
		
		try {
			File output = genServiceDDI2JS.generateQuestionnaire(in,"test");
			System.out.println(output.getAbsolutePath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
	}

}
