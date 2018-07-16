package fr.insee.eno.main;

import java.io.File;

import fr.insee.eno.GenerationService;
import fr.insee.eno.generation.DDI2ODTGenerator;
import fr.insee.eno.postprocessing.NoopPostprocessor;
import fr.insee.eno.preprocessing.DDIPreprocessor;

public class DummyTestDDI2ODT {

	public static void main(String[] args) {
		
		String basePathDDI2ODT = "src/test/resources/ddi-to-odt";
		GenerationService genServiceDDI2ODT = new GenerationService(new DDIPreprocessor(), new DDI2ODTGenerator(),
				new NoopPostprocessor());
		File in = new File(String.format("%s/in.xml", basePathDDI2ODT));
		
		try {
			File output = genServiceDDI2ODT.generateQuestionnaire(in, null);
			System.out.println(output.getAbsolutePath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
	}

}
