package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.StandaloneLoop;
import fr.insee.eno.core.model.question.TextQuestion;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.model.sequence.StructureItemReference.StructureItemType;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LunaticLoopResolutionTest {

    private static final String SEQUENCE_ID = "sequence-id";
    private static final String QUESTION_ID = "question-id";
    private static final String LOOP_ID = "loop-id";

    private EnoQuestionnaire enoQuestionnaire;
    private Questionnaire lunaticQuestionnaire;

    @BeforeEach
    void createSimpleQuestionnaire() {
        // Given
        enoQuestionnaire = new EnoQuestionnaire();
        Sequence sequence = new Sequence();
        sequence.setId(SEQUENCE_ID);
        sequence.getSequenceStructure().add(
                StructureItemReference.builder().id(QUESTION_ID).type(StructureItemType.QUESTION).build());
        enoQuestionnaire.getSequences().add(sequence);
        TextQuestion textQuestion = new TextQuestion();
        textQuestion.setId(QUESTION_ID);
        enoQuestionnaire.getSingleResponseQuestions().add(textQuestion);
        //
        EnoIndex enoIndex = new EnoIndex();
        enoIndex.put(SEQUENCE_ID, sequence);
        enoIndex.put(QUESTION_ID, textQuestion);
        enoQuestionnaire.setIndex(enoIndex);
        //
        lunaticQuestionnaire = new Questionnaire();
        fr.insee.lunatic.model.flat.Sequence lunaticSequence = new fr.insee.lunatic.model.flat.Sequence();
        lunaticSequence.setId(SEQUENCE_ID);
        Input lunaticTextQuestion = new Input();
        lunaticTextQuestion.setId(QUESTION_ID);
        lunaticQuestionnaire.getComponents().add(lunaticSequence);
        lunaticQuestionnaire.getComponents().add(lunaticTextQuestion);
    }

    /** Simple questionnaire without any loop. */
    @Test
    void testLoopResolution_noLoop() {
        // When
        LunaticLoopResolution lunaticLoopResolution = new LunaticLoopResolution(enoQuestionnaire);
        lunaticLoopResolution.apply(lunaticQuestionnaire);
        // Then
        assertEquals(2, lunaticQuestionnaire.getComponents().size());
    }

    /** Questionnaire with single sequence with a loop. */
    @Test
    void testLoopResolution_simplestCase() {
        // Given: adding a standalone loop in Eno questionnaire
        StandaloneLoop standaloneLoop = new StandaloneLoop();
        standaloneLoop.setId(LOOP_ID);
        standaloneLoop.getLoopScope().add(
                StructureItemReference.builder().id(SEQUENCE_ID).type(StructureItemType.SEQUENCE).build());
        enoQuestionnaire.getLoops().add(standaloneLoop);
        enoQuestionnaire.getIndex().put(LOOP_ID, standaloneLoop);
        //
        fr.insee.lunatic.model.flat.Loop lunaticLoop = new Loop();
        lunaticLoop.setId(LOOP_ID);
        lunaticQuestionnaire.getComponents().add(lunaticLoop);

        // When
        LunaticLoopResolution lunaticLoopResolution = new LunaticLoopResolution(enoQuestionnaire);
        lunaticLoopResolution.apply(lunaticQuestionnaire);

        // Then
        assertEquals(1, lunaticQuestionnaire.getComponents().size());
        assertInstanceOf(Loop.class, lunaticQuestionnaire.getComponents().get(0));
        assertEquals(LOOP_ID, lunaticQuestionnaire.getComponents().get(0).getId());
        assertEquals(2, ((Loop) lunaticQuestionnaire.getComponents().get(0)).getComponents().size());
        assertEquals(SEQUENCE_ID, ((Loop) lunaticQuestionnaire.getComponents().get(0)).getComponents().get(0).getId());
        assertInstanceOf(Input.class, ((Loop) lunaticQuestionnaire.getComponents().get(0)).getComponents().get(1));
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class IntegrationTest1 {

        private final Questionnaire lunaticQuestionnaire = new Questionnaire();
        private List<Loop> lunaticLoops;

        @BeforeAll
        void mapLunaticQuestionnaire() throws DDIParsingException {
            // Given: a mapped and sorted Lunatic questionnaire
            EnoQuestionnaire enoQuestionnaire = DDIToEno.fromInputStream(LunaticLoopResolutionTest.class.getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-loops-sequence.xml"))
                    .transform(EnoParameters.of(Context.DEFAULT, ModeParameter.CAWI, Format.LUNATIC));
            LunaticMapper lunaticMapper = new LunaticMapper();
            lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
            new LunaticSortComponents(enoQuestionnaire).apply(lunaticQuestionnaire);
            // When: applying loop resolution
            new LunaticLoopResolution(enoQuestionnaire).apply(lunaticQuestionnaire);
            // Then
            lunaticLoops = lunaticQuestionnaire.getComponents().stream()
                    .filter(Loop.class::isInstance).map(Loop.class::cast).toList();
        }

        @Test
        void questionnaireStructure() {
            //
            assertEquals(9, lunaticQuestionnaire.getComponents().size());
            assertEquals(4, lunaticLoops.size());
            //
            assertEquals(ComponentTypeEnum.LOOP, lunaticQuestionnaire.getComponents().get(0).getComponentType());
            assertEquals(ComponentTypeEnum.LOOP, lunaticQuestionnaire.getComponents().get(1).getComponentType());
            assertEquals(ComponentTypeEnum.SEQUENCE, lunaticQuestionnaire.getComponents().get(2).getComponentType());
            assertEquals(ComponentTypeEnum.INPUT_NUMBER, lunaticQuestionnaire.getComponents().get(3).getComponentType());
            assertEquals(ComponentTypeEnum.INPUT_NUMBER, lunaticQuestionnaire.getComponents().get(4).getComponentType());
            assertEquals(ComponentTypeEnum.LOOP, lunaticQuestionnaire.getComponents().get(5).getComponentType());
            assertEquals(ComponentTypeEnum.LOOP, lunaticQuestionnaire.getComponents().get(6).getComponentType());
            assertEquals(ComponentTypeEnum.SEQUENCE, lunaticQuestionnaire.getComponents().get(7).getComponentType());
            assertEquals(ComponentTypeEnum.CHECKBOX_BOOLEAN, lunaticQuestionnaire.getComponents().get(8).getComponentType());
        }

        @Test
        void linkedLoopsIterations() {
            assertEquals("count(Q1A)", lunaticLoops.get(1).getIterations().getValue());
            assertEquals("count(Q3A)", lunaticLoops.get(3).getIterations().getValue());
            assertEquals(LabelTypeEnum.VTL, lunaticLoops.get(1).getIterations().getType());
            assertEquals(LabelTypeEnum.VTL, lunaticLoops.get(3).getIterations().getType());
        }

        @Test
        void loopDependencies() {
            // Main loops
            assertTrue(lunaticLoops.get(0).getLoopDependencies().isEmpty());
            assertThat(lunaticLoops.get(2).getLoopDependencies())
                    .containsExactlyInAnyOrderElementsOf(Set.of("MIN_OCC", "MAX_OCC"));
            // Linked loops
            assertEquals(List.of("Q1A"), lunaticLoops.get(1).getLoopDependencies());
            assertEquals(List.of("Q3A"), lunaticLoops.get(3).getLoopDependencies());
        }

        @Test
        void loopsConditionFilter() {
            lunaticLoops.forEach(loop ->
                    assertEquals("true", loop.getConditionFilter().getValue()));
        }

        @Test
        void loopsDepth() {
            lunaticLoops.forEach(loop ->
                    assertEquals(BigInteger.ONE, loop.getDepth()));
        }

        @Test
        void loopsStructure() {
            //
            assertEquals(2, lunaticLoops.get(0).getComponents().size());
            assertEquals(ComponentTypeEnum.SEQUENCE, lunaticLoops.get(0).getComponents().get(0).getComponentType());
            assertEquals(ComponentTypeEnum.INPUT, lunaticLoops.get(0).getComponents().get(1).getComponentType());
            //
            assertEquals(2, lunaticLoops.get(1).getComponents().size());
            assertEquals(ComponentTypeEnum.SEQUENCE, lunaticLoops.get(1).getComponents().get(0).getComponentType());
            assertEquals(ComponentTypeEnum.INPUT, lunaticLoops.get(1).getComponents().get(1).getComponentType());
            //
            assertEquals(2, lunaticLoops.get(2).getComponents().size());
            assertEquals(ComponentTypeEnum.SEQUENCE, lunaticLoops.get(2).getComponents().get(0).getComponentType());
            assertEquals(ComponentTypeEnum.INPUT, lunaticLoops.get(2).getComponents().get(1).getComponentType());
            //
            assertEquals(2, lunaticLoops.get(3).getComponents().size());
            assertEquals(ComponentTypeEnum.SEQUENCE, lunaticLoops.get(3).getComponents().get(0).getComponentType());
            assertEquals(ComponentTypeEnum.INPUT, lunaticLoops.get(3).getComponents().get(1).getComponentType());
        }

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class IntegrationTest2 {

        private final Questionnaire lunaticQuestionnaire = new Questionnaire();
        private List<Loop> lunaticLoops;

        @BeforeAll
        void mapLunaticQuestionnaire() throws DDIParsingException {
            // Given: a mapped and sorted Lunatic questionnaire
            EnoQuestionnaire enoQuestionnaire = DDIToEno.fromInputStream(
                    LunaticLoopResolutionTest.class.getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-loops-subsequence.xml"))
                    .transform(EnoParameters.of(Context.DEFAULT, ModeParameter.CAWI, Format.LUNATIC));
            LunaticMapper lunaticMapper = new LunaticMapper();
            lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
            new LunaticSortComponents(enoQuestionnaire).apply(lunaticQuestionnaire);
            // When: applying loop resolution
            new LunaticLoopResolution(enoQuestionnaire).apply(lunaticQuestionnaire);
            // Then
            lunaticLoops = lunaticQuestionnaire.getComponents().stream()
                    .filter(Loop.class::isInstance).map(Loop.class::cast).toList();
        }

        @Test
        void questionnaireStructure() {
            //
            assertEquals(10, lunaticQuestionnaire.getComponents().size());
            assertEquals(4, lunaticLoops.size());
            //
            assertEquals(ComponentTypeEnum.SEQUENCE, lunaticQuestionnaire.getComponents().get(0).getComponentType());
            assertEquals(ComponentTypeEnum.LOOP, lunaticQuestionnaire.getComponents().get(1).getComponentType());
            assertEquals(ComponentTypeEnum.LOOP, lunaticQuestionnaire.getComponents().get(2).getComponentType());
            assertEquals(ComponentTypeEnum.SEQUENCE, lunaticQuestionnaire.getComponents().get(3).getComponentType());
            assertEquals(ComponentTypeEnum.INPUT_NUMBER, lunaticQuestionnaire.getComponents().get(4).getComponentType());
            assertEquals(ComponentTypeEnum.INPUT_NUMBER, lunaticQuestionnaire.getComponents().get(5).getComponentType());
            assertEquals(ComponentTypeEnum.LOOP, lunaticQuestionnaire.getComponents().get(6).getComponentType());
            assertEquals(ComponentTypeEnum.LOOP, lunaticQuestionnaire.getComponents().get(7).getComponentType());
            assertEquals(ComponentTypeEnum.SEQUENCE, lunaticQuestionnaire.getComponents().get(8).getComponentType());
            assertEquals(ComponentTypeEnum.CHECKBOX_BOOLEAN, lunaticQuestionnaire.getComponents().get(9).getComponentType());
        }

        @Test
        void linkedLoopsIterations() {
            assertEquals("count(Q1A)", lunaticLoops.get(1).getIterations().getValue());
            assertEquals("count(Q2A)", lunaticLoops.get(3).getIterations().getValue());
            assertEquals(LabelTypeEnum.VTL, lunaticLoops.get(1).getIterations().getType());
            assertEquals(LabelTypeEnum.VTL, lunaticLoops.get(3).getIterations().getType());
        }

        @Test
        void loopDependencies() {
            // Main loops
            assertTrue(lunaticLoops.get(0).getLoopDependencies().isEmpty());
            assertThat(lunaticLoops.get(2).getLoopDependencies())
                    .containsExactlyInAnyOrderElementsOf(Set.of("MIN_OCC", "MAX_OCC"));
            // Linked loops
            assertEquals(List.of("Q1A"), lunaticLoops.get(1).getLoopDependencies());
            assertEquals(List.of("Q2A"), lunaticLoops.get(3).getLoopDependencies());
        }

        @Test
        void loopsStructure() {
            //
            assertEquals(2, lunaticLoops.get(0).getComponents().size());
            assertEquals(ComponentTypeEnum.SUBSEQUENCE, lunaticLoops.get(0).getComponents().get(0).getComponentType());
            assertEquals(ComponentTypeEnum.INPUT, lunaticLoops.get(0).getComponents().get(1).getComponentType());
            //
            assertEquals(2, lunaticLoops.get(1).getComponents().size());
            assertEquals(ComponentTypeEnum.SUBSEQUENCE, lunaticLoops.get(1).getComponents().get(0).getComponentType());
            assertEquals(ComponentTypeEnum.INPUT, lunaticLoops.get(1).getComponents().get(1).getComponentType());
            //
            assertEquals(2, lunaticLoops.get(2).getComponents().size());
            assertEquals(ComponentTypeEnum.SUBSEQUENCE, lunaticLoops.get(2).getComponents().get(0).getComponentType());
            assertEquals(ComponentTypeEnum.INPUT, lunaticLoops.get(2).getComponents().get(1).getComponentType());
            //
            assertEquals(2, lunaticLoops.get(3).getComponents().size());
            assertEquals(ComponentTypeEnum.SUBSEQUENCE, lunaticLoops.get(3).getComponents().get(0).getComponentType());
            assertEquals(ComponentTypeEnum.INPUT, lunaticLoops.get(3).getComponents().get(1).getComponentType());
        }

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class IntegrationTest3 {

        private final Questionnaire lunaticQuestionnaire = new Questionnaire();
        private List<Loop> lunaticLoops;

        @BeforeAll
        void mapLunaticQuestionnaire() throws DDIParsingException {
            // Given: a mapped and sorted Lunatic questionnaire
            EnoQuestionnaire enoQuestionnaire = DDIToEno.fromInputStream(
                    LunaticLoopResolutionTest.class.getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-loops-extended-sequence.xml"))
                    .transform(EnoParameters.of(Context.DEFAULT, ModeParameter.CAWI, Format.LUNATIC));
            LunaticMapper lunaticMapper = new LunaticMapper();
            lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
            new LunaticSortComponents(enoQuestionnaire).apply(lunaticQuestionnaire);
            // When: applying loop resolution
            new LunaticLoopResolution(enoQuestionnaire).apply(lunaticQuestionnaire);
            // Then
            lunaticLoops = lunaticQuestionnaire.getComponents().stream()
                    .filter(Loop.class::isInstance).map(Loop.class::cast).toList();
        }

        @Test
        void questionnaireStructure() {
            //
            assertEquals(4, lunaticQuestionnaire.getComponents().size());
            assertEquals(2, lunaticLoops.size());
            //
            assertEquals(ComponentTypeEnum.LOOP, lunaticQuestionnaire.getComponents().get(0).getComponentType());
            assertEquals(ComponentTypeEnum.LOOP, lunaticQuestionnaire.getComponents().get(1).getComponentType());
            assertEquals(ComponentTypeEnum.SEQUENCE, lunaticQuestionnaire.getComponents().get(2).getComponentType());
            assertEquals(ComponentTypeEnum.CHECKBOX_BOOLEAN, lunaticQuestionnaire.getComponents().get(3).getComponentType());
        }

        @Test
        void linkedLoopIterations() {
            assertEquals("count(Q1)", lunaticLoops.get(1).getIterations().getValue());
        }

        @Test
        void loopDependencies() {
            // Main loop
            assertThat(lunaticLoops.get(0).getLoopDependencies()).isEmpty();
            // Linked loop
            assertEquals(List.of("Q1"), lunaticLoops.get(1).getLoopDependencies());
            assertEquals(LabelTypeEnum.VTL, lunaticLoops.get(1).getIterations().getType());
        }

        @Test
        void loopsStructure() {
            //
            assertEquals(6, lunaticLoops.get(0).getComponents().size());
            assertEquals(ComponentTypeEnum.SEQUENCE, lunaticLoops.get(0).getComponents().get(0).getComponentType());
            assertEquals(ComponentTypeEnum.INPUT, lunaticLoops.get(0).getComponents().get(1).getComponentType());
            assertEquals(ComponentTypeEnum.SEQUENCE, lunaticLoops.get(0).getComponents().get(2).getComponentType());
            assertEquals(ComponentTypeEnum.INPUT, lunaticLoops.get(0).getComponents().get(3).getComponentType());
            assertEquals(ComponentTypeEnum.SEQUENCE, lunaticLoops.get(0).getComponents().get(4).getComponentType());
            assertEquals(ComponentTypeEnum.INPUT, lunaticLoops.get(0).getComponents().get(5).getComponentType());
            //
            assertEquals(6, lunaticLoops.get(1).getComponents().size());
            assertEquals(ComponentTypeEnum.SEQUENCE, lunaticLoops.get(1).getComponents().get(0).getComponentType());
            assertEquals(ComponentTypeEnum.INPUT, lunaticLoops.get(1).getComponents().get(1).getComponentType());
            assertEquals(ComponentTypeEnum.SEQUENCE, lunaticLoops.get(1).getComponents().get(2).getComponentType());
            assertEquals(ComponentTypeEnum.INPUT, lunaticLoops.get(1).getComponents().get(3).getComponentType());
            assertEquals(ComponentTypeEnum.SEQUENCE, lunaticLoops.get(1).getComponents().get(4).getComponentType());
            assertEquals(ComponentTypeEnum.INPUT, lunaticLoops.get(1).getComponents().get(5).getComponentType());
        }

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class IntegrationTest4 {

        private final Questionnaire lunaticQuestionnaire = new Questionnaire();
        private List<Loop> lunaticLoops;

        @BeforeAll
        void mapLunaticQuestionnaire() throws DDIParsingException {
            // Given: a mapped and sorted Lunatic questionnaire
            EnoQuestionnaire enoQuestionnaire = DDIToEno.fromInputStream(
                    LunaticLoopResolutionTest.class.getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-loops-extended-subsequence.xml"))
                    .transform(EnoParameters.of(Context.DEFAULT, ModeParameter.CAWI, Format.LUNATIC));
            LunaticMapper lunaticMapper = new LunaticMapper();
            lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
            new LunaticSortComponents(enoQuestionnaire).apply(lunaticQuestionnaire);
            // When: applying loop resolution
            new LunaticLoopResolution(enoQuestionnaire).apply(lunaticQuestionnaire);
            // Then
            lunaticLoops = lunaticQuestionnaire.getComponents().stream()
                    .filter(Loop.class::isInstance).map(Loop.class::cast).toList();
        }

        @Test
        void questionnaireStructure() {
            //
            assertEquals(6, lunaticQuestionnaire.getComponents().size());
            assertEquals(2, lunaticLoops.size());
            //
            assertEquals(ComponentTypeEnum.SEQUENCE, lunaticQuestionnaire.getComponents().get(0).getComponentType());
            assertEquals(ComponentTypeEnum.LOOP, lunaticQuestionnaire.getComponents().get(1).getComponentType());
            assertEquals(ComponentTypeEnum.SEQUENCE, lunaticQuestionnaire.getComponents().get(2).getComponentType());
            assertEquals(ComponentTypeEnum.LOOP, lunaticQuestionnaire.getComponents().get(3).getComponentType());
            assertEquals(ComponentTypeEnum.SEQUENCE, lunaticQuestionnaire.getComponents().get(4).getComponentType());
            assertEquals(ComponentTypeEnum.CHECKBOX_BOOLEAN, lunaticQuestionnaire.getComponents().get(5).getComponentType());
        }

        @Test
        void linkedLoopIterations() {
            assertEquals("count(Q11)", lunaticLoops.get(1).getIterations().getValue());
        }

        @Test
        void loopDependencies() {
            // Main loop
            assertThat(lunaticLoops.get(0).getLoopDependencies()).isEmpty();
            // Linked loop
            assertEquals(List.of("Q11"), lunaticLoops.get(1).getLoopDependencies());
            assertEquals(LabelTypeEnum.VTL, lunaticLoops.get(1).getIterations().getType());
        }

        @Test
        void loopsStructure() {
            //
            assertEquals(6, lunaticLoops.get(0).getComponents().size());
            assertEquals(ComponentTypeEnum.SUBSEQUENCE, lunaticLoops.get(0).getComponents().get(0).getComponentType());
            assertEquals(ComponentTypeEnum.INPUT, lunaticLoops.get(0).getComponents().get(1).getComponentType());
            assertEquals(ComponentTypeEnum.SUBSEQUENCE, lunaticLoops.get(0).getComponents().get(2).getComponentType());
            assertEquals(ComponentTypeEnum.INPUT, lunaticLoops.get(0).getComponents().get(3).getComponentType());
            assertEquals(ComponentTypeEnum.SUBSEQUENCE, lunaticLoops.get(0).getComponents().get(4).getComponentType());
            assertEquals(ComponentTypeEnum.INPUT, lunaticLoops.get(0).getComponents().get(5).getComponentType());
            //
            assertEquals(6, lunaticLoops.get(1).getComponents().size());
            assertEquals(ComponentTypeEnum.SUBSEQUENCE, lunaticLoops.get(1).getComponents().get(0).getComponentType());
            assertEquals(ComponentTypeEnum.INPUT, lunaticLoops.get(1).getComponents().get(1).getComponentType());
            assertEquals(ComponentTypeEnum.SUBSEQUENCE, lunaticLoops.get(1).getComponents().get(2).getComponentType());
            assertEquals(ComponentTypeEnum.INPUT, lunaticLoops.get(1).getComponents().get(3).getComponentType());
            assertEquals(ComponentTypeEnum.SUBSEQUENCE, lunaticLoops.get(1).getComponents().get(4).getComponentType());
            assertEquals(ComponentTypeEnum.INPUT, lunaticLoops.get(1).getComponents().get(5).getComponentType());
        }

    }

    @Nested
    class TestWithLargeQuestionnaire {

        @Test
        @DisplayName("Questionnaire 'l20g2ba7': loops are inserted and contain the right components")
        void largeCoverageQuestionnaire() throws DDIParsingException {
            // Given
            EnoQuestionnaire enoQuestionnaire = DDIToEno.fromInputStream(
                    this.getClass().getClassLoader().getResourceAsStream("functional/ddi/ddi-l20g2ba7.xml"))
                    .transform(EnoParameters.of(Context.DEFAULT, ModeParameter.CAWI, Format.LUNATIC));
            Questionnaire lunaticQuestionnaire = new Questionnaire();
            LunaticMapper lunaticMapper = new LunaticMapper();
            lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
            LunaticSortComponents lunaticSortComponents = new LunaticSortComponents(enoQuestionnaire);
            lunaticSortComponents.apply(lunaticQuestionnaire);

            // When
            LunaticLoopResolution lunaticLoopResolution = new LunaticLoopResolution(enoQuestionnaire);
            lunaticLoopResolution.apply(lunaticQuestionnaire);

            // Then
            List<Loop> loops = lunaticQuestionnaire.getComponents().stream()
                    .filter(componentType -> componentType instanceof Loop)
                    .map(Loop.class::cast)
                    .toList();
            assertEquals(2, loops.size());
            Optional<Loop> loop1 = loops.stream().filter(loop -> loop.getComponents().size() == 3).findAny();
            Optional<Loop> loop2 = loops.stream().filter(loop -> loop.getComponents().size() == 2).findAny();
            assertTrue(loop1.isPresent());
            assertTrue(loop2.isPresent());
            assertInstanceOf(Subsequence.class, loop1.get().getComponents().get(0));
            assertInstanceOf(Input.class, loop1.get().getComponents().get(1));
            assertInstanceOf(InputNumber.class, loop1.get().getComponents().get(2));
            assertInstanceOf(Subsequence.class, loop2.get().getComponents().get(0));
            assertInstanceOf(InputNumber.class, loop2.get().getComponents().get(1));
        }

    }

    @Nested
    class LoopWithExcept {

        @Test
        void componentsInLinkedLoopShouldHaveFilter() throws DDIParsingException {
            // Given
            EnoQuestionnaire enoQuestionnaire = DDIToEno.fromInputStream(
                    this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-loop-except.xml"))
                    .transform(EnoParameters.of(Context.DEFAULT, ModeParameter.CAWI, Format.LUNATIC));
            Questionnaire lunaticQuestionnaire = new Questionnaire();
            LunaticMapper lunaticMapper = new LunaticMapper();
            lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
            LunaticSortComponents lunaticSortComponents = new LunaticSortComponents(enoQuestionnaire);
            lunaticSortComponents.apply(lunaticQuestionnaire);

            // When
            LunaticLoopResolution lunaticLoopResolution = new LunaticLoopResolution(enoQuestionnaire);
            lunaticLoopResolution.apply(lunaticQuestionnaire);

            // Then
            assertEquals(ComponentTypeEnum.LOOP, lunaticQuestionnaire.getComponents().get(3).getComponentType());
            Loop lunaticLinkedLoop = (Loop) lunaticQuestionnaire.getComponents().get(3);
            assertEquals(ComponentTypeEnum.SEQUENCE, lunaticLinkedLoop.getComponents().get(0).getComponentType());
            assertEquals(ComponentTypeEnum.CHECKBOX_BOOLEAN, lunaticLinkedLoop.getComponents().get(1).getComponentType());
            //
            lunaticLinkedLoop.getComponents().forEach(component -> {
                assertEquals("(not(nvl(AGE, 0) < 18))", component.getConditionFilter().getValue());
                assertEquals(LabelTypeEnum.VTL, component.getConditionFilter().getType());
            });
        }

    }

    @Nested
    class LinkedLoopBasedOnDynamicTable {

        @Test
        void mapLunaticQuestionnaire() throws DDIParsingException {
            // Given: a mapped and sorted Lunatic questionnaire
            EnoQuestionnaire enoQuestionnaire = DDIToEno.fromInputStream(
                    LunaticLoopResolutionTest.class.getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-dynamic-table.xml"))
                    .transform(EnoParameters.of(Context.DEFAULT, ModeParameter.CAWI, Format.LUNATIC));
            Questionnaire lunaticQuestionnaire = new Questionnaire();
            LunaticMapper lunaticMapper = new LunaticMapper();
            lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
            new LunaticSortComponents(enoQuestionnaire).apply(lunaticQuestionnaire);

            // When: applying loop resolution
            new LunaticLoopResolution(enoQuestionnaire).apply(lunaticQuestionnaire);

            // Then
            List<Loop> lunaticLoops = lunaticQuestionnaire.getComponents().stream()
                    .filter(Loop.class::isInstance).map(Loop.class::cast).toList();
            assertEquals(1, lunaticLoops.size());
            Loop lunaticLinkedLoop = lunaticLoops.get(0);
            assertEquals(List.of("DYNAMIC_TABLE1", "DYNAMIC_TABLE2", "DYNAMIC_TABLE3"),
                    lunaticLinkedLoop.getLoopDependencies());
            assertEquals("count(DYNAMIC_TABLE1)", lunaticLinkedLoop.getIterations().getValue());
            assertEquals(LabelTypeEnum.VTL, lunaticLinkedLoop.getIterations().getType());
        }

    }

}
