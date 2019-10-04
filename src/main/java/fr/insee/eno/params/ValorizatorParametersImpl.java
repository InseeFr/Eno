package fr.insee.eno.params;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.parameters.ENOParameters;
import fr.insee.eno.parameters.LevelAbstract;
import fr.insee.eno.parameters.LevelQuestion;
import fr.insee.eno.parameters.LevelSequence;


public class ValorizatorParametersImpl implements ValorizatorParameters {

	private static final Logger LOGGER = LoggerFactory.getLogger(ValorizatorParametersImpl.class);


	@Override
	public ByteArrayOutputStream mergeParameters(ENOParameters enoParameters) throws JAXBException, IllegalArgumentException, IllegalAccessException, IOException   {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ENOParameters enoParametersFinal = mergeEnoParameters(enoParameters);		
		JAXBContext context = JAXBContext.newInstance(ENOParameters.class);
		Marshaller jaxbMarshaller =  context.createMarshaller();			
		jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);		
		jaxbMarshaller.marshal(enoParametersFinal, byteArrayOutputStream);

		return byteArrayOutputStream;
	}
	
	@Override
	public File mergeParameters(File enoParameters) throws JAXBException, IllegalArgumentException, IllegalAccessException, IOException   {
		File output = Constants.TEMP_FILE_PARAMS("new-params.xml");		
		InputStream paramsIS = FileUtils.openInputStream(enoParameters);
		ENOParameters enoParms = getParameters(paramsIS);
		ENOParameters enoParametersFinal = mergeEnoParameters(enoParms);		
		JAXBContext context = JAXBContext.newInstance(ENOParameters.class);
		Marshaller jaxbMarshaller =  context.createMarshaller();			
		jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.marshal(enoParametersFinal, output);

		return output;
	}

	@Override
	public ENOParameters mergeEnoParameters(ENOParameters enoParameters) throws JAXBException, IOException, IllegalArgumentException, IllegalAccessException  {
		LOGGER.info("Merging eno Parameters");
		ENOParameters enoParametersDefault = getDefaultParameters();
		LOGGER.info("Default parameters read");
		return mergeEnoParameters(enoParametersDefault, enoParameters);
	}

	public <T> T mergeEnoParameters(T enoParamsDefault, T newEnoParams) throws IllegalArgumentException, IllegalAccessException  {
		Class<?> objectClass = enoParamsDefault.getClass();
		LOGGER.debug("Class's name : "+objectClass.getSimpleName());
		Object merged;		
		// if the class of the object is a Primitive or similar, or an Enumeration, we do a simple merge otherwise:
		if(isComplexeType(objectClass)){
			merged = enoParamsDefault;
			// we recover each fields which constitutes the recovered object
			for (Field field : getAllFields(new ArrayList<>(), objectClass)) {
				String className = field.getType().getSimpleName();
				LOGGER.debug(" Name/Type of field :"+field.getName()+"/"+className);
				field.setAccessible(true);

				Object enoParamsDefaultValue;
				Object newEnoParamsValue;
				// we recover the value of each
				enoParamsDefaultValue = field.get(enoParamsDefault);
				newEnoParamsValue = newEnoParams!=null ? field.get(newEnoParams) : null;
				
				if (className != null) {
					if(enoParamsDefaultValue!=null) {
						
						// Special case for List (if empty we keep default value)
						if(className.equals(List.class.getSimpleName())) {
							if(newEnoParamsValue==null || ((List) newEnoParamsValue).isEmpty()) {
								field.set(merged, enoParamsDefaultValue);
								LOGGER.debug("List : No overloaded, default value");
							}
							else {
								// Special case for LevelQuestion and LevelSequence
								Class<?> levelClass = ((List) newEnoParamsValue).get(0).getClass();
								if(levelClass.equals(LevelQuestion.class) || levelClass.equals(LevelSequence.class)) {									
									field.set(merged, mergeListNumerotation(enoParamsDefaultValue, newEnoParamsValue));
									LOGGER.debug("List : Special overloaded");	
								}
								else {
									field.set(merged, newEnoParamsValue);
									LOGGER.debug("List : overloaded, new value");	
								}
							}
						}
						else {
							// Recursive call
							if(isComplexeType(field.getType())) {
								LOGGER.debug("Merging... : recursive call");
								field.set(merged, this.mergeEnoParameters(enoParamsDefaultValue, newEnoParamsValue));
							}
							// if field's class is a Primitive or similar, or an Enumeration, we do a simple merge.
							else {
								LOGGER.debug("Simple merge of field : values merged");
								field.set(merged,  (newEnoParamsValue != null) ? newEnoParamsValue : enoParamsDefaultValue);
							}
						}
					}
					else {
						// Case if defaultValue of field is null
						field.set(merged, newEnoParamsValue);
					}
					
					
				}
			}
		}
		else {
			// merge simple: new value if it is not null, if not the default one
			LOGGER.debug("Simple merge of simple class : values merged");
			merged = (newEnoParams != null) ? newEnoParams : enoParamsDefault;
		}
		return (T) merged;
	}


	public ENOParameters getDefaultParameters() throws JAXBException, IOException {
		InputStream xmlParameters = Constants.getInputStreamFromPath(Constants.PARAMETERS_DEFAULT_XML);
		StreamSource xml = new StreamSource(xmlParameters);
		return this.getParameters(xml);
	}

	@Override
	public ENOParameters getParameters(String xmlString) throws JAXBException, UnsupportedEncodingException {

		if ((xmlString == null) || (xmlString.length() == 0))
			return null;
		StreamSource xml = new StreamSource(new StringReader(xmlString));

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

	@Override
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
			//LOGGER.debug("Before unmarshalling object " + target);
		}

		@Override
		public void afterUnmarshal(Object target, Object parent) {
			//LOGGER.debug("After unmarshalling object " + target);
		}
	}

	public List<Field> getAllFields(List<Field> fields, Class<?> type) {
		fields.addAll(Arrays.asList(type.getDeclaredFields()));
		if (type.getSuperclass() != null) {
			getAllFields(fields, type.getSuperclass());
		}

		return fields;
	}
	
	
	public List<LevelAbstract> mergeListNumerotation(Object enoParamsDefaultValue, Object newEnoParamsValue) throws IllegalArgumentException, IllegalAccessException  {
		List<LevelAbstract> mergedList = new ArrayList<>();
		for(LevelAbstract levelDefault : (List<LevelAbstract>) enoParamsDefaultValue) {
			int size = mergedList.size();
			for(LevelAbstract  levelNew : (List<LevelAbstract>) newEnoParamsValue) {
				if(levelDefault.getName().equals(levelNew.getName())) {
					mergedList.add(this.mergeEnoParameters(levelDefault, levelNew));
				}
			}
			if(mergedList.size()==size) {
				mergedList.add(levelDefault);
			}
		}
		return mergedList;
	}
	
	public boolean isComplexeType(Class<?> objectClass) {
		List<String> PRIMITIVE_JAVA_OBJECT = Arrays.asList("Long", "Long[]", "Integer", "Integer[]", "String", "String[]", "Boolean", "boolean[]", "ArrayList", "LinkedHashMap");
		return !(objectClass.isPrimitive() || objectClass.isEnum() || PRIMITIVE_JAVA_OBJECT.contains(objectClass.getSimpleName()));
	}


}
