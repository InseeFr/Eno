package fr.insee.eno.transform.xsl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;

import fr.insee.eno.Constants;
import fr.insee.eno.parameters.OutFormat;
import fr.insee.eno.parameters.PostProcessing;

public class XslTransformSimplePost implements XslTransformation {

	private String config;
	private final byte[] parameters;

	public XslTransformSimplePost(byte[] parameters, OutFormat outFormat) {
		this.parameters=parameters;
		if (outFormat.equals(OutFormat.XFORMS)) {
			config = Constants.CONFIG_DDI2XFORMS;
		}
		if (outFormat.equals(OutFormat.FODT)) {
			config = Constants.CONFIG_DDI2FODT;
		}
		if (outFormat.equals(OutFormat.LUNATIC_XML)) {
			config = Constants.CONFIG_DDI2LUNATIC_XML;
		}
		if (outFormat.equals(OutFormat.FO)) {
			config = Constants.CONFIG_DDI2FO;
		}
		if (outFormat.equals(OutFormat.DDI)) {
			config = Constants.CONFIG_POGUES_XML2DDI;
		}

	}



	@Override
	public Transformer setParameters(Transformer transformer) {
		
		transformer.setParameter(XslParameters.IN2OUT_LABELS_FOLDER, Constants.LABELS_FOLDER);
		
		transformer.setParameter(XslParameters.IN2OUT_PROPERTIES_FILE, config);
		
		transformer.setParameter(XslParameters.IN2OUT_PARAMETERS_FILE, Constants.PARAMETERS_DEFAULT);
		if (parameters != null) {
			InputStream parametersIS = new ByteArrayInputStream(parameters);
			Source source = new StreamSource(parametersIS);
			transformer.setParameter(XslParameters.IN2OUT_PARAMETERS_NODE, source);
		}
		return transformer;
	}
	
	
	
	

}
