package fr.insee.eno.params.generation;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import fr.insee.eno.service.MultiModelService;

public class TestMultiModelGenerationService {
	

	private MultiModelService multiModelService = new MultiModelService();
	
	
	
	@Test
	public void defaultTest() {
		String basePathDDI = "src/test/resources/params/in-to-out/multimodel";
		File input = new File(String.format("%s/ddi.xml", basePathDDI));
		File params = new File(String.format("%s/params-fr.xml", basePathDDI));
		
		try {
			File outputFile = multiModelService.generateQuestionnaire(input, params, null, null, null);
			System.out.println("File generated to :"+outputFile.getAbsolutePath());
		} catch (Exception e) {
			Assert.fail();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
