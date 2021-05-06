package fr.insee.eno.main;

import fr.insee.eno.generation.DDI2LunaticXMLGenerator;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.postprocessing.lunaticxml.*;
import fr.insee.eno.preprocessing.DDICleaningPreprocessor;
import fr.insee.eno.preprocessing.DDIDereferencingPreprocessor;
import fr.insee.eno.preprocessing.DDIMarkdown2XhtmlPreprocessor;
import fr.insee.eno.preprocessing.DDITitlingPreprocessor;
import fr.insee.eno.preprocessing.Preprocessor;
import fr.insee.eno.service.GenerationService;
import org.junit.jupiter.api.Test;

import java.io.File;

public class DummyTestDDI2LunaticXML {
	
	private DDI2LunaticXMLGenerator ddi2lunaticXmlGenerator = new DDI2LunaticXMLGenerator();	
	
	@Test
	public void mainTest() {
		
		String basePathDDI2JS = "src/test/resources/ddi-to-lunatic-xml";
		
		Preprocessor[] preprocessors = {
				new DDIMarkdown2XhtmlPreprocessor(),
				new DDIDereferencingPreprocessor(),
				new DDICleaningPreprocessor(),
				new DDITitlingPreprocessor()};
		
		Postprocessor[] postprocessors =  {
				new LunaticXMLSortComponentsPostprocessor(),
				new LunaticXMLInsertGenericQuestionsPostprocessor(),
				new LunaticXMLExternalizeVariablesAndDependenciesPostprocessor(),
				new LunaticXMLVTLParserPostprocessor(),
				new LunaticXMLVTLParserPostprocessor(),
				new LunaticXMLPaginationPostprocessor(),};
		
		GenerationService genServiceDDI2JS = new GenerationService(preprocessors, ddi2lunaticXmlGenerator,postprocessors);
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
