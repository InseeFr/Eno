package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.eno.core.serialize.DDIDeserializer;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DDIInsertDeclarationsTest {

    // Note: there is currently no testing for the case when a question (and its declarations) is in a loop

    @Test
    void functionalTest() throws DDIParsingException {
        // Given
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        DDIMapper ddiMapper = new DDIMapper();
        ddiMapper.mapDDI(
                DDIDeserializer.deserialize(this.getClass().getClassLoader().getResourceAsStream("functional/ddi/ddi-lqnje8yr.xml")),
                enoQuestionnaire);
        EnoIndex enoIndex = enoQuestionnaire.getIndex();

        // When
        new DDIInsertDeclarations(enoIndex).apply(enoQuestionnaire);

        // Then
        Map<String, Question> questionsMap = new HashMap<>();
        enoQuestionnaire.getSingleResponseQuestions().forEach(question ->
                questionsMap.put(question.getId(), question));
        // testing ALL statement items defined in this DDI
        assertEquals(Set.of("lee4vk6v-SI"), declarationIds(questionsMap.get("lee4wdch")));
        assertEquals(Set.of("lsa7p1th-SI"), declarationIds(questionsMap.get("lsa7m4oz")));
        assertEquals(Set.of("lsa86kcc-SI"), declarationIds(questionsMap.get("lsa816bs")));
        assertEquals(Set.of("lsa8233e-SI"), declarationIds(questionsMap.get("lsa84bkt")));
        assertEquals(Set.of("lsa7urjr-SI"), declarationIds(questionsMap.get("lsa7zrcd")));
        assertEquals(Set.of("lsa7ylt0-SI"), declarationIds(questionsMap.get("lsa7vu2q")));
        assertEquals(Set.of("lskhh124-SI"), declarationIds(questionsMap.get("lrrmagqp")));
        assertEquals(Set.of("ls1wbfpa-SI"), declarationIds(questionsMap.get("lfl0kslr")));
        assertEquals(Set.of("ls1wnfsb-SI"), declarationIds(questionsMap.get("lfl0hxzb")));
        assertEquals(Set.of("ls1wephk-SI"), declarationIds(questionsMap.get("lfl0ijc3")));
        assertEquals(Set.of("ls1wb5ne-SI"), declarationIds(questionsMap.get("lfl0tmp0")));
        assertEquals(Set.of("ls1whjeh-SI"), declarationIds(questionsMap.get("lfl0u4in")));
        assertEquals(Set.of("ls1wmqdq-SI"), declarationIds(questionsMap.get("lfl0pgo9")));
        assertEquals(Set.of("ls1wnltn-SI"), declarationIds(questionsMap.get("lfl0uv7t")));
        assertEquals(Set.of("ls1wrziw-SI"), declarationIds(questionsMap.get("lfmc62sn")));
        assertEquals(Set.of("ls1wlv4t-SI"), declarationIds(questionsMap.get("lfmbwjt9")));
    }

    private static Set<String> declarationIds(Question question) {
        return question.getDeclarations().stream().map(EnoIdentifiableObject::getId).collect(Collectors.toSet());
    }

}
