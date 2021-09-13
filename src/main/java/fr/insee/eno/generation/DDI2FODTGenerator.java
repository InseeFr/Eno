package fr.insee.eno.generation;

import java.io.File;

import fr.insee.eno.parameters.OutFormat;

public class DDI2FODTGenerator implements Generator {

	private In2outGenerator in2outGenerator = new In2outGenerator();

	@Override
	public File generate(File finalInput, byte[] parameters, String surveyName) throws Exception {
		return in2outGenerator.in2outGenerate(finalInput, parameters, surveyName, OutFormat.FODT);
	}

	@Override
	public String in2out() {
		return "ddi2fodt";
	}
}
