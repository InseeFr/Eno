package fr.insee.eno.transform.xsl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;

import fr.insee.eno.Constants;

public class XslTransformXformsMetadata implements XslTransformation {
	
	private final byte[] parameters;
	private final byte[] metadata;

	public XslTransformXformsMetadata(byte[] parameters, byte[] metadata) {
		this.parameters=parameters;
		this.metadata=metadata;
	}



	@Override
	public Transformer setParameters(Transformer transformer) {
	
		transformer.setParameter(XslParameters.IN2OUT_PROPERTIES_FILE, Constants.CONFIG_DDI2XFORMS);
		transformer.setParameter(XslParameters.IN2OUT_PARAMETERS_FILE, Constants.PARAMETERS_DEFAULT);
		transformer.setParameter(XslParameters.IN2OUT_METADATA_FILE, Constants.METADATA_DEFAULT);
		if (metadata != null) {
			
			InputStream metadataIS = new ByteArrayInputStream(metadata);
			Source source = new StreamSource(metadataIS);
			transformer.setParameter(XslParameters.IN2OUT_METADATA_NODE, source);
		}
		if (parameters != null) {
			InputStream parametersIS = new ByteArrayInputStream(parameters);
			Source source = new StreamSource(parametersIS);
			transformer.setParameter(XslParameters.IN2OUT_PARAMETERS_NODE, source);
		}
		return transformer;
	}
	
	
	
	

}
