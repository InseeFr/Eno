package fr.insee.eno.core.processing.out.steps.lunatic.pagination;

import fr.insee.lunatic.model.flat.*;

public class LunaticAddPageNumbersUtils {
    private LunaticAddPageNumbersUtils() {
        throw new IllegalArgumentException("Utility class");
    }


    public static CheckboxOne buildCheckboxOne(String id, String name) {
        CheckboxOne co = new CheckboxOne();
        co.setId(id);
        co.setResponse(buildResponse(name));
        return co;
    }

    public static Textarea buildTextarea(String id, String name) {
        Textarea textarea = new Textarea();
        textarea.setId(id);
        textarea.setResponse(buildResponse(name));
        return textarea;
    }

    public static Input buildInput(String id, String name) {
        Input input = new Input();
        input.setId(id);
        input.setResponse(buildResponse(name));
        return input;
    }

    public static Sequence buildSequence(String id) {
        Sequence sequence = new Sequence();
        sequence.setId(id);
        return sequence;
    }

    public static Subsequence buildSubsequence(String id) {
        Subsequence subsequence = new Subsequence();
        subsequence.setId(id);
        return subsequence;
    }

    public static InputNumber buildNumber(String id, String name) {
        InputNumber number = new InputNumber();
        number.setId(id);
        number.setResponse(buildResponse(name));
        return number;
    }

    public static Loop buildEmptyLoop(String id) {
        Loop loop = new Loop();
        loop.setId(id);
        return loop;
    }

    public static ResponseType buildResponse(String name) {
        ResponseType response = new ResponseType();
        response.setName(name);
        return response;
    }

    public static PairwiseLinks buildEmptyPairWiseLinks(String id) {
        PairwiseLinks pairwiseLinks = new PairwiseLinks();
        pairwiseLinks.setId(id);
        return pairwiseLinks;
    }
}
