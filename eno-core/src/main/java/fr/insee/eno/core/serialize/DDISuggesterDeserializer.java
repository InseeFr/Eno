package fr.insee.eno.core.serialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.suggester.SuggesterConfigurationDTO;
import lombok.Getter;
import lombok.Setter;

/** Deserializer for the suggester configuration that is described as a xml string in a CDATA in DDI. */
public class DDISuggesterDeserializer {

    /** Wrapper class to deserialize CDATA content. */
    @Getter
    @Setter
    private static class SuggesterWrapperDTO {
        @JacksonXmlCData
        @JacksonXmlText
        String content;
    }

    private final XmlMapper xmlMapper;

    public DDISuggesterDeserializer() {
        xmlMapper = XmlMapper.builder().defaultUseWrapper(false).build();
    }

    /** This is actually not useful for now since the DDI Java lib unwraps the CDATA when using the getStringValue()
     * method. Yet it was a bit tricky to do and might become useful someday. */
    public SuggesterConfigurationDTO deserializeFromCData(String xmlString) {
        String cdataContent;
        try {
            // Xml content cannot start with CDATA
            String validXml = "<content>" + xmlString + "</content>";
            // Unwrap CDATA content
            cdataContent = xmlMapper.readValue(validXml, SuggesterWrapperDTO.class).getContent();
        } catch (JsonProcessingException e) {
            throw new MappingException("Error occurred when unwrapping CDATA content.", e);
        }
        return deserialize(cdataContent);
    }

    public SuggesterConfigurationDTO deserialize(String xmlString) {
        try {
            // Wrap xml content by any root tag
            String xmlSuggesterConfiguration = "<options>" + xmlString + "</options>";
            return xmlMapper.readValue(xmlSuggesterConfiguration, SuggesterConfigurationDTO.class);
        } catch (JsonProcessingException e) {
            throw new MappingException("Error occurred when trying to deserialize suggester configuration.", e);
        }
    }

}
