package fr.insee.eno.main;

import java.io.*;

import fr.insee.eno.generation.IdentityGenerator;
import fr.insee.eno.postprocessing.NoopPostprocessor;
import fr.insee.eno.preprocessing.DDI32ToDDI33Preprocessor;
import fr.insee.eno.service.GenerationService;
import org.apache.commons.io.FileUtils;

public class DummyTestDDI32ToDDI33 {

	public static void main(String[] args) throws IOException {
		
		String basePathDDI32DDI33FO = "src/test/resources/ddi32-to-ddi33";
		GenerationService genServiceDDI32DDI33 = new GenerationService(new DDI32ToDDI33Preprocessor(), new IdentityGenerator(),new NoopPostprocessor());
		File in = new File(String.format("%s/in.xml", basePathDDI32DDI33FO));
		ByteArrayInputStream inputStream = new ByteArrayInputStream(FileUtils.readFileToByteArray(in));
		try {
			ByteArrayOutputStream output = genServiceDDI32DDI33.generateQuestionnaire(inputStream, "test");
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

