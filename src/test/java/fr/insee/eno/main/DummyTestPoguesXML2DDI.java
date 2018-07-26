package fr.insee.eno.main;

import java.io.File;

import fr.insee.eno.GenerationService;
import fr.insee.eno.generation.PoguesXML2DDIGenerator;
import fr.insee.eno.postprocessing.DDIPostprocessor;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.preprocessing.PoguesXMLPreprocessor;

public class DummyTestPoguesXML2DDI {

	public static void main(String[] args) {

		String basePath = "src/test/resources/pogues-xml-to-ddi";
		GenerationService genService = new GenerationService(new PoguesXMLPreprocessor(), new PoguesXML2DDIGenerator(),
				new Postprocessor[] {new DDIPostprocessor()});
		File in = new File(String.format("%s/in.xml", basePath));
		try {
			File output = genService.generateQuestionnaire(in, null, "test");
			System.out.println(output.getAbsolutePath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
