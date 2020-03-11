package fr.insee.eno.main;

import java.io.File;

import org.junit.Test;

import fr.insee.eno.generation.DDI2LunaticXMLGenerator;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.postprocessing.lunaticxml.LunaticXMLExternalizeVariablesPostprocessor;
import fr.insee.eno.postprocessing.lunaticxml.LunaticXMLInsertGenericQuestionsPostprocessor;
import fr.insee.eno.postprocessing.lunaticxml.LunaticXMLSortComponentsPostprocessor;
import fr.insee.eno.postprocessing.lunaticxml.LunaticXMLVTLParserPostprocessor;
import fr.insee.eno.service.GenerationService;
import fr.insee.eno.preprocessing.DDICleaningPreprocessor;
import fr.insee.eno.preprocessing.DDIDereferencingPreprocessor;
import fr.insee.eno.preprocessing.DDITitlingPreprocessor;
import fr.insee.eno.preprocessing.Preprocessor;

public class DummyTestDDI2JS {
	
	private DDI2LunaticXMLGenerator ddi2jsGenerator = new DDI2LunaticXMLGenerator();	
	
	@Test
	public void mainTest() {
		
		String basePathDDI2JS = "src/test/resources/ddi-to-js";
		
		Preprocessor[] preprocessors = {
				new DDIDereferencingPreprocessor(),
				new DDICleaningPreprocessor(),
				new DDITitlingPreprocessor()};
		
		Postprocessor[] postprocessors =  {
				new LunaticXMLSortComponentsPostprocessor(),
				new LunaticXMLInsertGenericQuestionsPostprocessor(),
				new LunaticXMLExternalizeVariablesPostprocessor(),
				new LunaticXMLVTLParserPostprocessor()};
		
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
