package fr.insee.eno.core.processing.out.steps.lunatic.pagination;

import fr.insee.lunatic.model.flat.*;

public class LunaticAddPageNumbersUtils {
    private LunaticAddPageNumbersUtils() {
        throw new IllegalArgumentException("Utility class");
    }


    public static CheckboxOne buildCheckboxOne(String id, String name, Sequence sequence, Subsequence subsequence) {
        CheckboxOne co = new CheckboxOne();
        co.setComponentType(ComponentTypeEnum.CHECKBOX_ONE);
        co.setId(id);
        co.setHierarchy(buildHierarchy(sequence, subsequence));
        co.setResponse(buildResponse(name));
        return co;
    }

    public static Textarea buildTextarea(String id, String name, Sequence sequence) {
        Textarea textarea = new Textarea();
        textarea.setComponentType(ComponentTypeEnum.TEXTAREA);
        textarea.setId(id);
        textarea.setHierarchy(buildHierarchy(sequence));
        textarea.setResponse(buildResponse(name));
        return textarea;
    }

    public static Input buildInput(String id, String name, Sequence sequence, Subsequence subsequence) {
        Input input = new Input();
        input.setComponentType(ComponentTypeEnum.INPUT);
        input.setId(id);
        input.setHierarchy(buildHierarchy(sequence, subsequence));
        input.setResponse(buildResponse(name));
        return input;
    }

    public static Sequence buildSequence(String id) {
        Sequence sequence = new Sequence();
        sequence.setId(id);
        sequence.setComponentType(ComponentTypeEnum.SEQUENCE);
        sequence.setHierarchy(buildHierarchy(sequence));
        return sequence;
    }

    public static Subsequence buildSubsequence(String id, Sequence sequence) {
        Subsequence subsequence = new Subsequence();
        subsequence.setId(id);
        subsequence.setComponentType(ComponentTypeEnum.SUBSEQUENCE);
        subsequence.setHierarchy(buildHierarchy(sequence, subsequence));
        return subsequence;
    }

    public static InputNumber buildNumber(String id, String name, Sequence sequence, Subsequence subsequence) {
        InputNumber number = new InputNumber();
        number.setComponentType(ComponentTypeEnum.INPUT_NUMBER);
        number.setId(id);
        number.setHierarchy(buildHierarchy(sequence, subsequence));
        number.setResponse(buildResponse(name));
        return number;
    }

    public static Loop buildEmptyLoop(String id, Sequence sequence) {
        Loop loop = new Loop();
        loop.setComponentType(ComponentTypeEnum.LOOP);
        loop.setId(id);
        loop.setHierarchy(buildHierarchy(sequence));
        return loop;
    }

    public static Hierarchy buildHierarchy(Sequence sequence) {
        SequenceDescription sequenceDescription = new SequenceDescription();
        sequenceDescription.setId(sequence.getId());
        Hierarchy hierarchy = new Hierarchy();
        hierarchy.setSequence(sequenceDescription);
        return hierarchy;
    }

    public static Hierarchy buildHierarchy(Sequence sequence, Subsequence subsequence) {
        Hierarchy hierarchy = buildHierarchy(sequence);

        if(subsequence == null) {
            return hierarchy;
        }

        SequenceDescription subsequenceDescription = new SequenceDescription();
        subsequenceDescription.setId(subsequence.getId());

        hierarchy.setSubSequence(subsequenceDescription);
        return hierarchy;
    }

    public static ResponseType buildResponse(String name) {
        ResponseType response = new ResponseType();
        response.setName(name);
        return response;
    }

    public static PairwiseLinks buildEmptyPairWiseLinks(String id, Sequence sequence, Subsequence subsequence) {
        PairwiseLinks pairwiseLinks = new PairwiseLinks();
        pairwiseLinks.setId(id);
        pairwiseLinks.setComponentType(ComponentTypeEnum.PAIRWISE_LINKS);
        pairwiseLinks.setHierarchy(buildHierarchy(sequence, subsequence));
        return pairwiseLinks;
    }
}
