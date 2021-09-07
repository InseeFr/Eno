package fr.insee.eno.main;

import java.io.File;

import org.junit.jupiter.api.Test;

import fr.insee.eno.generation.PoguesXML2DDIGenerator;
import fr.insee.eno.postprocessing.NoopPostprocessor;
import fr.insee.eno.preprocessing.PoguesXMLPreprocessorGoToTreatment;
import fr.insee.eno.service.GenerationService;

public class DummyTestPoguesXML2DDI {
	
	private PoguesXMLPreprocessorGoToTreatment poguesXMLPreprocessorGoToTreatment = new PoguesXMLPreprocessorGoToTreatment();

	private PoguesXML2DDIGenerator poguesXML2DDIGenerator = new PoguesXML2DDIGenerator();
	
	private NoopPostprocessor noopPostprocessor = new NoopPostprocessor();
	
	@Test
	public void mainTest() {

		String basePath = "src/test/resources/pogues-xml-to-ddi";
		GenerationService genService = new GenerationService(poguesXMLPreprocessorGoToTreatment, poguesXML2DDIGenerator,
				noopPostprocessor);
		File in = new File(String.format("%s/in.xml", basePath));
		try {
			File output = genService.generateQuestionnaire(in, "test");
			System.out.println(output.getAbsolutePath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
