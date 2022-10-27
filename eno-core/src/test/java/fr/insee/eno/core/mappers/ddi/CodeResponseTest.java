package fr.insee.eno.core.mappers.ddi;

import datacollection33.CodeDomainType;
import datacollection33.GridDimensionType;
import datacollection33.QuestionGridType;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.response.CodeResponse;
import fr.insee.eno.core.reference.DDIIndex;
import fr.insee.eno.core.utils.DDIUtils;
import logicalproduct33.CategoryType;
import logicalproduct33.CodeListType;
import logicalproduct33.CodeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import reusable33.ContentType;
import reusable33.LabelType;
import reusable33.ParameterType;
import reusable33.ReferenceType;
import reusable33.impl.ContentTypeImpl;

import java.beans.PropertyDescriptor;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CodeResponseTest {

    private ParameterType outParameter;

    @BeforeEach
    public void newParameterType() {
        outParameter = ParameterType.Factory.newInstance();
    }

    @Test
    public void mapLabel_oneElementInCodeList() {
        //
        String fooLabel = "Foo label";
        //
        String outParameterId = "op-id";
        String questionGridId = "qg-id";
        String codeListId = "cl-id";
        String category1Id = "c-1-id";
        //
        DDIUtils.setId(outParameter, outParameterId);
        //
        QuestionGridType questionGridType = QuestionGridType.Factory.newInstance();
        GridDimensionType gridDimensionType = GridDimensionType.Factory.newInstance();
        CodeDomainType codeDomainType = CodeDomainType.Factory.newInstance();
        ReferenceType codeListReferenceType = ReferenceType.Factory.newInstance();
        DDIUtils.setId(codeListReferenceType, codeListId);
        codeDomainType.setCodeListReference(codeListReferenceType);
        gridDimensionType.setCodeDomain(codeDomainType);
        questionGridType.getGridDimensionList().add(gridDimensionType);
        //
        CodeListType codeListType = CodeListType.Factory.newInstance();
        CodeType codeType1 = CodeType.Factory.newInstance();
        ReferenceType categoryReferenceType1 = ReferenceType.Factory.newInstance();
        DDIUtils.setId(categoryReferenceType1, category1Id);
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
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("index", ddiIndex);
        context.setVariable("listIndex", 0);
        //
        CodeResponse codeResponse = new CodeResponse();
        DDIMapper ddiMapper = new DDIMapper();
        BeanWrapper beanWrapper = new BeanWrapperImpl(codeResponse);
        PropertyDescriptor propertyDescriptor = beanWrapper.getPropertyDescriptor("label");
        //
        ddiMapper.propertyMapping(outParameter, codeResponse, beanWrapper, propertyDescriptor, context);

        //
        assertEquals(fooLabel, codeResponse.getLabel());
    }
}
