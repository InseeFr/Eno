package fr.insee.eno.core.sandbox;

import fr.insee.lunatic.conversion.JsonDeserializer;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LunaticTests {

    @Test
    void componentType() {
        InputNumber inputNumber = new InputNumber();
        inputNumber.setComponentType(ComponentTypeEnum.TEXTAREA);
        // This enum shouldn't exist, Lunatic serializer should automatically write this value
        // (nothing prevents you from writing the wrong component type...)
        assertNotEquals(ComponentTypeEnum.INPUT_NUMBER, inputNumber.getComponentType());
    }

    @Test
    void helloLunaticQuestionnaire() {
        // New Lunatic questionnaire
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        // Questionnaire level metadata
        lunaticQuestionnaire.setEnoCoreVersion("3.0.0-SNAPSHOT");
        lunaticQuestionnaire.setModele("FOO");
        lunaticQuestionnaire.setPagination("question");
        lunaticQuestionnaire.setGeneratingDate("No restriction on date format");
        // NEW: complex object for labels
        LabelType lunaticLabel = new LabelType();
        lunaticLabel.setValue("Hello questionnaire");
        lunaticLabel.setType(LabelTypeEnum.VTL_MD);
        lunaticQuestionnaire.setLabel(lunaticLabel);
        // Variables list
        List<IVariableType> lunaticVariables = lunaticQuestionnaire.getVariables();
        // Add a variable
        IVariableType lunaticVariable = new VariableType();
        lunaticVariable.setName("FOO_VARIABLE");
        lunaticVariable.setComponentRef("azerty");
        lunaticVariables.add(lunaticVariable);
        // Sequence
        Sequence sequence = new Sequence();
        ComponentTypeEnum sequenceType = ComponentTypeEnum.valueOf("SEQUENCE");
        sequence.setComponentType(sequenceType);
        // Declaration
        DeclarationType declaration = new DeclarationType();
        DeclarationPositionEnum declarationPosition = DeclarationPositionEnum.valueOf("AFTER_QUESTION_TEXT");
        declaration.setPosition(declarationPosition);
        // Components
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        components.add(sequence);

        //
        assertNotNull(lunaticQuestionnaire);
    }

    @Test
    void lunaticDeserializer_doesNotWork() {
        JsonDeserializer jsonDeserializer = new JsonDeserializer();
        assertThrows(Exception.class, () -> jsonDeserializer.deserialize(this.getClass().getClassLoader()
                        .getResourceAsStream("functional/lunatic/lunatic-l20g2ba7.json")));
    }

}
