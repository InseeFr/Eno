package fr.insee.eno.main;

import fr.insee.eno.generation.PoguesXML2DDIGenerator;
import fr.insee.eno.postprocessing.NoopPostprocessor;
import fr.insee.eno.preprocessing.PoguesXMLPreprocessorGoToTreatment;
import fr.insee.eno.service.GenerationService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.*;

public class DummyTestPoguesXML2DDI {
	
	private PoguesXMLPreprocessorGoToTreatment poguesXMLPreprocessorGoToTreatment = new PoguesXMLPreprocessorGoToTreatment();

	private PoguesXML2DDIGenerator poguesXML2DDIGenerator = new PoguesXML2DDIGenerator();
	
	private NoopPostprocessor noopPostprocessor = new NoopPostprocessor();
	
	@Test
	public void mainTest() throws IOException {

		String basePath = "src/test/resources/pogues-xml-to-ddi";
		GenerationService genService = new GenerationService(poguesXMLPreprocessorGoToTreatment, poguesXML2DDIGenerator,
				noopPostprocessor);
		File in = new File(String.format("%s/in.xml", basePath));
		ByteArrayInputStream inputStream = new ByteArrayInputStream(FileUtils.readFileToByteArray(in));
		try {
			ByteArrayOutputStream output = genService.generateQuestionnaire(inputStream, "test");
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
