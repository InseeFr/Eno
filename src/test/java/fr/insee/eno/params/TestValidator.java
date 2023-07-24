package fr.insee.eno.params;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import fr.insee.eno.parameters.InFormat;
import fr.insee.eno.parameters.Mode;
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
				PreProcessing.DDI_MULTIMODAL_SELECTION,
				PreProcessing.DDI_DEREFERENCING,
				PreProcessing.DDI_CLEANING,
				PreProcessing.DDI_TITLING,
				PreProcessing.POGUES_XML_GOTO_2_ITE);
		
		Pipeline pipeline = new Pipeline();		
		pipeline.setInFormat(InFormat.DDI);
		pipeline.getPreProcessing().addAll(preProcessings);
		
		ValidationMessage valid = validator.validatePreProcessings(pipeline);
		
		System.out.println(valid.getMessage());
		Assertions.assertFalse(valid.isValid());
		
	}
	

	
	@Test
	public void testValidatePostProcessings() {
		
		List<PreProcessing> preProcessings = Arrays.asList(
				PreProcessing.DDI_DEREFERENCING);
		List<PostProcessing> postProcessings = Arrays.asList(
				PostProcessing.XFORMS_BROWSING,
				PostProcessing.XFORMS_INSEE_MODEL);
		
		Pipeline pipeline = new Pipeline();
		pipeline.setInFormat(InFormat.DDI);
		pipeline.setOutFormat(OutFormat.XFORMS);
		pipeline.getPreProcessing().addAll(preProcessings);
		pipeline.getPostProcessing().addAll(postProcessings);
		
		//List<PreProcessing> preProcessings2 = Arrays.asList(
				//PreProcessing.DDI_DEREFERENCING);
		//List<PostProcessing> postProcessings2 = Arrays.asList(
				//PostProcessing.XFORMS_BROWSING);
		
		//Pipeline pipeline2 = new Pipeline();
		//pipeline2.setInFormat(InFormat.DDI);
		//pipeline2.setOutFormat(OutFormat.POGUES_XML);
		//pipeline2.getPreProcessing().addAll(preProcessings2);
		//pipeline2.getPostProcessing().addAll(postProcessings2);
		
		ValidationMessage valid = validator.validatePostProcessings(pipeline);
		//ValidationMessage valid2 = validator.validatePostProcessings(pipeline2);	
		
		
		
		System.out.println(valid.getMessage());
		Assertions.assertTrue(valid.isValid());
		//System.out.println(valid2.getMessage());
		//Assertions.assertFalse(valid2.isValid());
	}
	
	@Test
	public void testValidateIn2Out() {
		ValidationMessage valid0 = validator.validateIn2Out(InFormat.DDI, OutFormat.XFORMS);
		ValidationMessage valid1 = validator.validateIn2Out(InFormat.POGUES_XML, OutFormat.XFORMS);
		ValidationMessage valid2 = validator.validateIn2Out(InFormat.DDI, OutFormat.DDI);
		ValidationMessage valid3 = validator.validateIn2Out(InFormat.POGUES_XML, OutFormat.DDI);
		//ValidationMessage valid4 = validator.validateIn2Out(InFormat.DDI, OutFormat.POGUES_XML);
		
		System.out.println(valid0.getMessage());
		System.out.println(valid1.getMessage());
		System.out.println(valid2.getMessage());
		System.out.println(valid3.getMessage());
		//System.out.println(valid4.getMessage());
		
		Assertions.assertTrue(valid0.isValid());
		Assertions.assertFalse(valid1.isValid());
		Assertions.assertTrue(valid2.isValid());
		Assertions.assertTrue(valid3.isValid());
		//Assertions.assertTrue(valid4.isValid());
	}
	
	
	@Test
	public void validateModeLunatic() {
		ValidationMessage valid0 = validator.validateMode(OutFormat.LUNATIC_XML,Mode.CAWI);
		ValidationMessage valid1 = validator.validateMode(OutFormat.LUNATIC_XML,Mode.PAPI);
		ValidationMessage valid2 = validator.validateMode(OutFormat.LUNATIC_XML,Mode.CAPI);
		ValidationMessage valid3 = validator.validateMode(OutFormat.LUNATIC_XML,Mode.CATI);
		ValidationMessage valid4 = validator.validateMode(OutFormat.LUNATIC_XML,Mode.ALL);
		ValidationMessage valid5 = validator.validateMode(OutFormat.LUNATIC_XML,Mode.PROCESS);
		ValidationMessage valid6 = validator.validateMode(OutFormat.LUNATIC_XML,null);	
		
		
		Assertions.assertTrue(valid0.isValid());
		Assertions.assertFalse(valid1.isValid());
		Assertions.assertTrue(valid2.isValid());
		Assertions.assertTrue(valid3.isValid());
		Assertions.assertFalse(valid4.isValid());
		Assertions.assertTrue(valid5.isValid());
		Assertions.assertFalse(valid6.isValid());
		
	}
	
	
	@Test
	public void validateModeDDI() {
		
		ValidationMessage valida0 = validator.validateMode(OutFormat.DDI,Mode.CAWI);
		ValidationMessage valida1 = validator.validateMode(OutFormat.DDI,Mode.PAPI);
		ValidationMessage valida2 = validator.validateMode(OutFormat.DDI,Mode.CAPI);
		ValidationMessage valida3 = validator.validateMode(OutFormat.DDI,Mode.CATI);
		ValidationMessage valida4 = validator.validateMode(OutFormat.DDI,Mode.ALL);
		ValidationMessage valida5 = validator.validateMode(OutFormat.DDI,Mode.PROCESS);
		ValidationMessage valida6 = validator.validateMode(OutFormat.DDI,null);	
		
		
		Assertions.assertFalse(valida0.isValid());
		Assertions.assertFalse(valida1.isValid());
		Assertions.assertFalse(valida2.isValid());
		Assertions.assertFalse(valida3.isValid());
		Assertions.assertTrue(valida4.isValid());
		Assertions.assertFalse(valida5.isValid());
		Assertions.assertTrue(valida6.isValid());
		
	}
	
	@Test
	public void validateModeFO() {

		
		ValidationMessage validb0 = validator.validateMode(OutFormat.FO,Mode.CAWI);
		ValidationMessage validb1 = validator.validateMode(OutFormat.FO,Mode.PAPI);
		ValidationMessage validb2 = validator.validateMode(OutFormat.FO,Mode.CAPI);
		ValidationMessage validb3 = validator.validateMode(OutFormat.FO,Mode.CATI);
		ValidationMessage validb4 = validator.validateMode(OutFormat.FO,Mode.ALL);
		ValidationMessage validb5 = validator.validateMode(OutFormat.FO,Mode.PROCESS);
		ValidationMessage validb6 = validator.validateMode(OutFormat.FO,null);	
	
		
		Assertions.assertFalse(validb0.isValid());
		Assertions.assertTrue(validb1.isValid());
		Assertions.assertFalse(validb2.isValid());
		Assertions.assertFalse(validb3.isValid());
		Assertions.assertFalse(validb4.isValid());
		Assertions.assertFalse(validb5.isValid());
		Assertions.assertTrue(validb6.isValid());
	}
	
	@Test
	public void validateModeFODT() {
		
		ValidationMessage validc0 = validator.validateMode(OutFormat.FODT,Mode.CAWI);
		ValidationMessage validc1 = validator.validateMode(OutFormat.FODT,Mode.PAPI);
		ValidationMessage validc2 = validator.validateMode(OutFormat.FODT,Mode.CAPI);
		ValidationMessage validc3 = validator.validateMode(OutFormat.FODT,Mode.CATI);
		ValidationMessage validc4 = validator.validateMode(OutFormat.FODT,Mode.ALL);
		ValidationMessage validc5 = validator.validateMode(OutFormat.FODT,Mode.PROCESS);
		ValidationMessage validc6 = validator.validateMode(OutFormat.FODT,null);
		
		Assertions.assertFalse(validc0.isValid());
		Assertions.assertFalse(validc1.isValid());
		Assertions.assertFalse(validc2.isValid());
		Assertions.assertFalse(validc3.isValid());
		Assertions.assertTrue(validc4.isValid());
		Assertions.assertFalse(validc5.isValid());
		Assertions.assertTrue(validc6.isValid());
		
	}
	
	@Test
	public void validateMode() {
		
		ValidationMessage validd0 = validator.validateMode(OutFormat.XFORMS,Mode.CAWI);
		ValidationMessage validd1 = validator.validateMode(OutFormat.XFORMS,Mode.PAPI);
		ValidationMessage validd2 = validator.validateMode(OutFormat.XFORMS,Mode.CAPI);
		ValidationMessage validd3 = validator.validateMode(OutFormat.XFORMS,Mode.CATI);
		ValidationMessage validd4 = validator.validateMode(OutFormat.XFORMS,Mode.ALL);
		ValidationMessage validd5 = validator.validateMode(OutFormat.XFORMS,Mode.PROCESS);
		ValidationMessage validd6 = validator.validateMode(OutFormat.XFORMS,null);	
		
		Assertions.assertTrue(validd0.isValid());
		Assertions.assertFalse(validd1.isValid());
		Assertions.assertFalse(validd2.isValid());
		Assertions.assertFalse(validd3.isValid());
		Assertions.assertFalse(validd4.isValid());
		Assertions.assertFalse(validd5.isValid());
		Assertions.assertTrue(validd6.isValid());
	}
}
