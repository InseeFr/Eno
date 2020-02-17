package fr.insee.eno.params;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.parameters.ENOParameters;
import fr.insee.eno.parameters.LevelAbstract;
import fr.insee.eno.parameters.LevelQuestion;
import fr.insee.eno.parameters.LevelSequence;
import fr.insee.eno.transform.xsl.ClasspathURIResolver;
import fr.insee.eno.transform.xsl.EnoErrorListener;
import fr.insee.eno.transform.xsl.XslParameters;
import fr.insee.eno.transform.xsl.XslTransformation;


public class ValorizatorParametersImpl implements ValorizatorParameters {

	private static final Logger LOGGER = LoggerFactory.getLogger(ValorizatorParametersImpl.class);
	
	private XslTransformation saxonService = new XslTransformation();


	@Override
	public ByteArrayOutputStream mergeParameters(ENOParameters enoParameters) throws JAXBException, IllegalArgumentException, IllegalAccessException, IOException   {
		
		ByteArrayOutputStream tempByteArrayOutputStream = new ByteArrayOutputStream();
		
		JAXBContext context = JAXBContext.newInstance(ENOParameters.class);
		Marshaller jaxbMarshaller =  context.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.marshal(enoParameters, tempByteArrayOutputStream);
		
		InputStream PARAM_XSL = Constants.getInputStreamFromPath(Constants.MERGE_PARAMETERS_XSL);
		InputStream inputStream = new ByteArrayInputStream(tempByteArrayOutputStream.toByteArray());
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		try {
			saxonService.mergeEnoParameters(inputStream, outputStream, PARAM_XSL);
		}catch(Exception e) {
			String errorMessage = "An error was occured during the valorisation of parameters. "+e.getMessage();
			LOGGER.error(errorMessage);
			throw new EnoGenerationException(errorMessage);
		}
		
		tempByteArrayOutputStream.close();
		inputStream.close();
		PARAM_XSL.close();

		return outputStream;
	}
	
	@Override
	public File mergeParameters(File enoParameters) throws JAXBException, IllegalArgumentException, IllegalAccessException, IOException   {
		File finalParam = Constants.TEMP_FILE_PARAMS("new-params.xml");
		
		InputStream PARAM_XSL = Constants.getInputStreamFromPath(Constants.MERGE_PARAMETERS_XSL);
		InputStream inputStream = FileUtils.openInputStream(enoParameters);
		OutputStream outputStream = FileUtils.openOutputStream(finalParam);
		
		try {
			saxonService.mergeEnoParameters(inputStream, outputStream, PARAM_XSL);
		}catch(Exception e) {
			String errorMessage = "An error was occured during the valorisation of parameters. "+e.getMessage();
			LOGGER.error(errorMessage);
			throw new EnoGenerationException(errorMessage);
		}
		
		inputStream.close();
		outputStream.close();
		PARAM_XSL.close();
		
		return finalParam;
	}

	@Override
	public ENOParameters mergeEnoParameters(ENOParameters enoParameters) throws JAXBException, IOException, IllegalArgumentException, IllegalAccessException  {
		LOGGER.info("Merging eno Parameters");
		ByteArrayOutputStream outputStream = this.mergeParameters(enoParameters);
		ENOParameters finalEnoParam = this.getParameters(new ByteArrayInputStream(outputStream.toByteArray()));
		outputStream.close();
		return finalEnoParam;
	}

	


	/**
	 * 
	 * @return the java object representing parameters of default parameters xml file
	 * @throws JAXBException
	 * @throws IOException
	 */
	public ENOParameters getDefaultParameters() throws JAXBException, IOException {
		InputStream xmlParameters = Constants.getInputStreamFromPath(Constants.PARAMETERS_DEFAULT_XML);
		StreamSource xml = new StreamSource(xmlParameters);
		return this.getParameters(xml);
	}

	
	@Override
	public ENOParameters getParameters(InputStream inputStream) throws JAXBException, UnsupportedEncodingException {

		if (inputStream == null)
			return null;

		LOGGER.debug("Preparing to translate from XML to java");
		
		

		JAXBContext context = JAXBContext.newInstance(ENOParameters.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setListener(new UnmarshallLogger());

		ENOParameters enoParameters = (ENOParameters) unmarshaller.unmarshal(inputStream);

		return enoParameters;
	}

	public ENOParameters getParameters(StreamSource xmlStream) throws JAXBException, UnsupportedEncodingException {

		if (xmlStream == null)
			return null;

		LOGGER.debug("Preparing to translate from XML to java");

		JAXBContext context = JAXBContext.newInstance(ENOParameters.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setListener(new UnmarshallLogger());

		ENOParameters enoParameters = (ENOParameters) unmarshaller.unmarshal(xmlStream);

		return enoParameters;
	}

	private class UnmarshallLogger extends Unmarshaller.Listener {

		@Override
		public void beforeUnmarshal(Object target, Object parent) {
			LOGGER.debug("Before unmarshalling object " + target);
		}

		@Override
		public void afterUnmarshal(Object target, Object parent) {
			LOGGER.debug("After unmarshalling object " + target);
		}
	}

	public List<Field> getAllFields(List<Field> fields, Class<?> type) {
		fields.addAll(Arrays.asList(type.getDeclaredFields()));
		if (type.getSuperclass() != null) {
			getAllFields(fields, type.getSuperclass());
		}

		return fields;
	}
}
