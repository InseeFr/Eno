package fr.insee.eno.transform.xsl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;

import fr.insee.eno.Constants;

public class XslTransformFo implements XslTransformation {
	
	private final String surveyName;
	private final String formName;
	private final byte[] parameters;

	public XslTransformFo(byte[] parameters,String surveyName, String formName) {
		this.parameters=parameters;
		this.surveyName=surveyName;
		this.formName=formName;
	}

	@Override
	public Transformer setParameters(Transformer transformer) {
	
		transformer.setParameter(XslParameters.IN2OUT_SURVEY_NAME, surveyName);
		transformer.setParameter(XslParameters.IN2OUT_FORM_NAME, formName);
		transformer.setParameter(XslParameters.IN2OUT_PROPERTIES_FILE, Constants.CONFIG_DDI2FO);
		transformer.setParameter(XslParameters.IN2OUT_PARAMETERS_FILE, Constants.PARAMETERS_DEFAULT);
		if (parameters != null) {
			InputStream parametersIS = new ByteArrayInputStream(parameters);
			parametersIS = new ByteArrayInputStream(parameters);
			Source source = new StreamSource(parametersIS);
			transformer.setParameter(XslParameters.IN2OUT_PARAMETERS_NODE, source);
		}
		return transformer;
	}
	

}
