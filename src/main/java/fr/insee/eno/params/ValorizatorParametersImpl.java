package fr.insee.eno.params;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.exception.Utils;
import fr.insee.eno.parameters.ENOParameters;
import fr.insee.eno.transform.xsl.XslTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.*;


public class ValorizatorParametersImpl implements ValorizatorParameters {

	private static final Logger LOGGER = LoggerFactory.getLogger(ValorizatorParametersImpl.class);
	
	private XslTransformation saxonService = new XslTransformation();

	private static final String styleSheetPath = Constants.MERGE_PARAMETERS_XSL;


	@Override
	public ByteArrayOutputStream mergeParameters(ENOParameters enoParameters) throws JAXBException, IllegalArgumentException, IllegalAccessException, IOException   {
		
		ByteArrayOutputStream tempByteArrayOutputStream = new ByteArrayOutputStream();
		
		JAXBContext context = JAXBContext.newInstance(ENOParameters.class);
		Marshaller jaxbMarshaller =  context.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.marshal(enoParameters, tempByteArrayOutputStream);
		
		InputStream PARAM_XSL = Constants.getInputStreamFromPath(styleSheetPath);
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

	/**
	 *
	 * @param enoParametersIS (InputStream)
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	@Override
	public ByteArrayOutputStream mergeParameters(InputStream enoParametersIS) throws IllegalArgumentException, IOException   {
		InputStream PARAM_XSL = Constants.getInputStreamFromPath(styleSheetPath);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		try(enoParametersIS; PARAM_XSL) {
			saxonService.mergeEnoParameters(enoParametersIS, outputStream, PARAM_XSL);
		}catch(Exception e) {
			String errorMessage = String.format("An error was occured during the valorisation of parameters. %s : %s",
					e.getMessage(),
					Utils.getErrorLocation(styleSheetPath,e));
			LOGGER.error(errorMessage);
			throw new EnoGenerationException(errorMessage);
		}
		return outputStream;
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
	public ENOParameters getParameters(InputStream inputStream) throws JAXBException {

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
}
