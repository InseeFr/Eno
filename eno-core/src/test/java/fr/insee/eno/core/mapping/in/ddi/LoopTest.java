package fr.insee.eno.core.mapping.in.ddi;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.LinkedLoop;
import fr.insee.eno.core.model.navigation.StandaloneLoop;
import fr.insee.eno.core.parsers.DDIParser;
import fr.insee.eno.core.processing.in.steps.ddi.DDIResolveVariableReferencesInExpressions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoopTest {

    @Nested
    class SequenceLoopsTest {

        private static EnoQuestionnaire enoQuestionnaire;

        @BeforeAll
        static void mapQuestionnaire() throws DDIParsingException {
            // Given + when
            enoQuestionnaire = new EnoQuestionnaire();
            DDIMapper ddiMapper = new DDIMapper();
            ddiMapper.mapDDI(
                    DDIParser.parse(LoopTest.class.getClassLoader().getResourceAsStream(
                            "integration/ddi/ddi-loops-sequence.xml")),
                    enoQuestionnaire);
            new DDIResolveVariableReferencesInExpressions().apply(enoQuestionnaire);
            // Then
            // -> tests
        }

        @Test
        void loopCount() {
            assertEquals(4, enoQuestionnaire.getLoops().size());
        }

        @Test
        void standaloneLoops() {
            List<StandaloneLoop> standaloneLoops = enoQuestionnaire.getLoops().stream()
                    .filter(StandaloneLoop.class::isInstance).map(StandaloneLoop.class::cast).toList();
            //
            assertEquals(2, standaloneLoops.size());
            //
            standaloneLoops.forEach(standaloneLoop ->
                    assertEquals("\"Add\"", standaloneLoop.getAddButtonLabel().getValue()));
            //
            assertEquals("1", standaloneLoops.get(0).getMinIteration().getValue());
            assertEquals("3", standaloneLoops.get(0).getMaxIteration().getValue());
            assertEquals("nvl( MIN_OCC , 1)", standaloneLoops.get(1).getMinIteration().getValue());
            assertEquals("nvl( MAX_OCC , 1)", standaloneLoops.get(1).getMaxIteration().getValue());
        }

        @Test
        void linkedLoops() {
            List<LinkedLoop> linkedLoops = enoQuestionnaire.getLoops().stream()
                    .filter(LinkedLoop.class::isInstance).map(LinkedLoop.class::cast).toList();
            //
            assertEquals(2, linkedLoops.size());
            //
            assertEquals(enoQuestionnaire.getLoops().get(0).getId(), linkedLoops.get(0).getReference());
            assertEquals(enoQuestionnaire.getLoops().get(2).getId(), linkedLoops.get(1).getReference());
        }

    }

}
