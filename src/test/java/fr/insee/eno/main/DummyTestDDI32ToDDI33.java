package fr.insee.eno.main;

import java.io.File;

import fr.insee.eno.generation.IdentityGenerator;
import fr.insee.eno.postprocessing.NoopPostprocessor;
import fr.insee.eno.preprocessing.DDI32ToDDI33Preprocessor;
import fr.insee.eno.service.GenerationService;

public class DummyTestDDI32ToDDI33 {

	public static void main(String[] args) {
		
		String basePathDDI32DDI33FO = "src/test/resources/ddi32-to-ddi33";
		GenerationService genServiceDDI32DDI33 = new GenerationService(new DDI32ToDDI33Preprocessor(), new IdentityGenerator(),new NoopPostprocessor());
		File in = new File(String.format("%s/in.xml", basePathDDI32DDI33FO));
		try {
			File output = genServiceDDI32DDI33.generateQuestionnaire(in, "test");
			System.out.println(output.getAbsolutePath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
	}

}

