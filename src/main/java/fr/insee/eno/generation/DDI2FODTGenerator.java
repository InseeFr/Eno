package fr.insee.eno.generation;

import java.io.File;

import fr.insee.eno.parameters.OutFormat;

public class DDI2FODTGenerator extends In2outGenerator {

	@Override
	public File generate(File finalInput, byte[] parameters, String surveyName) throws Exception {
		return this.in2outGenerate(finalInput, parameters, surveyName, OutFormat.FODT);
	}

	@Override
	public String in2out() {
		return "ddi2fodt";
	}
}
