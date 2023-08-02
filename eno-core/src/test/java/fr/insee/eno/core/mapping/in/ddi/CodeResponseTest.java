package fr.insee.eno.core.mapping.in.ddi;

import datacollection33.CodeDomainType;
import datacollection33.GridDimensionType;
import datacollection33.QuestionGridType;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.response.CodeResponse;
import fr.insee.eno.core.reference.DDIIndex;
import fr.insee.eno.core.utils.DDITestUtils;
import logicalproduct33.CategoryType;
import logicalproduct33.CodeListType;
import logicalproduct33.CodeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reusable33.ContentType;
import reusable33.LabelType;
import reusable33.ParameterType;
import reusable33.ReferenceType;
import reusable33.impl.ContentTypeImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled("to be reworked")
class CodeResponseTest {

    private ParameterType outParameter;
    private CodeResponse codeResponse;

    @BeforeEach
    public void newParameterType() {
        outParameter = ParameterType.Factory.newInstance();
        codeResponse = new CodeResponse();
    }

    @Test
    public void mapLabel_oneElementInCodeList() {
        //
        String fooLabel = "Foo label";
        //
        String outParameterId = "op-id";
        //String questionGridId = "qg-id"; //(unnecessary)
        String codeListId = "cl-id";
        String category1Id = "c-1-id";
        //
        DDITestUtils.setId(outParameter, outParameterId);
        //
        QuestionGridType questionGridType = QuestionGridType.Factory.newInstance();
        GridDimensionType gridDimensionType = GridDimensionType.Factory.newInstance();
        CodeDomainType codeDomainType = CodeDomainType.Factory.newInstance();
        ReferenceType codeListReferenceType = ReferenceType.Factory.newInstance();
        DDITestUtils.setId(codeListReferenceType, codeListId);
        codeDomainType.setCodeListReference(codeListReferenceType);
        gridDimensionType.setCodeDomain(codeDomainType);
        questionGridType.getGridDimensionList().add(gridDimensionType);
        //
        CodeListType codeListType = CodeListType.Factory.newInstance();
        CodeType codeType1 = CodeType.Factory.newInstance();
        ReferenceType categoryReferenceType1 = ReferenceType.Factory.newInstance();
        DDITestUtils.setId(categoryReferenceType1, category1Id);
        codeType1.setCategoryReference(categoryReferenceType1);
        codeListType.getCodeList().add(codeType1);
        //
        CategoryType categoryType = CategoryType.Factory.newInstance();
        LabelType labelType = LabelType.Factory.newInstance();
        ContentTypeImpl contentType = (ContentTypeImpl) ContentType.Factory.newInstance();
        contentType.setStringValue(fooLabel);
        labelType.getContentList().add(contentType);
        categoryType.getLabelList().add(labelType);
        //
        DDIIndex ddiIndex = Mockito.mock(DDIIndex.class);
        Mockito.when(ddiIndex.getParent(outParameterId)).thenReturn(questionGridType);
        Mockito.when(ddiIndex.get(codeListId)).thenReturn(codeListType);
        Mockito.when(ddiIndex.get(category1Id)).thenReturn(categoryType);

        //
        DDIMapper ddiMapper = new DDIMapper();
        ddiMapper.mapDDIObject(outParameter, codeResponse);

        //
        assertEquals(fooLabel, codeResponse.getLabel().getValue());
    }
}
