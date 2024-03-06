package fr.insee.eno.core.serialize;

import fr.insee.eno.core.model.suggester.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DDISuggesterDeserializerTest {

    @Test
    void deserializeSuggesterConfiguration_fromCData() {
        String xmlString = "<![CDATA[<fields/>]]>";
        SuggesterConfigurationDTO suggesterConfigurationDTO = new DDISuggesterDeserializer().deserializeFromCData(xmlString);
        assertNotNull(suggesterConfigurationDTO);
    }

    // All the tests below work the same if the xml string input is wrapped with <![CDATA[...]]>

    @Test
    void deserializeSuggesterConfiguration() {
        String xmlString = "<fields/>";
        SuggesterConfigurationDTO suggesterConfigurationDTO = new DDISuggesterDeserializer().deserialize(xmlString);
        assertNotNull(suggesterConfigurationDTO);
    }

    @Test
    void deserializeSuggesterField() {
        String xmlString = """
                <fields xmlns="http://xml.insee.fr/schema/applis/lunatic-h">
                     <name>label</name>
                     <rules>[\\w]+</rules>
                     <language>French</language>
                     <min>3</min>
                     <stemmer>false</stemmer>
                </fields>""";
        SuggesterConfigurationDTO suggesterConfigurationDTO = new DDISuggesterDeserializer().deserialize(xmlString);
        assertEquals(1, suggesterConfigurationDTO.getFields().size());
        SuggesterFieldDTO suggesterFieldDTO = suggesterConfigurationDTO.getFields().getFirst();
        assertEquals("label", suggesterFieldDTO.getName());
        assertEquals(List.of("[\\w]+"), suggesterFieldDTO.getRules());
        assertEquals(3, suggesterFieldDTO.getMin());
        assertEquals(false, suggesterFieldDTO.getStemmer());
    }

    @Test
    void deserializeFieldSynonyms() {
        String xmlString = """
                <fields>
                    <synonyms>
                        <source>foo</source>
                        <target>BAR</target>
                    </synonyms>
                    <synonyms>
                        <source>example</source>
                        <target>INSTANCE</target>
                        <target>SAMPLE</target>
                        <target>ILLUSTRATION</target>
                    </synonyms>
                </fields>""";
        SuggesterConfigurationDTO suggesterConfigurationDTO = new DDISuggesterDeserializer().deserialize(xmlString);
        List<FieldSynonymDTO> synonyms = suggesterConfigurationDTO.getFields().getFirst().getSynonyms();
        assertEquals(2, synonyms.size());
        assertEquals("foo", synonyms.get(0).getSource());
        assertEquals(List.of("BAR"), synonyms.get(0).getTarget());
        assertEquals("example", synonyms.get(1).getSource());
        assertEquals(List.of("INSTANCE", "SAMPLE", "ILLUSTRATION"), synonyms.get(1).getTarget());
    }

    @Test
    void deserializeStopWords() {
        String xmlString = """
                <stopWords xmlns="http://xml.insee.fr/schema/applis/lunatic-h">word A</stopWords>
                <stopWords xmlns="http://xml.insee.fr/schema/applis/lunatic-h">word B</stopWords>""";
        SuggesterConfigurationDTO suggesterConfigurationDTO = new DDISuggesterDeserializer().deserialize(xmlString);
        assertEquals(List.of("word A", "word B"), suggesterConfigurationDTO.getStopWords());
    }

    @Test
    void deserializeOrder() {
        String xmlString = """
                <order xmlns="http://xml.insee.fr/schema/applis/lunatic-h">
                     <field>label</field>
                     <type>ascending</type>
                </order>""";
        SuggesterConfigurationDTO suggesterConfigurationDTO = new DDISuggesterDeserializer().deserialize(xmlString);
        assertEquals("label", suggesterConfigurationDTO.getOrder().getField());
        assertEquals("ascending", suggesterConfigurationDTO.getOrder().getType());
    }

    @Test
    void deserializeQueryParser() {
        //
        String xmlString = """
                <queryParser xmlns="http://xml.insee.fr/schema/applis/lunatic-h">
                     <type>tokenized</type>
                     <params>
                         <language>French</language>
                         <min>3</min>
                         <pattern>[\\w.]+</pattern>
                         <stemmer>false</stemmer>
                     </params>
                </queryParser>""";
        //
        SuggesterConfigurationDTO suggesterConfigurationDTO = new DDISuggesterDeserializer().deserialize(xmlString);
        //
        SuggesterQueryParserDTO suggesterQueryParserDTO = suggesterConfigurationDTO.getQueryParser();
        assertEquals("tokenized", suggesterQueryParserDTO.getType());
        QueryParserParamsDTO queryParserParamsDTO = suggesterQueryParserDTO.getParams();
        assertEquals("French", queryParserParamsDTO.getLanguage());
        assertEquals(3, queryParserParamsDTO.getMin());
        assertEquals("[\\w.]+", queryParserParamsDTO.getPattern());
        assertEquals(false, queryParserParamsDTO.getStemmer());
    }

}
