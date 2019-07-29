package fr.insee.eno.main;

import java.io.File;

import fr.insee.eno.GenerationService;
import fr.insee.eno.generation.DDI2JSGenerator;
import fr.insee.eno.postprocessing.JSExternalizeVariablesPostprocessor;
import fr.insee.eno.postprocessing.JSSortComponentsPostprocessor;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.preprocessing.DDIPreprocessor;

public class DummyTestDDI2JS {

	public static void main(String[] args) {
		
		String basePathDDI2JS = "src/test/resources/ddi-to-js";
		Postprocessor[] postprocessors =  {
				new JSSortComponentsPostprocessor(),
				new JSExternalizeVariablesPostprocessor()};
		
		GenerationService genServiceDDI2JS = new GenerationService(new DDIPreprocessor(), new DDI2JSGenerator(),postprocessors);
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
