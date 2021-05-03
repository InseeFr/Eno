package fr.insee.eno.params.generation;

import java.io.File;
import fr.insee.eno.service.MultiModelService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestMultiModelGenerationService {
	

	private MultiModelService multiModelService = new MultiModelService();
	
	
	
	@Test
	public void defaultTest() {
		String basePathDDI = "src/test/resources/params/in-to-out/multimodel";
		File input = new File(String.format("%s/ddi.xml", basePathDDI));
		File params = new File(String.format("%s/params-xforms.xml", basePathDDI));

		Assertions.assertAll(()->{
			File outputFile = multiModelService.generateQuestionnaire(input, params, null, null, null);
			System.out.println("File generated to :"+outputFile.getAbsolutePath());
		});
		
	}
	
}
