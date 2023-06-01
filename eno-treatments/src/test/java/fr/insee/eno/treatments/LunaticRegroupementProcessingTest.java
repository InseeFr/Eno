package fr.insee.eno.treatments;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.insee.eno.treatments.dto.Regroupement;
import fr.insee.lunatic.conversion.JSONSerializer;
import fr.insee.lunatic.exception.SerializationException;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class LunaticRegroupementProcessingTest {
    private LunaticRegroupementProcessing processing;

    private Questionnaire questionnaire;

    @BeforeEach
    void init() {
        List<Regroupement> regroupementList = new ArrayList<>();
        regroupementList.add(new Regroupement(List.of("Q1", "Q2")));
        regroupementList.add(new Regroupement(List.of("Q5", "Q6")));

        processing = new LunaticRegroupementProcessing(regroupementList);

        questionnaire = new Questionnaire();

        List<ComponentType> components = new ArrayList<>();
        List<ComponentType> l6Components = new ArrayList<>();
        List<ComponentType> l7Components = new ArrayList<>();

        Sequence s1 = buildSequence("jfaz9kv9", "1");
        components.add(s1);

        InputNumber n2 = buildNumber("jfazk91m", "2", "Q1", s1, null);
        components.add(n2);

        InputNumber n3 = buildNumber("lhpz37kh", "3", "Q2", s1, null);
        components.add(n3);

        Sequence s4 = buildSequence("li1w5tqk", "4");
        components.add(s4);

        InputNumber n5 = buildNumber("li1w3tmf", "5", "NB", s4, null);
        components.add(n5);

        Loop l6 = buildEmptyLoop("li1wjxs2", "6", null, s4);

        // build loop 6 components
        Subsequence ss6 = buildSubsequence("li1wbv47", "6", "6", s4);
        l6Components.add(ss6);

        Input i6 = buildInput("li1wptdt", "6", "PRENOM", s4, ss6);
        l6Components.add(i6);

        l6.getComponents().addAll(l6Components);
        components.add(l6);

        Loop l7 = buildEmptyLoop("li1wsotd", "7", "2", s4);

        // build loop 7 components
        Subsequence ss71 = buildSubsequence("li1wfnbk", null, "7.1", s4);
        l7Components.add(ss71);

        CheckboxOne co71 = buildCheckboxOne("lhpyz9b0", "7.1", "Q5", s4, ss71);
        l7Components.add(co71);

        InputNumber n72 = buildNumber("lhpzan4t", "7.2", "Q6", s4, ss71);
        l7Components.add(n72);

        l7.getComponents().addAll(l7Components);
        components.add(l7);

        Sequence s8 = buildSequence("li1wjpqw", "8");
        components.add(s8);

        Sequence s9 = buildSequence("COMMENT-SEQ", "9");
        components.add(s9);

        Textarea t10 = buildTextarea("COMMENT-QUESTION", "10", "COMMENT_QE", s9);
        components.add(t10);

        questionnaire.getComponents().addAll(components);
        questionnaire.setMaxPage("10");


    }

    @Test
    void testQuestionnaire() throws SerializationException {
        JSONSerializer jsonSerializer = new JSONSerializer();
        System.out.println(jsonSerializer.serialize2(questionnaire));
        processing.apply(questionnaire);
        System.out.println(jsonSerializer.serialize2(questionnaire));
        assertNotNull(questionnaire);
    }

    private CheckboxOne buildCheckboxOne(String id, String page, String name, Sequence sequence, Subsequence subsequence) {
        CheckboxOne co = new CheckboxOne();
        co.setComponentType(ComponentTypeEnum.CHECKBOX_ONE);
        co.setId(id);
        co.setPage(page);
        co.setHierarchy(buildHierarchy(sequence, subsequence));
        co.setResponse(buildResponse(name));
        return co;
    }

    private Textarea buildTextarea(String id, String page, String name, Sequence sequence) {
        Textarea textarea = new Textarea();
        textarea.setComponentType(ComponentTypeEnum.TEXTAREA);
        textarea.setId(id);
        textarea.setPage(page);
        textarea.setHierarchy(buildHierarchy(sequence));
        textarea.setResponse(buildResponse(name));
        return textarea;
    }

    private Input buildInput(String id, String page, String name, Sequence sequence, Subsequence subsequence) {
        Input input = new Input();
        input.setComponentType(ComponentTypeEnum.INPUT);
        input.setId(id);
        input.setPage(page);
        input.setHierarchy(buildHierarchy(sequence, subsequence));
        input.setResponse(buildResponse(name));
        return input;
    }

    private Sequence buildSequence(String id, String page) {
        Sequence sequence = new Sequence();
        sequence.setId(id);
        sequence.setComponentType(ComponentTypeEnum.SEQUENCE);
        sequence.setPage(page);
        sequence.setHierarchy(buildHierarchy(sequence));
        return sequence;
    }

    private Subsequence buildSubsequence(String id, String page, String gotoPage, Sequence sequence) {
        Subsequence subsequence = new Subsequence();
        subsequence.setId(id);
        subsequence.setComponentType(ComponentTypeEnum.SUBSEQUENCE);
        subsequence.setPage(page);
        subsequence.setGoToPage(gotoPage);
        subsequence.setHierarchy(buildHierarchy(sequence, subsequence));
        return subsequence;
    }

    private InputNumber buildNumber(String id, String page, String name, Sequence sequence, Subsequence subsequence) {
        InputNumber number = new InputNumber();
        number.setComponentType(ComponentTypeEnum.INPUT_NUMBER);
        number.setId(id);
        number.setPage(page);
        number.setHierarchy(buildHierarchy(sequence, subsequence));
        number.setResponse(buildResponse(name));
        return number;
    }

    private Loop buildEmptyLoop(String id, String page, String maxPage, Sequence sequence) {
        Loop loop = new Loop();
        loop.setComponentType(ComponentTypeEnum.LOOP);
        loop.setId(id);
        loop.setPage(page);
        loop.setMaxPage(maxPage);
        loop.setHierarchy(buildHierarchy(sequence));
        return loop;
    }

    private Hierarchy buildHierarchy(Sequence sequence) {
        SequenceDescription sequenceDescription = new SequenceDescription();
        sequenceDescription.setId(sequence.getId());
        sequenceDescription.setPage(sequence.getPage());
        Hierarchy hierarchy = new Hierarchy();
        hierarchy.setSequence(sequenceDescription);
        return hierarchy;
    }

    private Hierarchy buildHierarchy(Sequence sequence, Subsequence subsequence) {
        Hierarchy hierarchy = buildHierarchy(sequence);

        if(subsequence == null) {
            return hierarchy;
        }

        SequenceDescription subsequenceDescription = new SequenceDescription();
        subsequenceDescription.setId(subsequence.getId());
        subsequenceDescription.setPage(subsequence.getGoToPage());

        hierarchy.setSubSequence(subsequenceDescription);
        return hierarchy;
    }

    private ResponseType buildResponse(String name) {
        ResponseType response = new ResponseType();
        response.setName(name);
        return response;
    }
}
