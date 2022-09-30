package fr.insee.eno.core.mappers.ddi;

import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import group33.ResourcePackageType;
import instance33.DDIInstanceType;
import logicalproduct33.CodeListSchemeType;
import logicalproduct33.VariableSchemeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import reusable33.*;

import java.beans.PropertyDescriptor;

import static org.junit.jupiter.api.Assertions.*;

public class EnoQuestionnaireTest {

    // TODO: more unit tests (one per DDI annotation in the model classes)

    // Note: it may be pertinent to do one unit test class per model class

    // Note: it is also possible to read portions of DDI xml files to do unit testing of annotations

    private DDIInstanceType ddiInstanceType;

    // TODO: move this method somewhere and make it accessible for the whole package
    private void mapProperty(EnoQuestionnaire enoQuestionnaire, String propertyName) {
        DDIMapper ddiMapper = new DDIMapper();
        BeanWrapper beanWrapper = new BeanWrapperImpl(enoQuestionnaire);
        PropertyDescriptor propertyDescriptor = beanWrapper.getPropertyDescriptor(propertyName);
        EvaluationContext context = new StandardEvaluationContext();
        ddiMapper.propertyMapping(ddiInstanceType, enoQuestionnaire, beanWrapper, propertyDescriptor, context);
    }

    @BeforeEach
    public void newDDIInstanceType() {
        ddiInstanceType = DDIInstanceType.Factory.newInstance();
    }

    @Test
    public void mapId() {
        //
        String expectedId = "FOO-ID";
        // Given
        ddiInstanceType.getIDList().add(IDType.Factory.newInstance());
        ddiInstanceType.getIDList().get(0).setStringValue(expectedId);
        // When
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        mapProperty(enoQuestionnaire, "id");
        // Then
        assertEquals(expectedId, enoQuestionnaire.getId());
    }

    @Test
    public void mapQuestionnaireModel() {
        //
        String expectedModel = "FOO-MODEL";
        //
        ddiInstanceType.getResourcePackageList()
                .add(ResourcePackageType.Factory.newInstance());
        ddiInstanceType.getResourcePackageArray(0).getCodeListSchemeList()
                .add(CodeListSchemeType.Factory.newInstance());
        ddiInstanceType.getResourcePackageArray(0).getCodeListSchemeArray(0).getCodeListSchemeNameList()
                .add(NameType.Factory.newInstance());
        ddiInstanceType.getResourcePackageArray(0).getCodeListSchemeArray(0).getCodeListSchemeNameArray(0)
                .getStringList()
                .add(StringType.Factory.newInstance());
        ddiInstanceType.getResourcePackageArray(0).getCodeListSchemeArray(0).getCodeListSchemeNameArray(0)
                .getStringArray(0)
                .setStringValue(expectedModel);
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        mapProperty(enoQuestionnaire, "questionnaireModel");
        //
        assertEquals(expectedModel, enoQuestionnaire.getQuestionnaireModel());
    }

    @Test
    public void mapLabel() {
        //
        String expectedLabel = "Foo label";
        //
        ddiInstanceType.setCitation(CitationType.Factory.newInstance());
        ddiInstanceType.getCitation().setTitle(InternationalStringType.Factory.newInstance());
        ddiInstanceType.getCitation().getTitle().getStringList().add(StringType.Factory.newInstance());
        ddiInstanceType.getCitation().getTitle().getStringArray(0).setStringValue(expectedLabel);
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        mapProperty(enoQuestionnaire, "label");
        //
        assertEquals(expectedLabel, enoQuestionnaire.getLabel());
    }

    @Test
    public void mapVariables() {
        assertDoesNotThrow(() -> {
            //
            ddiInstanceType.getResourcePackageList()
                    .add(ResourcePackageType.Factory.newInstance());
            ddiInstanceType.getResourcePackageArray(0).getVariableSchemeList()
                    .add(VariableSchemeType.Factory.newInstance());
            //
            EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
            mapProperty(enoQuestionnaire, "variables");
        });
    }

    @Test
    public void mapVariableGroups() {
        assertDoesNotThrow(() -> {
            //
            ddiInstanceType.getResourcePackageList()
                    .add(ResourcePackageType.Factory.newInstance());
            ddiInstanceType.getResourcePackageArray(0).getVariableSchemeList()
                    .add(VariableSchemeType.Factory.newInstance());
            //
            EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
            mapProperty(enoQuestionnaire, "variableGroups");
        });
    }

}
