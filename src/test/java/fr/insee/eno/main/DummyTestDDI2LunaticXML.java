package fr.insee.eno.main;

import fr.insee.eno.generation.DDI2LunaticXMLGenerator;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.postprocessing.lunaticxml.*;
import fr.insee.eno.preprocessing.DDICleaningPreprocessor;
import fr.insee.eno.preprocessing.DDIDereferencingPreprocessor;
import fr.insee.eno.preprocessing.DDIMarkdown2XhtmlPreprocessor;
import fr.insee.eno.preprocessing.DDIMultimodalSelectionPreprocessor;
import fr.insee.eno.preprocessing.DDITitlingPreprocessor;
import fr.insee.eno.preprocessing.Preprocessor;
import fr.insee.eno.service.GenerationService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.*;

public class DummyTestDDI2LunaticXML {
	
	private DDI2LunaticXMLGenerator ddi2lunaticXmlGenerator = new DDI2LunaticXMLGenerator();	
	
	@Test
	public void mainTest() throws IOException {
		
		String basePathDDI2JS = "src/test/resources/ddi-to-lunatic-xml";
		
		Preprocessor[] preprocessors = {

				new DDIMultimodalSelectionPreprocessor(),
				new DDIMarkdown2XhtmlPreprocessor(),
				new DDIDereferencingPreprocessor(),
				new DDICleaningPreprocessor(),
				new DDITitlingPreprocessor()};
		
		Postprocessor[] postprocessors =  {
				new LunaticXMLSortComponentsPostprocessor(),
				new LunaticXMLInsertGenericQuestionsPostprocessor(),
				new LunaticXMLExternalizeVariablesAndDependenciesPostprocessor(),
				new LunaticXMLInsertCleaningBlockPostprocessor(),
				new LunaticXMLVTLParserPostprocessor(),
				new LunaticXMLVTLParserPostprocessor(),
				new LunaticXMLPaginationPostprocessor(),};
		
		GenerationService genServiceDDI2JS = new GenerationService(preprocessors, ddi2lunaticXmlGenerator,postprocessors);
		File in = new File(String.format("%s/in.xml", basePathDDI2JS));
		ByteArrayInputStream inputStream = new ByteArrayInputStream(FileUtils.readFileToByteArray(in));
		try {
			ByteArrayOutputStream output = genServiceDDI2JS.generateQuestionnaire(inputStream, "test");
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
