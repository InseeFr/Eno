package fr.insee.eno.core.converter;

import datacollection33.QuestionItemDocument;
import datacollection33.QuestionItemType;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.question.TextQuestion;
import org.apache.xmlbeans.XmlException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DDIConverterTest {

    @Test
    void convertTextQuestion() throws XmlException, IOException {
        // (creating object with code is tedious, so let's use a string input and de-serialize it)
        String stringInput = """
                <d:QuestionItem xmlns:d="ddi:datacollection:3_3" xmlns:r="ddi:reusable:3_3">
                  <r:Agency>fr.insee</r:Agency>
                  <r:ID>lmyo3e0y</r:ID>
                  <r:Version>1</r:Version>
                  <d:QuestionItemName>
                     <r:String xml:lang="fr-FR">Q1</r:String>
                  </d:QuestionItemName>
                  <r:OutParameter isArray="false">
                     <r:Agency>fr.insee</r:Agency>
                     <r:ID>lmyo3e0y-QOP-lmynykd5</r:ID>
                     <r:Version>1</r:Version>
                     <r:ParameterName>
                        <r:String xml:lang="fr-FR">Q1</r:String>
                     </r:ParameterName>
                  </r:OutParameter>
                  <r:Binding>
                     <r:SourceParameterReference>
                        <r:Agency>fr.insee</r:Agency>
                        <r:ID>lmyo3e0y-RDOP-lmynykd5</r:ID>
                        <r:Version>1</r:Version>
                        <r:TypeOfObject>OutParameter</r:TypeOfObject>
                     </r:SourceParameterReference>
                     <r:TargetParameterReference>
                        <r:Agency>fr.insee</r:Agency>
                        <r:ID>lmyo3e0y-QOP-lmynykd5</r:ID>
                        <r:Version>1</r:Version>
                        <r:TypeOfObject>OutParameter</r:TypeOfObject>
                     </r:TargetParameterReference>
                  </r:Binding>
                  <d:QuestionText>
                     <d:LiteralText>
                        <d:Text xml:lang="fr-FR">"Unique question"</d:Text>
                     </d:LiteralText>
                  </d:QuestionText>
                  <d:TextDomain maxLength="249">
                     <r:OutParameter isArray="false">
                        <r:Agency>fr.insee</r:Agency>
                        <r:ID>lmyo3e0y-RDOP-lmynykd5</r:ID>
                        <r:Version>1</r:Version>
                        <r:TextRepresentation maxLength="249"/>
                     </r:OutParameter>
                  </d:TextDomain>
                </d:QuestionItem>
                """;
        QuestionItemType ddiQuestionItem = QuestionItemDocument.Factory.parse(
                new ByteArrayInputStream(stringInput.getBytes())).getQuestionItem();
        //
        EnoObject result = DDIConverter.instantiateFromDDIObject(ddiQuestionItem, null);
        //
        assertTrue(result instanceof TextQuestion);
    }

}
