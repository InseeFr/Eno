package fr.insee.eno.test;

import fr.insee.eno.generation.PoguesXML2DDIGenerator;
import fr.insee.eno.postprocessing.NoopPostprocessor;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.preprocessing.PoguesXMLPreprocessorGoToTreatment;
import fr.insee.eno.preprocessing.PoguesXmlInsertFilterLoopIntoQuestionTree;
import fr.insee.eno.preprocessing.Preprocessor;
import fr.insee.eno.service.GenerationService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xmlunit.diff.Diff;

import java.io.*;

import static fr.insee.eno.Constants.createTempEnoFile;

public class TestPoguesXMLToDDI {

	
	private PoguesXML2DDIGenerator poguesXML2DDI = new PoguesXML2DDIGenerator();

	private XMLDiff xmlDiff = new XMLDiff();

	@Test
	public void simpleDiffTest() {
		String basePath = "src/test/resources/pogues-xml-to-ddi";
		testTransformationInOut(String.format("%s/in.xml", basePath), String.format("%s/out.xml", basePath));
	}

	@Test
	public void arbitrarySuggesterResponseTest() {
		String basePath = "src/test/resources/pogues-xml-to-ddi/suggester-arbitrary";
		testTransformationInOut(String.format("%s/in.xml", basePath), String.format("%s/out.xml", basePath));
	}

	@Test
	public void arbitrarySuggesterResponseInLoopTest() {
		String basePath = "src/test/resources/pogues-xml-to-ddi/suggester-arbitrary";
		testTransformationInOut(String.format("%s/in-loop.xml", basePath), String.format("%s/out-loop.xml", basePath));
	}

	private void testTransformationInOut(String inPath, String outPath){
		try {
			Preprocessor[] preprocessors = {
					new PoguesXmlInsertFilterLoopIntoQuestionTree(),
					new PoguesXMLPreprocessorGoToTreatment()};
			Postprocessor[] postprocessors = {new NoopPostprocessor()};

			GenerationService genService = new GenerationService(preprocessors, poguesXML2DDI, postprocessors);

			File in = new File(inPath);
			ByteArrayInputStream inputStream = new ByteArrayInputStream(FileUtils.readFileToByteArray(in));
			File outputFile = createTempEnoFile();
			ByteArrayOutputStream output = genService.generateQuestionnaire(inputStream, "xml-pogues-2-ddi-test");
			try (FileOutputStream fos = new FileOutputStream(outputFile)) {
				fos.write(output.toByteArray());
			}
			output.close();
			File expectedFile = new File(outPath);
			Diff diff = xmlDiff.getDiff(outputFile, expectedFile);
			Assertions.assertFalse(diff::hasDifferences, ()->getDiffMessage(diff));

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

	private String getDiffMessage(Diff diff) {
		return String.format("Transformed output should match expected XML document:\n %s",
				diff.toString());
	}
}
