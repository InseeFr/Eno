package fr.insee.eno.transform.xsl;

import java.io.File;

import javax.xml.transform.Transformer;

public class XslTransformationIncorporation implements XslTransformation {
	
	private File  generatedFileParameter;
	
	public XslTransformationIncorporation(File generatedFileParameter) {
	this.generatedFileParameter = generatedFileParameter;
	}
	
	

	@Override
	public Transformer setParameters(Transformer transformer) {
		transformer.setParameter(XslParameters.INCORPORATION_GENERATED_FILE, generatedFileParameter.toURI());
		return transformer;
	}
	

}
