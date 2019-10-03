package fr.insee.eno.params;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import fr.insee.eno.parameters.InFormat;
import fr.insee.eno.parameters.OutFormat;
import fr.insee.eno.parameters.Pipeline;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.parameters.PreProcessing;
import fr.insee.eno.params.validation.ValidationMessage;
import fr.insee.eno.params.validation.Validator;
import fr.insee.eno.params.validation.ValidatorImpl;

public class TestValidator {
	
	private Validator validator = new ValidatorImpl();
	
	@Test
	public void testValidatePreProcessings() {
		
		List<PreProcessing> preProcessings = Arrays.asList(
				PreProcessing.DDI_DEREFERENCING,
				PreProcessing.DDI_CLEANING,
				PreProcessing.DDI_TITLING,
				PreProcessing.POGUES_XML_GOTO_2_ITE);
		
		Pipeline pipeline = new Pipeline();		
		pipeline.setInFormat(InFormat.DDI);
		pipeline.getPreProcessing().addAll(preProcessings);
		
		ValidationMessage valid = validator.validatePreProcessings(pipeline);
		
		System.out.println(valid.getMessage());
		Assert.assertFalse(valid.isValid());
		
	}
	
	@Test
	public void testValidatePostProcessings() {
		
		List<PreProcessing> preProcessings = Arrays.asList(
				PreProcessing.DDI_DEREFERENCING);
		List<PostProcessing> postProcessings = Arrays.asList(
				PostProcessing.FR_BROWSING,
				PostProcessing.FR_MODELE_COLTRANE);
		
		Pipeline pipeline = new Pipeline();
		pipeline.setInFormat(InFormat.DDI);
		pipeline.setOutFormat(OutFormat.FR);
		pipeline.getPreProcessing().addAll(preProcessings);
		pipeline.getPostProcessing().addAll(postProcessings);
		
		List<PreProcessing> preProcessings2 = Arrays.asList(
				PreProcessing.DDI_DEREFERENCING);
		List<PostProcessing> postProcessings2 = Arrays.asList(
				PostProcessing.FR_BROWSING);
		
		Pipeline pipeline2 = new Pipeline();
		pipeline2.setInFormat(InFormat.DDI);
		pipeline2.setOutFormat(OutFormat.POGUES_XML);
		pipeline2.getPreProcessing().addAll(preProcessings2);
		pipeline2.getPostProcessing().addAll(postProcessings2);
		
		ValidationMessage valid = validator.validatePostProcessings(pipeline);
		ValidationMessage valid2 = validator.validatePostProcessings(pipeline2);	
		
		
		
		System.out.println(valid.getMessage());
		Assert.assertTrue(valid.isValid());
		System.out.println(valid2.getMessage());
		Assert.assertFalse(valid2.isValid());
	}
	
	@Test
	public void testValidateIn2Out() {
		ValidationMessage valid0 = validator.validateIn2Out(InFormat.DDI, OutFormat.FR);
		ValidationMessage valid1 = validator.validateIn2Out(InFormat.POGUES_XML, OutFormat.FR);
		ValidationMessage valid2 = validator.validateIn2Out(InFormat.DDI, OutFormat.DDI);
		ValidationMessage valid3 = validator.validateIn2Out(InFormat.POGUES_XML, OutFormat.DDI);
		ValidationMessage valid4 = validator.validateIn2Out(InFormat.DDI, OutFormat.POGUES_XML);
		
		System.out.println(valid0.getMessage());
		System.out.println(valid1.getMessage());
		System.out.println(valid2.getMessage());
		System.out.println(valid3.getMessage());
		System.out.println(valid4.getMessage());
		
		Assert.assertTrue(valid0.isValid());
		Assert.assertFalse(valid1.isValid());
		Assert.assertTrue(valid2.isValid());
		Assert.assertTrue(valid3.isValid());
		Assert.assertTrue(valid4.isValid());
	}
}
