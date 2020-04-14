package fr.insee.eno.params.validation;

import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import fr.insee.eno.Constants;

public class SchemaValidatorImpl implements SchemaValidator {
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SchemaValidatorImpl.class);
	
	private Schema schema;
	private Validator validator;
	
	public SchemaValidatorImpl() {
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			schema = sf.newSchema(Constants.ENO_PARAMETERS_XSD);
		} catch (SAXException e) {
			e.printStackTrace();
		}
		validator = schema.newValidator();
		
	}
		
	@Override
	public ValidationMessage validate(InputStream paramsIS)  {
		LOGGER.info("Validation of parameters file...");
		boolean valid=false;
		String message="";
		try {
			validateIS(paramsIS);
			valid=true;
		} catch (Exception e) {
			e.printStackTrace();
			message = e.getMessage();
			LOGGER.error(message);
		}
		finally {
			LOGGER.info("Validation :"+valid);
		}
		message = valid ? "Parameters respect schema." : message;
		return new ValidationMessage(message, valid);
	}

	public void validateIS(InputStream paramsIS) throws Exception {
		validator.reset();
		Source source = new StreamSource(paramsIS);
		source = toDOMSource(source);
		validator.validate(source);
	}

	public DOMSource toDOMSource(Source source) throws Exception {
		if (source instanceof DOMSource) {
			return (DOMSource) source;
		}
		Transformer trans = TransformerFactory.newInstance().newTransformer();
		DOMResult result = new DOMResult();
		trans.transform(source, result);
		return new DOMSource(result.getNode());
	}

}
