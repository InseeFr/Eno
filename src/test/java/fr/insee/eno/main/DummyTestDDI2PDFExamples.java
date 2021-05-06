package fr.insee.eno.main;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FilenameUtils;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.junit.jupiter.api.Test;

import fr.insee.eno.generation.DDI2FOGenerator;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.postprocessing.fo.FOEditStructurePagesPostprocessor;
import fr.insee.eno.postprocessing.fo.FOInsertAccompanyingMailsPostprocessor;
import fr.insee.eno.postprocessing.fo.FOInsertCoverPagePostprocessor;
import fr.insee.eno.postprocessing.fo.FOInsertEndQuestionPostprocessor;
import fr.insee.eno.postprocessing.fo.FOMailingPostprocessor;
import fr.insee.eno.postprocessing.fo.FOSpecificTreatmentPostprocessor;
import fr.insee.eno.postprocessing.fo.FOTableColumnPostprocessorFake;
import fr.insee.eno.service.GenerationService;
import fr.insee.eno.preprocessing.DDICleaningPreprocessor;
import fr.insee.eno.preprocessing.DDIDereferencingPreprocessor;
import fr.insee.eno.preprocessing.DDIMarkdown2XhtmlPreprocessor;
import fr.insee.eno.preprocessing.DDITitlingPreprocessor;
import fr.insee.eno.preprocessing.Preprocessor;

public class DummyTestDDI2PDFExamples {

	private DDI2FOGenerator generator =  new DDI2FOGenerator();
	
	@Test
	public void mainTest() {

		String basePathExamples = "src/test/resources/examples";
		String basePathImg = "src/test/resources/examples/img/";

		
		File in = new File(String.format("%s/esa-ddi-v2.xml", basePathExamples));
		File xconf = new File(String.format("%s/fop.xconf", basePathExamples));
		// File paramFile = new File(String.format("%s/ddi2pdf.xml", basePathExamples));
		File paramFile = new File(String.format("%s/parameters.xml", basePathExamples));
		InputStream paramIS = null;

		try {
			paramIS = new FileInputStream(paramFile);
			InputStream isXconf = new FileInputStream(xconf);
			URI imgFolderUri = new File(basePathImg).toURI();
			
			Preprocessor[] preprocessors = {
					new DDIMarkdown2XhtmlPreprocessor(),
					new DDIDereferencingPreprocessor(),
					new DDICleaningPreprocessor(),
					new DDITitlingPreprocessor()};
			
			Postprocessor[] postprocessors = { 
					new FOMailingPostprocessor(),
					new FOTableColumnPostprocessorFake(),
					new FOInsertEndQuestionPostprocessor(),
					new FOEditStructurePagesPostprocessor(),
					new FOSpecificTreatmentPostprocessor(),
					new FOInsertCoverPagePostprocessor(),
					new FOInsertAccompanyingMailsPostprocessor()};

			GenerationService genServiceDDI2PDF = new GenerationService(preprocessors, generator, postprocessors);
			genServiceDDI2PDF.setParameters(paramIS);

			File outputFO = genServiceDDI2PDF.generateQuestionnaire(in, "examples");

			// Step 1: Construct a FopFactory by specifying a reference to the
			// configuration file
			// (reuse if you plan to render multiple documents!)
			FopFactory fopFactory = FopFactory.newInstance(imgFolderUri, isXconf);

			File outFilePDF = new File(
					String.format("%s.pdf", FilenameUtils.removeExtension(outputFO.getAbsolutePath())));

			// Step 2: Set up output stream.
			// Note: Using BufferedOutputStream for performance reasons
			// (helpful with FileOutputStreams).
			OutputStream out = new BufferedOutputStream(new FileOutputStream(outFilePDF));

			// Step 3: Construct fop with desired output format
			Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

			// Step 4: Setup JAXP using identity transformer
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer(); // identity
																// transformer

			// Step 5: Setup input and output for XSLT transformation
			// Setup input stream
			Source src = new StreamSource(outputFO);
			// Resulting SAX events (the generated FO) must be piped through
			// to FOP
			Result res = new SAXResult(fop.getDefaultHandler());

			// Step 6: Start XSLT transformation and FOP processing
			transformer.transform(src, res);

			// Clean-up
			out.close();
			paramIS.close();
			isXconf.close();
			System.out.println(outputFO.getAbsolutePath());
			System.out.println(outFilePDF.getAbsolutePath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
