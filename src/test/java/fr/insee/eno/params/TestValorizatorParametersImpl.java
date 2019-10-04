package fr.insee.eno.params;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlunit.diff.Diff;

import fr.insee.eno.Constants;
import fr.insee.eno.parameters.AccompanyingMail;
import fr.insee.eno.parameters.BrowsingEnum;
import fr.insee.eno.parameters.ENOParameters;
import fr.insee.eno.parameters.Format;
import fr.insee.eno.parameters.FrParameters;
import fr.insee.eno.parameters.GlobalNumerotation;
import fr.insee.eno.parameters.InFormat;
import fr.insee.eno.parameters.Language;
import fr.insee.eno.parameters.LevelEnum;
import fr.insee.eno.parameters.LevelQuestion;
import fr.insee.eno.parameters.NumerotationQuestion;
import fr.insee.eno.parameters.OutFormat;
import fr.insee.eno.parameters.Parameters;
import fr.insee.eno.parameters.Parameters.Languages;
import fr.insee.eno.parameters.PdfParameters;
import fr.insee.eno.parameters.Pipeline;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.parameters.StudyUnit;
import fr.insee.eno.parameters.Table;
import fr.insee.eno.parameters.Table.Row;
import fr.insee.eno.test.XMLDiff;

public class TestValorizatorParametersImpl {

	private ValorizatorParametersImpl valorizatorParametersImpl;
	private ENOParameters enoParameters;
	
	private XMLDiff xmlDiff = new XMLDiff();
	
	@Before
	public void setValorizator() {
		valorizatorParametersImpl = new ValorizatorParametersImpl();
	}
	
	@Before
	public void setEnoParameters() throws JAXBException, IOException {
		enoParameters = new ENOParameters();
		
		Pipeline pipeline = new Pipeline();
		pipeline.setInFormat(InFormat.DDI);
		pipeline.setOutFormat(OutFormat.ODT);
		pipeline.getPostProcessing().add(PostProcessing.DDI_MARKDOWN_TO_XHTML);
		
		enoParameters.setPipeline(pipeline);
		Parameters parameters = new Parameters();
		parameters.setStudyUnit(StudyUnit.HOUSEHOLD);
		
		Languages languages = new Languages();
		languages.getLanguage().add(Language.DE);
		languages.getLanguage().add(Language.FR);
		languages.getLanguage().add(Language.EN);
		parameters.setLanguages(languages);
		
		
		PdfParameters pdfParameters = new PdfParameters();
		Table table = new Table();
		Row row = new Row();
		row.setDefaultSize(150);
		table.setRow(row);
		Format format = new Format();
		format.setOrientation(90);		
		pdfParameters.setTable(table);
		pdfParameters.setFormat(format);				
		
		pdfParameters.setAccompanyingMail(AccompanyingMail.CNR_COL);
		
		GlobalNumerotation globalNumerotation = new GlobalNumerotation();
		globalNumerotation.setBrowsing(BrowsingEnum.NO_NUMBER);
		
		NumerotationQuestion question = new NumerotationQuestion();
		LevelQuestion levelQuestion2 = new LevelQuestion();
		levelQuestion2.setPostNumQuest("%***%");
		levelQuestion2.setName(LevelEnum.SUBMODULE);
		levelQuestion2.setPostNumParentQuest("yolyo");
		LevelQuestion levelQuestion = new LevelQuestion();
		levelQuestion.setPostNumQuest("%***%");
		levelQuestion.setName(LevelEnum.MODULE);
		levelQuestion.setPostNumParentQuest("yolyo");
		question.getLevel().add(levelQuestion);
		//question.getLevel().add(levelQuestion2);
		
		globalNumerotation.setQuestion(question);
		parameters.setTitle(globalNumerotation);
		parameters.setPdfParameters(pdfParameters);
		
		FrParameters frParameters = new FrParameters();
		frParameters.setLengthOfLongTable(56);
		frParameters.setNumericExample(true);
		
		parameters.setFrParameters(frParameters);
		
		enoParameters.setParameters(parameters);
	}	
	
	@Test
	public void testValorizationJavaParameters() {
		try {			
			
			ENOParameters defaultParams = valorizatorParametersImpl.getDefaultParameters();
			long debut = System.currentTimeMillis();
			ENOParameters enoParametersFinal = valorizatorParametersImpl.mergeEnoParameters(
					defaultParams, 
					enoParameters);
			System.out.println("Merging time : "+(System.currentTimeMillis()-debut)+" ms");
			ENOParameters enoParametersDefault = valorizatorParametersImpl.getDefaultParameters();
			
			// Pipeline
			Assert.assertEquals(InFormat.DDI, enoParametersFinal.getPipeline().getInFormat());
			Assert.assertEquals(OutFormat.ODT, enoParametersFinal.getPipeline().getOutFormat());
			//Default value
			Assert.assertEquals(enoParametersDefault.getPipeline().getPreProcessing(), enoParametersFinal.getPipeline().getPreProcessing());
			// New value
			Assert.assertEquals(Arrays.asList(PostProcessing.DDI_MARKDOWN_TO_XHTML), enoParametersFinal.getPipeline().getPostProcessing());
			
			// Browsing
			Assert.assertEquals(BrowsingEnum.NO_NUMBER, enoParametersFinal.getParameters().getTitle().getBrowsing());
			
			//StudyUnit 
			Assert.assertEquals(StudyUnit.HOUSEHOLD, enoParametersFinal.getParameters().getStudyUnit());
			

			debut = System.currentTimeMillis();
			Path outPath = Paths.get(Constants.TEMP_FOLDER_PATH + "/parameters-new.xml");
			Files.deleteIfExists(outPath);
			
			JAXBContext context = JAXBContext.newInstance(ENOParameters.class);
			Marshaller jaxbMarshaller =  context.createMarshaller();			
			jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);	 
			
			jaxbMarshaller.marshal(enoParametersFinal, outPath.toFile());
			System.out.println("Writing time : "+(System.currentTimeMillis()-debut)+" ms");
			System.out.println("Write output to "+outPath);
			
		} catch (NullPointerException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			Assert.fail();
		}
	}
	
	@Test
	public void testValorizationXmlParameters() {
		try {
			String basePath = "src/test/resources/params/valorization";			
			File in = new File(String.format("%s/parameters-input.xml", basePath));								
			File outputFile = valorizatorParametersImpl.mergeParameters(in);
			File expectedFile = new File(String.format("%s/parameters-expected.xml", basePath));			
			Diff diff = xmlDiff.getDiff(outputFile,expectedFile);
			
			Assert.assertFalse(getDiffMessage(diff, basePath), diff.hasDifferences());
			
		} catch (NullPointerException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			Assert.fail();
		}
		
	}
	
	
	
	public void testReadingDefaultParameters() {
		try {
			ENOParameters enoParameters = valorizatorParametersImpl.getDefaultParameters();			
			
			Assert.assertEquals(enoParameters.getPipeline().getInFormat(), InFormat.DDI);
			Assert.assertEquals(enoParameters.getPipeline().getOutFormat(), OutFormat.FR);
			Assert.assertEquals(enoParameters.getPipeline().getOutFormat(), OutFormat.FR);
			Assert.assertEquals(enoParameters.getParameters().getStudyUnit(), StudyUnit.DEFAULT);

		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (NullPointerException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			Assert.fail();
		}
	}
	
	private String getDiffMessage(Diff diff, String path) {
		return String.format("Transformed output for %s should match expected XML document:\n %s", path,
				diff.toString());
	}
}
