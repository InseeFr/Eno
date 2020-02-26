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
import org.junit.Test;

import fr.insee.eno.GenerationService;
import fr.insee.eno.generation.DDI2PDFGenerator;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.postprocessing.pdf.PDFEditStructurePagesPostprocessor;
import fr.insee.eno.postprocessing.pdf.PDFInsertAccompanyingMailsPostprocessor;
import fr.insee.eno.postprocessing.pdf.PDFInsertCoverPagePostprocessor;
import fr.insee.eno.postprocessing.pdf.PDFInsertEndQuestionPostprocessor;
import fr.insee.eno.postprocessing.pdf.PDFMailingPostprocessor;
import fr.insee.eno.postprocessing.pdf.PDFSpecificTreatmentPostprocessor;
import fr.insee.eno.postprocessing.pdf.PDFTableColumnPostprocessorFake;
import fr.insee.eno.preprocessing.DDICleaningPreprocessor;
import fr.insee.eno.preprocessing.DDIDereferencingPreprocessor;
import fr.insee.eno.preprocessing.DDITitlingPreprocessor;
import fr.insee.eno.preprocessing.Preprocessor;

public class DummyTestDDI2PDF {
	
	private DDI2PDFGenerator ddi2pdfGenerator = new DDI2PDFGenerator();	

	@Test
	public void mainTest() {

		String basePathddi2PDF = "src/test/resources/ddi-to-pdf";
		String basePathImg = "src/test/resources/examples/img/";

		Preprocessor[] preprocessors = {
				new DDIDereferencingPreprocessor(),
				new DDICleaningPreprocessor(),
				new DDITitlingPreprocessor()};
		
		Postprocessor[] postprocessors = { 
				new PDFMailingPostprocessor(),
				new PDFTableColumnPostprocessorFake(),
				new PDFInsertEndQuestionPostprocessor(),
				new PDFEditStructurePagesPostprocessor(),
				new PDFSpecificTreatmentPostprocessor(),
				new PDFInsertCoverPagePostprocessor(),
				new PDFInsertAccompanyingMailsPostprocessor()};
		
		GenerationService genServiceDDI2PDF = new GenerationService(preprocessors, ddi2pdfGenerator, postprocessors);
		File in = new File(String.format("%s/in.xml", basePathddi2PDF));
		File xconf = new File(String.format("%s/fop.xconf", basePathddi2PDF));

		try {
			InputStream isXconf = new FileInputStream(xconf);
			URI imgFolderUri = new File(basePathImg).toURI();

			File outputFO = genServiceDDI2PDF.generateQuestionnaire(in, "test");

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
			System.out.println(outFilePDF.getAbsolutePath());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
