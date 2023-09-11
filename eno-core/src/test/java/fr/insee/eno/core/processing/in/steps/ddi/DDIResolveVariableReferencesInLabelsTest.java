package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.label.Label;
import fr.insee.eno.core.model.question.SimpleMultipleChoiceQuestion;
import fr.insee.eno.core.model.question.SingleResponseQuestion;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.variable.CollectedVariable;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.parsers.DDIParser;
import fr.insee.eno.core.reference.EnoCatalog;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DDIResolveVariableReferencesInLabelsTest {

    @Test
    void resolveReference_sequence() {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        //
        Sequence sequence = new Sequence();
        enoQuestionnaire.getSequences().add(sequence);
        //
        Label label = new Label();
        label.setValue("\"Label with reference \" || ¤foo-reference¤");
        sequence.setLabel(label);
        //
        Variable variable = new CollectedVariable();
        variable.setName("FOO");
        variable.setReference("foo-reference");
        enoQuestionnaire.getVariables().add(variable);
        //
        EnoCatalog enoCatalog = new EnoCatalog(enoQuestionnaire);

        //
        DDIResolveVariableReferencesInLabels processing = new DDIResolveVariableReferencesInLabels(enoCatalog);
        processing.apply(enoQuestionnaire);

        //
        assertEquals("\"Label with reference \" || FOO", sequence.getLabel().getValue());
    }

    @Nested
    class IntegrationTestLabels {

        private static EnoQuestionnaire enoQuestionnaire;

        @BeforeAll
        static void mapQuestionnaire() throws DDIParsingException {
            // Given
            enoQuestionnaire = new EnoQuestionnaire();
            DDIMapper ddiMapper = new DDIMapper();
            ddiMapper.mapDDI(
                    DDIParser.parse(DDIResolveVariableReferencesInLabelsTest.class.getClassLoader()
                            .getResourceAsStream("integration/ddi/ddi-labels.xml")),
                    enoQuestionnaire);
            EnoCatalog enoCatalog = new EnoCatalog(enoQuestionnaire);
            new DDIInsertDeclarations().apply(enoQuestionnaire);
            new DDIInsertControls().apply(enoQuestionnaire);
            new DDIInsertCodeLists().apply(enoQuestionnaire);
            // When
            new DDIResolveVariableReferencesInLabels(enoCatalog).apply(enoQuestionnaire);
            // Then
            // -> tests
        }

        @Test
        void sequenceLabel() {
            assertEquals("\"Static sequence name\"",
                    enoQuestionnaire.getSequences().get(0).getLabel().getValue());
            assertEquals("\"Dynamic sequence name: \" || Q1",
                    enoQuestionnaire.getSequences().get(1).getLabel().getValue().trim());
        }

        @Test
        void subsequenceLabel() {
            assertEquals("\"Static subsequence name\"",
                    enoQuestionnaire.getSubsequences().get(0).getLabel().getValue());
            assertEquals("\"Dynamic subsequence name: \" || Q1",
                    enoQuestionnaire.getSubsequences().get(1).getLabel().getValue().trim());
        }

        @Test
        void declarationOnSequence() {
            assertEquals("\"Static declaration on sequence\"",
                    enoQuestionnaire.getSequences().get(0).getInstructions().get(0).getLabel().getValue());
            assertEquals("\"Dynamic declaration on sequence: \" || Q1",
                    enoQuestionnaire.getSequences().get(1).getInstructions().get(0).getLabel().getValue().trim());
        }

        @Test
        void declarationOnSubsequence() {
            assertEquals("\"Static declaration on subsequence\"",
                    enoQuestionnaire.getSubsequences().get(0).getInstructions().get(0).getLabel().getValue());
            assertEquals("\"Dynamic declaration on subsequence: \" || Q1",
                    enoQuestionnaire.getSubsequences().get(1).getInstructions().get(0).getLabel().getValue().trim());
        }

        /** Question label + declarations, instructions and controls within it
         * (so that we don't repeat the search of the questions).  */
        @Test
        void labelsWithinQuestion() {
            //
            Optional<SingleResponseQuestion> question1 = enoQuestionnaire.getSingleResponseQuestions().stream()
                    .filter(question -> "Q1".equals(question.getName())).findAny();
            assertTrue(question1.isPresent());
            assertEquals("\"Static question name\"",
                    question1.get().getLabel().getValue());
            assertEquals("\"Static declaration before the question.\"",
                    question1.get().getDeclarations().get(0).getLabel().getValue());
            assertEquals("\"Static declaration after the question.\"",
                    question1.get().getInstructions().get(0).getLabel().getValue());
            assertEquals("\"Static control message\"",
                    question1.get().getControls().get(0).getMessage().getValue());
            //
            Optional<SingleResponseQuestion> question2 = enoQuestionnaire.getSingleResponseQuestions().stream()
                    .filter(question -> "Q2".equals(question.getName())).findAny();
            assertTrue(question2.isPresent());
            assertEquals("\"Dynamic question name: \" || Q1",
                    question2.get().getLabel().getValue().trim());
            assertEquals("\"Dynamic declaration before the question: \" || Q1",
                    question2.get().getDeclarations().get(0).getLabel().getValue().trim());
            assertEquals("\"Dynamic declaration after the question: \" || Q1",
                    question2.get().getInstructions().get(0).getLabel().getValue().trim());
            assertEquals("\"Dynamic control message: \" || Q1",
                    question2.get().getControls().get(0).getMessage().getValue().trim());
        }

        @Test
        void codeList_codeItemsLabel() {
            //
            Optional<CodeList> staticCodeList = enoQuestionnaire.getCodeLists().stream()
                    .filter(codeList -> "STATIC_CODE_LIST".equals(codeList.getName())).findAny();
            assertTrue(staticCodeList.isPresent());
            assertEquals("\"Static code 1\"",
                    staticCodeList.get().getCodeItems().get(0).getLabel().getValue());
            assertEquals("\"Static code 2\"",
                    staticCodeList.get().getCodeItems().get(1).getLabel().getValue());
            //
            Optional<CodeList> dynamicCodeList = enoQuestionnaire.getCodeLists().stream()
                    .filter(codeList -> "DYNAMIC_CODE_LIST".equals(codeList.getName())).findAny();
            assertTrue(dynamicCodeList.isPresent());
            assertEquals("\"Dynamic code 1: \" || Q1",
                    dynamicCodeList.get().getCodeItems().get(0).getLabel().getValue().trim());
            assertEquals("\"Dynamic code 2: \" || Q1",
                    dynamicCodeList.get().getCodeItems().get(1).getLabel().getValue().trim());
        }

        /** Note this test will be redundant with the previous one if we refactor
         * multiple choice questions mapping using code lists. */
        @Test
        void multipleChoiceQuestion_codeResponsesLabel() {
            //
            SimpleMultipleChoiceQuestion mcq1 = (SimpleMultipleChoiceQuestion)
                    enoQuestionnaire.getMultipleResponseQuestions().get(0);
            assertEquals("\"Static code 1\"", mcq1.getCodeResponses().get(0).getLabel().getValue());
            assertEquals("\"Static code 2\"", mcq1.getCodeResponses().get(1).getLabel().getValue());
            //
            SimpleMultipleChoiceQuestion mcq2 = (SimpleMultipleChoiceQuestion)
                    enoQuestionnaire.getMultipleResponseQuestions().get(1);
            assertEquals("\"Dynamic code 1: \" || Q1",
                    mcq2.getCodeResponses().get(0).getLabel().getValue().trim());
            assertEquals("\"Dynamic code 2: \" || Q1",
                    mcq2.getCodeResponses().get(1).getLabel().getValue().trim());
        }

    }

}
