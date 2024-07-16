package fr.insee.eno.core.mapping.in.ddi;

import fr.insee.ddi.lifecycle33.datacollection.ControlConstructSchemeType;
import fr.insee.ddi.lifecycle33.datacollection.QuestionSchemeType;
import fr.insee.ddi.lifecycle33.group.ResourcePackageType;
import fr.insee.ddi.lifecycle33.instance.DDIInstanceType;
import fr.insee.ddi.lifecycle33.logicalproduct.CodeListSchemeType;
import fr.insee.ddi.lifecycle33.logicalproduct.VariableGroupType;
import fr.insee.ddi.lifecycle33.logicalproduct.VariableSchemeType;
import fr.insee.ddi.lifecycle33.logicalproduct.VariableType;
import fr.insee.ddi.lifecycle33.reusable.*;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnoQuestionnaireTest {

    // TODO: more unit tests (one per DDI annotation in the model classes)

    // Note: it may be pertinent to do one unit test class per model class

    // Note: it is also possible to read portions of DDI xml files to do unit testing of annotations

    private DDIInstanceType ddiInstanceType;
    private EnoQuestionnaire enoQuestionnaire;

    private DDIMapper ddiMapper;

    @BeforeEach
    public void newDDIInstanceType() {
        //
        ddiInstanceType = DDIInstanceType.Factory.newInstance();
        ddiInstanceType.getIDList().add(IDType.Factory.newInstance());
        ddiInstanceType.getIDList().get(0).setStringValue("ddi-instance-id");
        //
        ResourcePackageType resourcePackageType = ResourcePackageType.Factory.newInstance();
        resourcePackageType.getIDList().add(IDType.Factory.newInstance());
        resourcePackageType.getIDArray(0).setStringValue("resource-package-id");
        //
        ControlConstructSchemeType controlConstructSchemeType = ControlConstructSchemeType.Factory.newInstance();
        controlConstructSchemeType.getIDList().add(IDType.Factory.newInstance());
        controlConstructSchemeType.getIDArray(0).setStringValue("control-construct-scheme-id");
        resourcePackageType.getControlConstructSchemeList().add(controlConstructSchemeType);
        //
        QuestionSchemeType questionSchemeType = QuestionSchemeType.Factory.newInstance();
        questionSchemeType.getIDList().add(IDType.Factory.newInstance());
        questionSchemeType.getIDArray(0).setStringValue("question-scheme-id");
        resourcePackageType.getQuestionSchemeList().add(questionSchemeType);
        //
        VariableSchemeType variableSchemeType = VariableSchemeType.Factory.newInstance();
        variableSchemeType.getIDList().add(IDType.Factory.newInstance());
        variableSchemeType.getIDArray(0).setStringValue("variable-scheme-id");
        resourcePackageType.getVariableSchemeList().add(variableSchemeType);
        //
        CodeListSchemeType codeListSchemeType = CodeListSchemeType.Factory.newInstance();
        codeListSchemeType.getIDList().add(IDType.Factory.newInstance());
        codeListSchemeType.getIDArray(0).setStringValue("code-list-id");
        //
        codeListSchemeType.getCodeListSchemeNameList().add(NameType.Factory.newInstance());
        codeListSchemeType.getCodeListSchemeNameArray(0).getStringList().add(StringType.Factory.newInstance());
        codeListSchemeType.getCodeListSchemeNameArray(0).getStringArray(0).setStringValue("CL-MODEL");
        //
        resourcePackageType.getCodeListSchemeList().add(codeListSchemeType);
        //
        ddiInstanceType.getResourcePackageList().add(resourcePackageType);
        //
        enoQuestionnaire = new EnoQuestionnaire();
        //
        ddiMapper = new DDIMapper();
    }

    @Test
    void mapId() {
        //
        ddiMapper.mapDDIObject(ddiInstanceType, enoQuestionnaire);
        //
        assertEquals("ddi-instance-id", enoQuestionnaire.getId());
    }

    @Test
    void mapQuestionnaireModel() {
        //
        ddiMapper.mapDDIObject(ddiInstanceType, enoQuestionnaire);
        //
        assertEquals("CL-MODEL", enoQuestionnaire.getQuestionnaireModel());
    }

    @Test
    void mapLabel() {
        //
        String expectedLabel = "Foo label";
        //
        ddiInstanceType.setCitation(CitationType.Factory.newInstance());
        ddiInstanceType.getCitation().setTitle(InternationalStringType.Factory.newInstance());
        ddiInstanceType.getCitation().getTitle().getStringList().add(StringType.Factory.newInstance());
        ddiInstanceType.getCitation().getTitle().getStringArray(0).setStringValue(expectedLabel);
        //
        ddiMapper.mapDDIObject(ddiInstanceType, enoQuestionnaire);
        //
        assertEquals(expectedLabel, enoQuestionnaire.getLabel().getValue());
    }

    @Test
    void mapVariables() {
        //
        VariableType ddiVariable = VariableType.Factory.newInstance();
        ddiVariable.getIDList().add(IDType.Factory.newInstance());
        ddiVariable.getIDArray(0).setStringValue("variable-id");
        ddiInstanceType.getResourcePackageArray(0).getVariableSchemeArray(0).getVariableList().add(ddiVariable);
        //
        ddiMapper.mapDDIObject(ddiInstanceType, enoQuestionnaire);
        //
        assertEquals(1, enoQuestionnaire.getVariables().size());
    }

    @Test
    void mapVariableGroups() {
        //
        VariableGroupType variableGroupType = VariableGroupType.Factory.newInstance();
        variableGroupType.getIDList().add(IDType.Factory.newInstance());
        variableGroupType.getIDArray(0).setStringValue("variable-group-id");
        CodeValueType type = CodeValueType.Factory.newInstance();
        type.setStringValue("Questionnaire");
        variableGroupType.setTypeOfVariableGroup(type);
        ddiInstanceType.getResourcePackageArray(0).getVariableSchemeArray(0).getVariableGroupList().add(variableGroupType);
        //
        ddiMapper.mapDDIObject(ddiInstanceType, enoQuestionnaire);
        //
        assertEquals(1, enoQuestionnaire.getVariableGroups().size());
    }

}
