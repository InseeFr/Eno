package fr.insee.eno.test;

import fr.insee.eno.generation.IdentityGenerator;
import fr.insee.eno.parameters.*;
import fr.insee.eno.params.ValorizatorParameters;
import fr.insee.eno.params.ValorizatorParametersImpl;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.postprocessing.lunaticxml.LunaticXMLPaginationPostprocessor;
import fr.insee.eno.preprocessing.NoopPreprocessor;
import fr.insee.eno.preprocessing.Preprocessor;
import fr.insee.eno.service.GenerationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xmlunit.diff.Diff;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class TestLunaticXMLPaginationPostProcessor {

	private XMLDiff xmlDiff = new XMLDiff();
	private ValorizatorParameters valorizatorParameters = new ValorizatorParametersImpl();

	@Test
	public void nonePagination(){
		try{
			paginationTest(Pagination.NONE);
		}catch (Exception e){}
	}

	@Test
	public void sequencePagination(){
		try{
			paginationTest(Pagination.SEQUENCE);
		}catch (Exception e){}
	}

	@Test
	public void questionPagination(){
		try{
			paginationTest(Pagination.QUESTION);
		}catch (Exception e){}
	}
	

	public void paginationTest(Pagination pagination) throws IOException {
		ByteArrayOutputStream parametersBAOS=null;
		try {
			String basePath = "src/test/resources/lunatic-xml-pagination";
			Preprocessor[] preprocessors = { new NoopPreprocessor() };
			IdentityGenerator identityGenerator = new IdentityGenerator();
			Postprocessor[] postprocessors =  { new LunaticXMLPaginationPostprocessor() };
			ENOParameters enoParameters = new ENOParameters();
			Pipeline pipeline = new Pipeline();
			pipeline.setOutFormat(OutFormat.LUNATIC_XML);
			enoParameters.setPipeline(pipeline);
			LunaticXMLParameters lunaticXMLParameters = new LunaticXMLParameters();
			lunaticXMLParameters.setPagination(pagination);
			Parameters paramaters = new Parameters();
			paramaters.setLunaticXmlParameters(lunaticXMLParameters);
			enoParameters.setParameters(paramaters);
			parametersBAOS = valorizatorParameters.mergeParameters(enoParameters);
			GenerationService genService = new GenerationService(preprocessors, identityGenerator, postprocessors);
			genService.setParameters(parametersBAOS);
		File in = new File(String.format("%s/in.xml", basePath));
			File outputFile = genService.generateQuestionnaire(in, "ddi-2-lunatic-xml-test/"+pagination.value());
			File expectedFile = new File(String.format("%s/out-"+ pagination.value() +".xml", basePath));


			Diff diff = xmlDiff.getDiff(outputFile,expectedFile);
			Assertions.assertFalse(diff::hasDifferences, ()->getDiffMessage(diff, basePath));
			
		} catch (IOException e) {
			e.printStackTrace();
			Assertions.fail();
		} catch (NullPointerException e) {
			e.printStackTrace();
			Assertions.fail();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			Assertions.fail();
		} finally {
			if(parametersBAOS != null) parametersBAOS.close();
		}
	}
	
	private String getDiffMessage(Diff diff, String path) {
		return String.format("Transformed output for %s should match expected XML document:\n %s", path,
				diff.toString());
	}
}
