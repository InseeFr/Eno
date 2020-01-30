package fr.insee.eno.main;

import java.io.File;

import org.junit.Test;

import fr.insee.eno.generation.DDI2ODTGenerator;
import fr.insee.eno.postprocessing.NoopPostprocessor;
import fr.insee.eno.preprocessing.DDIPreprocessor;
import fr.insee.eno.service.GenerationService;

public class DummyTestDDI2ODT {
	
	private DDIPreprocessor ddiPreprocessor = new DDIPreprocessor();
	
	private DDI2ODTGenerator ddi2odtGenerator = new DDI2ODTGenerator();
	
	private NoopPostprocessor noopPostprocessor = new NoopPostprocessor();	

	@Test
	public void main() {
			
		String basePathDDI2ODT = "src/test/resources/ddi-to-odt";
		GenerationService genServiceDDI2ODT = new GenerationService(ddiPreprocessor, ddi2odtGenerator, noopPostprocessor);
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
