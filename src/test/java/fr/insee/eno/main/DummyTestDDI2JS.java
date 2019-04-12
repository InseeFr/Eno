package fr.insee.eno.main;

import java.io.File;

import fr.insee.eno.GenerationService;
import fr.insee.eno.generation.DDI2JSGenerator;
import fr.insee.eno.postprocessing.JSAddVariableReferencePostprocessor;
import fr.insee.eno.postprocessing.NoopPostprocessor;
import fr.insee.eno.preprocessing.DDIPreprocessor;

public class DummyTestDDI2JS {

	public static void main(String[] args) {
		
		String basePathDDI2ODT = "src/test/resources/ddi-to-js";
		GenerationService genServiceDDI2JS = new GenerationService(new DDIPreprocessor(), new DDI2JSGenerator(),
				new JSAddVariableReferencePostprocessor());
		File in = new File(String.format("%s/in.xml", basePathDDI2ODT));
		
		try {
			File output = genServiceDDI2JS.generateQuestionnaire(in,"test");
			System.out.println(output.getAbsolutePath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
	}

}
