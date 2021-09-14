package fr.insee.eno.generation;

import java.io.File;

import fr.insee.eno.parameters.OutFormat;

public class DDI2FOGenerator extends In2outGenerator {

	@Override
	public File generate(File finalInput, byte[] parameters, String surveyName) throws Exception {
		return this.in2outGenerate(finalInput, parameters, surveyName, OutFormat.FO);
	}


	public String in2out() {
		return "ddi2fo";
	}

}
