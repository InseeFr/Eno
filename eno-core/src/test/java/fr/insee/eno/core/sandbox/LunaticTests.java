package fr.insee.eno.core.sandbox;

import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LunaticTests {

    @Test
    public void helloLunaticQuestionnaire() {
        // New Lunatic questionnaire
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        // Questionnaire level metadata
        lunaticQuestionnaire.setEnoCoreVersion("3.0.0-SNAPSHOT");
        lunaticQuestionnaire.setModele("FOO");
        lunaticQuestionnaire.setLabel("Hello questionnaire");
        lunaticQuestionnaire.setPagination("question");
        lunaticQuestionnaire.setGeneratingDate("No restriction on date format");
        // Variables list
        List<IVariableType> lunaticVariables = lunaticQuestionnaire.getVariables();
        // Add a variable
        IVariableType lunaticVariable = new VariableType();
        lunaticVariable.setName("FOO_VARIABLE");
        lunaticVariable.setComponentRef("azerty");
        lunaticVariables.add(lunaticVariable);
        // Components
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        // Sequence
        SequenceType sequence = new SequenceType();
        ComponentTypeEnum sequenceType = ComponentTypeEnum.valueOf("SEQUENCE");
        sequence.setComponentType(sequenceType);
        // Declaration
        DeclarationType declaration = new DeclarationType();
        DeclarationPositionEnum declarationPosition = DeclarationPositionEnum.valueOf("AFTER_QUESTION_TEXT");
        declaration.setPosition(declarationPosition);
    }

}
