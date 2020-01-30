package fr.insee.eno.main;

import java.io.File;

import org.junit.Test;

import fr.insee.eno.generation.DDI2FRGenerator;
import fr.insee.eno.postprocessing.NoopPostprocessor;
import fr.insee.eno.preprocessing.DDIPreprocessor;
import fr.insee.eno.service.GenerationService;

public class DummyTestDDI2XForms {

	private DDIPreprocessor ddiPreprocessor = new DDIPreprocessor();
	
	private DDI2FRGenerator ddi2frGenerator = new DDI2FRGenerator();
	
	private NoopPostprocessor noopPostprocessor = new NoopPostprocessor();
	
	@Test
	public void mainTest() {

		String basePathDDI2XFORMS = "src/test/resources/ddi-to-xform";
		GenerationService genServiceDDI2XFORMS = new GenerationService(ddiPreprocessor, ddi2frGenerator, noopPostprocessor);
		File in = new File(String.format("%s/in.xml", basePathDDI2XFORMS));

		try {
			File output = genServiceDDI2XFORMS.generateQuestionnaire(in, "test");
			System.out.println(output.getAbsolutePath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
