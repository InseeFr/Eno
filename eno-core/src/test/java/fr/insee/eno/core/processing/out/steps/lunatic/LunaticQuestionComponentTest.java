package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LunaticQuestionComponentTest {

    @Test
    void wrapQuestionnaireComponents() {
        //
        Questionnaire questionnaire = new Questionnaire();
        //
        Sequence sequence = new Sequence();
        sequence.setId("sequence-id");
        sequence.setComponentType(ComponentTypeEnum.SEQUENCE);
        questionnaire.getComponents().add(sequence);
        //
        InputNumber inputNumber = new InputNumber();
        inputNumber.setComponentType(ComponentTypeEnum.INPUT_NUMBER);
        inputNumber.setPage("1");
        inputNumber.setMin(0d);
        inputNumber.setMax(10d);
        inputNumber.setLabel(new LabelType());
        inputNumber.getLabel().setValue("\"Question label.\"");
        inputNumber.getLabel().setType(LabelTypeEnum.VTL_MD);
        inputNumber.setConditionFilter(new ConditionFilterType());
        inputNumber.getConditionFilter().setValue("FOO = 1");
        inputNumber.getConditionFilter().setType(LabelTypeEnum.VTL);
        inputNumber.getConditionFilter().getBindingDependencies().add("FOO");
        //
        DeclarationType beforeQuestionDeclaration = new DeclarationType();
        beforeQuestionDeclaration.setId("declaration-1-id");
        beforeQuestionDeclaration.setDeclarationType(DeclarationTypeEnum.STATEMENT);
        beforeQuestionDeclaration.setPosition(DeclarationPositionEnum.BEFORE_QUESTION_TEXT);
        beforeQuestionDeclaration.setLabel(new LabelType());
        beforeQuestionDeclaration.getLabel().setValue("\"Before question declaration.\"");
        beforeQuestionDeclaration.getLabel().setType(LabelTypeEnum.VTL_MD);
        inputNumber.getDeclarations().add(beforeQuestionDeclaration);
        //
        DeclarationType afterQuestionDeclaration = new DeclarationType();
        afterQuestionDeclaration.setId("declaration-2-id");
        afterQuestionDeclaration.setDeclarationType(DeclarationTypeEnum.HELP);
        afterQuestionDeclaration.setPosition(DeclarationPositionEnum.AFTER_QUESTION_TEXT);
        afterQuestionDeclaration.setLabel(new LabelType());
        afterQuestionDeclaration.getLabel().setValue("\"After question declaration.\"");
        afterQuestionDeclaration.getLabel().setType(LabelTypeEnum.VTL_MD);
        inputNumber.getDeclarations().add(afterQuestionDeclaration);
        //
        questionnaire.getComponents().add(inputNumber);

        //
        new LunaticQuestionComponent().apply(questionnaire);

        // Sequence component should not be changed
        assertEquals(sequence, questionnaire.getComponents().get(0));
        // Input number should be replaced by a Question component
        Question question = assertInstanceOf(Question.class, questionnaire.getComponents().get(1));
        assertEquals(ComponentTypeEnum.QUESTION, questionnaire.getComponents().get(1).getComponentType());
        assertEquals(1, question.getComponents().size());
        assertEquals(ComponentTypeEnum.INPUT_NUMBER, question.getComponents().getFirst().getComponentType());
        // Label and condition filter should be at the Question level
        assertEquals("\"Question label.\"", question.getLabel().getValue());
        assertEquals(LabelTypeEnum.VTL_MD, question.getLabel().getType());
        assertNull(question.getComponents().getFirst().getLabel());
        assertEquals("FOO = 1", question.getConditionFilter().getValue());
        assertNull(question.getComponents().getFirst().getConditionFilter());
        // Declarations too (plus "STATEMENT" should be changed to "HELP")
        assertEquals(2, question.getDeclarations().size());
        assertEquals(DeclarationTypeEnum.HELP, question.getDeclarations().get(0).getDeclarationType());
        assertEquals(DeclarationTypeEnum.HELP, question.getDeclarations().get(1).getDeclarationType());
        assertEquals(DeclarationPositionEnum.BEFORE_QUESTION_TEXT, question.getDeclarations().get(0).getPosition());
        assertEquals(DeclarationPositionEnum.AFTER_QUESTION_TEXT, question.getDeclarations().get(1).getPosition());
        assertEquals("\"Before question declaration.\"", question.getDeclarations().get(0).getLabel().getValue());
        assertEquals("\"After question declaration.\"", question.getDeclarations().get(1).getLabel().getValue());
        assertTrue(question.getComponents().getFirst().getDeclarations().isEmpty());
    }

    @Test
    void wrapLoopComponents() {
        //
        Questionnaire questionnaire = new Questionnaire();
        Loop loop = new Loop();
        loop.setComponentType(ComponentTypeEnum.LOOP);
        //
        Sequence sequence = new Sequence();
        sequence.setId("sequence-id");
        sequence.setComponentType(ComponentTypeEnum.SEQUENCE);
        loop.getComponents().add(sequence);
        //
        InputNumber inputNumber = new InputNumber();
        inputNumber.setComponentType(ComponentTypeEnum.INPUT_NUMBER);
        inputNumber.setPage("1");
        inputNumber.setMin(0d);
        inputNumber.setMax(10d);
        loop.getComponents().add(inputNumber);
        //
        questionnaire.getComponents().add(loop);

        //
        new LunaticQuestionComponent().apply(questionnaire);

        // Questionnaire first component should still be a loop
        assertEquals(ComponentTypeEnum.LOOP, questionnaire.getComponents().getFirst().getComponentType());
        // Sequence component should not be changed
        assertEquals(sequence, loop.getComponents().get(0));
        // Input number should be replaced by a Question component
        assertEquals(ComponentTypeEnum.QUESTION, loop.getComponents().get(1).getComponentType());
    }

    @Test
    void integrationTestPairwise() throws DDIParsingException {
        //
        EnoParameters parameters = EnoParameters.of(
                EnoParameters.Context.HOUSEHOLD, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        parameters.getLunaticParameters().setDsfr(true);
        Questionnaire lunaticQuestionnaire = DDIToLunatic.fromInputStream(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-pairwise.xml"))
                .transform(parameters);

        // (This questionnaire has a loop, then a sequence with a single question which is a pairwise question.)
        Question question = lunaticQuestionnaire.getComponents().stream()
                .filter(Question.class::isInstance).map(Question.class::cast)
                .findAny().orElse(null);
        assertNotNull(question);

        //
        assertEquals(1, question.getComponents().size());
        assertNull(question.getLabel());
        PairwiseLinks pairwiseLinks = assertInstanceOf(PairwiseLinks.class, question.getComponents().getFirst());
        assertNull(pairwiseLinks.getLabel());
        assertEquals("\"Pairwise link between \" || xAxis || \" and \" || yAxis",
                pairwiseLinks.getComponents().getFirst().getLabel().getValue());
        assertEquals(LabelTypeEnum.VTL_MD, pairwiseLinks.getComponents().getFirst().getLabel().getType());
    }

}
