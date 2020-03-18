package fr.insee.eno.params;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlunit.diff.Diff;

import fr.insee.eno.Constants;
import fr.insee.eno.parameters.AccompanyingMail;
import fr.insee.eno.parameters.BrowsingEnum;
import fr.insee.eno.parameters.ENOParameters;
import fr.insee.eno.parameters.XFORMSParameters;
import fr.insee.eno.parameters.Format;
import fr.insee.eno.parameters.GlobalNumbering;
import fr.insee.eno.parameters.InFormat;
import fr.insee.eno.parameters.LunaticXMLParameters;
import fr.insee.eno.parameters.Language;
import fr.insee.eno.parameters.LevelEnum;
import fr.insee.eno.parameters.LevelQuestion;
import fr.insee.eno.parameters.NumberingQuestion;
import fr.insee.eno.parameters.Orientation;
import fr.insee.eno.parameters.OutFormat;
import fr.insee.eno.parameters.FOParameters;
import fr.insee.eno.parameters.Parameters;
import fr.insee.eno.parameters.Parameters.Languages;
import fr.insee.eno.parameters.Pipeline;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.parameters.PreProcessing;
import fr.insee.eno.parameters.Context;
import fr.insee.eno.parameters.Table;
import fr.insee.eno.parameters.Table.Row;
import fr.insee.eno.test.XMLDiff;

public class TestValorizatorParametersImpl {

	private ValorizatorParametersImpl valorizatorParametersImpl;
	private ENOParameters complexeEnoParameters;
	private ENOParameters simpleEnoParameters;

	private XMLDiff xmlDiff = new XMLDiff();

	@Before
	public void setValorizator() {
		valorizatorParametersImpl = new ValorizatorParametersImpl();
	}

	@Before
	public void setComplexeEnoParameters() {
		complexeEnoParameters = new ENOParameters();

		Pipeline pipeline = new Pipeline();
		pipeline.setInFormat(InFormat.DDI);
		pipeline.setOutFormat(OutFormat.FO);
		pipeline.getPostProcessing().add(PostProcessing.FO_INSERT_ACCOMPANYING_MAILS);

		complexeEnoParameters.setPipeline(pipeline);
		Parameters parameters = new Parameters();
		parameters.setContext(Context.HOUSEHOLD);

		Languages languages = new Languages();
		languages.getLanguage().add(Language.DE);
		languages.getLanguage().add(Language.FR);
		languages.getLanguage().add(Language.EN);
		parameters.setLanguages(languages);


		FOParameters foParameters = new FOParameters();
		Table table = new Table();
		Row row = new Row();
		row.setDefaultSize(150);
		table.setRow(row);
		Format format = new Format();
		format.setOrientation(Orientation.LANDSCAPE);		
		foParameters.setTable(table);
		foParameters.setFormat(format);				

		foParameters.setAccompanyingMail(AccompanyingMail.CNR_COL);

		GlobalNumbering globalNumerotation = new GlobalNumbering();
		globalNumerotation.setBrowsing(BrowsingEnum.NO_NUMBER);

		NumberingQuestion question = new NumberingQuestion();
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
		parameters.setFoParameters(foParameters);

		XFORMSParameters xformsParameters = new XFORMSParameters();
		xformsParameters.setLengthOfLongTable(56);
		xformsParameters.setNumericExample(true);

		parameters.setXformsParameters(xformsParameters);
		
		LunaticXMLParameters lunaticXMLParameters = new LunaticXMLParameters();
		lunaticXMLParameters.setFilterDescription(true);
		
		parameters.setLunaticXmlParameters(lunaticXMLParameters);


		complexeEnoParameters.setParameters(parameters);
	}

	@Before
	public void setSimpleEnoParameters() {
		simpleEnoParameters = new ENOParameters();
		Pipeline pipeline = new Pipeline();
		pipeline.setInFormat(InFormat.DDI);
		pipeline.setOutFormat(OutFormat.XFORMS);
		pipeline.getPreProcessing().addAll(Arrays.asList(
				PreProcessing.DDI_DEREFERENCING,
				PreProcessing.DDI_CLEANING,
				PreProcessing.DDI_TITLING));
		pipeline.getPostProcessing().addAll(Arrays.asList(
				PostProcessing.XFORMS_BROWSING,
				PostProcessing.XFORMS_FIX_ADHERENCE));
		simpleEnoParameters.setPipeline(pipeline);
	}

	@Test
	public void testValorizationComplexeJavaParameters() {
		try {			

			long debut = System.currentTimeMillis();
			ENOParameters enoParametersFinal = valorizatorParametersImpl.mergeEnoParameters(complexeEnoParameters);
			System.out.println("Merging time : "+(System.currentTimeMillis()-debut)+" ms");
			// Pipeline
			Assert.assertEquals(InFormat.DDI, enoParametersFinal.getPipeline().getInFormat());
			Assert.assertEquals(OutFormat.FO, enoParametersFinal.getPipeline().getOutFormat());
			// New value
			Assert.assertEquals(Arrays.asList(PostProcessing.FO_INSERT_ACCOMPANYING_MAILS), enoParametersFinal.getPipeline().getPostProcessing());

			// Browsing
			Assert.assertEquals(BrowsingEnum.NO_NUMBER, enoParametersFinal.getParameters().getTitle().getBrowsing());

			//Context 
			Assert.assertEquals(Context.HOUSEHOLD, enoParametersFinal.getParameters().getContext());

			//AccompanyingMail
			Assert.assertEquals(AccompanyingMail.CNR_COL, enoParametersFinal.getParameters().getFoParameters().getAccompanyingMail());
			
			
			debut = System.currentTimeMillis();
			Path outPath = Paths.get(Constants.TEMP_FOLDER_PATH + "/complexe-parameters-new.xml");
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
	public void testValorizationSimpleJavaParameters() {
		try {			

			long debut = System.currentTimeMillis();
			ENOParameters enoParametersFinal = valorizatorParametersImpl.mergeEnoParameters( 
					simpleEnoParameters);
			System.out.println("Merging time : "+(System.currentTimeMillis()-debut)+" ms");
			ENOParameters enoParametersDefault = valorizatorParametersImpl.getDefaultParameters();			



			debut = System.currentTimeMillis();
			Path outPath = Paths.get(Constants.TEMP_FOLDER_PATH + "/simple-parameters-new.xml");
			Files.deleteIfExists(outPath);

			JAXBContext context = JAXBContext.newInstance(ENOParameters.class);
			Marshaller jaxbMarshaller =  context.createMarshaller();			
			jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);	 

			jaxbMarshaller.marshal(enoParametersFinal, outPath.toFile());
			System.out.println("Writing time : "+(System.currentTimeMillis()-debut)+" ms");
			System.out.println("Write output to "+outPath);

			// Pipeline
			Assert.assertEquals(InFormat.DDI, enoParametersFinal.getPipeline().getInFormat());
			Assert.assertEquals(OutFormat.XFORMS, enoParametersFinal.getPipeline().getOutFormat());
			//PreProcessing value
			Assert.assertEquals(
					Arrays.asList(PreProcessing.DDI_DEREFERENCING,PreProcessing.DDI_CLEANING,PreProcessing.DDI_TITLING),
					enoParametersFinal.getPipeline().getPreProcessing());
			// PostProcessing value
			Assert.assertEquals(
					Arrays.asList(PostProcessing.XFORMS_BROWSING,PostProcessing.XFORMS_FIX_ADHERENCE),
					enoParametersFinal.getPipeline().getPostProcessing());


			//Other params 
			Assert.assertEquals(
					enoParametersDefault.getParameters().getContext(), 
					enoParametersFinal.getParameters().getContext());
			Assert.assertEquals(
					enoParametersDefault.getParameters().getXformsParameters().getDecimalSeparator(), 
					enoParametersFinal.getParameters().getXformsParameters().getDecimalSeparator());

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
			Assert.assertEquals(enoParameters.getPipeline().getOutFormat(), OutFormat.XFORMS);
			Assert.assertEquals(enoParameters.getPipeline().getOutFormat(), OutFormat.XFORMS);
			Assert.assertEquals(enoParameters.getParameters().getContext(), Context.DEFAULT);

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
