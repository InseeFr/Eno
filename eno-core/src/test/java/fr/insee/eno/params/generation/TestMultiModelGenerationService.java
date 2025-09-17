package fr.insee.eno.params.generation;

import fr.insee.eno.service.MultiModelService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import static fr.insee.eno.Constants.createTempEnoFile;

public class TestMultiModelGenerationService {
	

	private MultiModelService multiModelService = new MultiModelService();
	
	
	
	@Test
	public void defaultTest() {
		String basePathDDI = "src/test/resources/params/in-to-out/multimodel";
		File input = new File(String.format("%s/ddi.xml", basePathDDI));
		File params = new File(String.format("%s/params-xforms.xml", basePathDDI));

		Assertions.assertAll(()->{
			File outputFile = createTempEnoFile(".zip");
			ByteArrayOutputStream output = multiModelService.generateQuestionnaire(
					new ByteArrayInputStream(FileUtils.readFileToByteArray(input)),
					new ByteArrayInputStream(FileUtils.readFileToByteArray(params)), null, null, null);
			try (FileOutputStream fos = new FileOutputStream(outputFile)) {
				fos.write(output.toByteArray());
			}
			output.close();
			System.out.println("File generated to :"+outputFile.getAbsolutePath());
		});

	}
	
}
