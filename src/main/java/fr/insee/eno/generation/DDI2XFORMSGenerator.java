package fr.insee.eno.generation;

import java.io.File;

import fr.insee.eno.parameters.OutFormat;

public class DDI2XFORMSGenerator extends In2outGenerator {
	
	@Override
	public File generate(File finalInput, byte[] parameters, String surveyName) throws Exception {
		return this.in2outGenerate(finalInput, parameters, surveyName, OutFormat.XFORMS);
	}


	public String in2out() {
		return "ddi2xforms";
	}

}
