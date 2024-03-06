package fr.insee.eno.test;

import java.io.*;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xmlunit.diff.Diff;

import fr.insee.eno.generation.PoguesXML2DDIGenerator;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.postprocessing.NoopPostprocessor;
import fr.insee.eno.preprocessing.PoguesXMLPreprocessorGoToTreatment;
import fr.insee.eno.preprocessing.PoguesXmlInsertFilterLoopIntoQuestionTree;
import fr.insee.eno.preprocessing.Preprocessor;
import fr.insee.eno.service.GenerationService;

import static fr.insee.eno.Constants.createTempEnoFile;

public class TestPoguesXMLToDDI {

	
	private PoguesXML2DDIGenerator poguesXML2DDI = new PoguesXML2DDIGenerator();

	private XMLDiff xmlDiff = new XMLDiff();

	@Test
	public void simpleDiffTest() {
		try {
			String basePath = "src/test/resources/pogues-xml-to-ddi";
			
			Preprocessor[] preprocessors = {
					new PoguesXmlInsertFilterLoopIntoQuestionTree(),
					new PoguesXMLPreprocessorGoToTreatment()};
			Postprocessor[] postprocessors = {new NoopPostprocessor()};
			
			GenerationService genService = new GenerationService(preprocessors, poguesXML2DDI, postprocessors);

			File in = new File(String.format("%s/in.xml", basePath));
			ByteArrayInputStream inputStream = new ByteArrayInputStream(FileUtils.readFileToByteArray(in));
			File outputFile = createTempEnoFile();
			ByteArrayOutputStream output = genService.generateQuestionnaire(inputStream, "xml-pogues-2-ddi-test");
			try (FileOutputStream fos = new FileOutputStream(outputFile)) {
				fos.write(output.toByteArray());
			}
			output.close();
			File expectedFile = new File(String.format("%s/out.xml", basePath));
			Diff diff = xmlDiff.getDiff(outputFile, expectedFile);
			Assertions.assertFalse(diff::hasDifferences, ()->getDiffMessage(diff, basePath));

		} catch (IOException e) {
			e.printStackTrace();
			Assertions.fail();
		} catch (NullPointerException e) {
			e.printStackTrace();
			Assertions.fail();
		} catch (Exception e) {
			e.printStackTrace();
			Assertions.fail();
		}
	}

	private String getDiffMessage(Diff diff, String path) {
		return String.format("Transformed output for %s should match expected XML document:\n %s", path,
				diff.toString());
	}
}
