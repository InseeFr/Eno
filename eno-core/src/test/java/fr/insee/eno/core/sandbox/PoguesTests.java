package fr.insee.eno.core.sandbox;

import fr.insee.pogues.model.*;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PoguesTests {

    /** Pogues questionnaire object overview. */
    @Test
    void helloPogues() {
        Questionnaire poguesQuestionnaire = new Questionnaire();
        // Top level metadata
        poguesQuestionnaire.setId("foo-questionnaire-id");
        poguesQuestionnaire.setName("FOO_QUESTIONNAIRE_NAME");
        poguesQuestionnaire.getLabel().add("Some questionnaire label.");
        poguesQuestionnaire.setAgency("Insee");
        poguesQuestionnaire.setFormulasLanguage(FormulasLanguageEnum.VTL);
        poguesQuestionnaire.setFlowLogic(FlowLogicEnum.FILTER);
        poguesQuestionnaire.setGenericName(GenericNameEnum.QUESTIONNAIRE);
        poguesQuestionnaire.getTargetMode().add(SurveyModeEnum.CAWI);
        poguesQuestionnaire.setFinal(false); // ??? what is this for?
        poguesQuestionnaire.setDepth(BigInteger.ONE);
        // Note: questionnaire object should have a depth (this property is for "child" objects)
        // Fields inherited from ComponentType (not sure if all of these are useful)
        poguesQuestionnaire.getControl().add(new ControlType()); // control
        poguesQuestionnaire.getDeclaration().add(new DeclarationType()); // declaration
        poguesQuestionnaire.getFlowControl().add(new FlowControlType()); // VTL filter
        poguesQuestionnaire.getGoTo().add(new GoToType()); // Redirection filter
        // Code lists
        CodeLists codeLists = new CodeLists();
        poguesQuestionnaire.setCodeLists(codeLists);
        // Component groups
        ComponentGroup componentGroup = new ComponentGroup();
        poguesQuestionnaire.getComponentGroup().add(componentGroup);
        componentGroup.setId("foo-component-group-id");
        componentGroup.setName("FOO_COMPONENT_GROUPS_NAME");
        componentGroup.getLabel().add("Some component group label.");
        componentGroup.getMemberReference().add("foo-component-group-member-reference");
        // Components
        QuestionType questionType = new QuestionType();
        SequenceType sequenceType = new SequenceType();
        componentGroup.getMember().add(questionType);
        componentGroup.getMember().add(sequenceType);
        // Iterations (loops)
        Questionnaire.Iterations iterations = new Questionnaire.Iterations();
        IterationType iterationType = new DynamicIterationType();
        iterations.getIteration().add(iterationType);
        // Data collections
        DataCollection dataCollection = new DataCollection();
        poguesQuestionnaire.getDataCollection().add(dataCollection);
        // Variables
        Questionnaire.Variables variables = new Questionnaire.Variables();
        VariableType collectedVariableType = new CollectedVariableType();
        VariableType calculatedVariableType = new CalculatedVariableType();
        VariableType externalVariableType = new ExternalVariableType();
        variables.getVariable().add(collectedVariableType);
        variables.getVariable().add(calculatedVariableType);
        variables.getVariable().add(externalVariableType);
        poguesQuestionnaire.setVariables(variables);
        // Questionnaire structure
        poguesQuestionnaire.getChild().add(new SequenceType());

        //
        assertNotNull(poguesQuestionnaire);
    }

}
