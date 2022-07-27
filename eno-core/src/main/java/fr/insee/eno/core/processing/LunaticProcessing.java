package fr.insee.eno.core.processing;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.Sequence;
import fr.insee.lunatic.model.flat.*;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class LunaticProcessing {

    //TODO: refactor duplicate code

    /** Map to get Lunatic components by id.*/
    private final Map<String, ComponentType> lunaticComponentsMap = new HashMap<>();
    /** Map to get sequences from Eno questionnaire by id. */
    private final Map<String, Sequence> enoSequencesMap = new HashMap<>();
    /** Map to get subsequences from Eno questionnaire by id.*/
    private final Map<String, fr.insee.eno.core.model.Subsequence> enoSubsequencesMap = new HashMap<>();

    /**
     * TODO
     * @param lunaticQuestionnaire Lunatic questionnaire to be modified.
     * @param enoQuestionnaire Eno questionnaire that contains some required info.
     */
    public void applyProcessing(Questionnaire lunaticQuestionnaire, EnoQuestionnaire enoQuestionnaire) {
        //
        lunaticQuestionnaire.getComponents().forEach(component ->
                lunaticComponentsMap.put(component.getId(), component));
        //
        enoQuestionnaire.getSequences().forEach(sequence -> enoSequencesMap.put(sequence.getId(), sequence));
        enoQuestionnaire.getSubsequences().forEach(subsequence -> enoSubsequencesMap.put(subsequence.getId(), subsequence));
        //
        addGeneratingDate(lunaticQuestionnaire);
        addPageNumbers(lunaticQuestionnaire);
        addHierarchy(enoQuestionnaire);
        removeSubsequencePage();
        addCommentQuestion(lunaticQuestionnaire);
    }

    private void addGeneratingDate(Questionnaire lunaticQuestionnaire) {
        lunaticQuestionnaire.setGeneratingDate(
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(LocalDateTime.now()));
    }

    /**
     * Add page number in each component of the given Lunatic questionnaire.
     * @param lunaticQuestionnaire Lunatic questionnaire to be paginated.
     */
    private void addPageNumbers(Questionnaire lunaticQuestionnaire) {
        int pageNumber = 1;
        for (String sequenceId : enoSequencesMap.keySet()) {
            ComponentType lunaticSequence = lunaticComponentsMap.get(sequenceId);
            lunaticSequence.setPage(String.valueOf(pageNumber));
            pageNumber ++;
            for (String componentId : enoSequencesMap.get(sequenceId).getComponentReferences()) {
                ComponentType component = lunaticComponentsMap.get(componentId);
                component.setPage(String.valueOf(pageNumber));
                if (component instanceof Subsequence subsequence) {
                    subsequence.setGoToPage(String.valueOf(pageNumber));
                    if (! subsequence.getDeclarations().isEmpty()) {
                        pageNumber ++;
                    }
                    for (String componentId2 : enoSubsequencesMap.get(componentId).getComponentReferences()) {
                        ComponentType component2 = lunaticComponentsMap.get(componentId2);
                        component2.setPage(String.valueOf(pageNumber));
                        pageNumber ++;
                    }
                } else {
                    pageNumber ++;
                }
            }
        }
        lunaticQuestionnaire.setMaxPage(String.valueOf(pageNumber - 1));
    }

    /**
     * Fill the 'hierarchy' field in Lunatic components.
     */
    private void addHierarchy(EnoQuestionnaire enoQuestionnaire) {
        for (String sequenceId : enoQuestionnaire.getSequenceReferences()) {
            ComponentType lunaticSequence = lunaticComponentsMap.get(sequenceId);
            Hierarchy hierarchy = new Hierarchy();
            SequenceDescription sequenceDescription = createDescription(lunaticSequence);
            hierarchy.setSequence(sequenceDescription);
            lunaticSequence.setHierarchy(hierarchy);
            for (String componentId : enoSequencesMap.get(sequenceId).getComponentReferences()) {
                ComponentType component = lunaticComponentsMap.get(componentId);
                if (component instanceof Subsequence) {
                    Hierarchy hierarchy2 = new Hierarchy();
                    hierarchy2.setSequence(sequenceDescription);
                    SequenceDescription subsequenceDescription = createDescription(component);
                    hierarchy2.setSubSequence(subsequenceDescription);
                    component.setHierarchy(hierarchy2);
                    for (String componentId2 : enoSubsequencesMap.get(componentId).getComponentReferences()) {
                        ComponentType component2 = lunaticComponentsMap.get(componentId2);
                        component2.setHierarchy(hierarchy2);
                    }
                } else {
                    component.setHierarchy(hierarchy);
                }
            }
        }
    }

    private static SequenceDescription createDescription(ComponentType component) {
        assert component instanceof SequenceType || component instanceof Subsequence;
        SequenceDescription sequenceDescription = new SequenceDescription();
        sequenceDescription.setId(component.getId());
        sequenceDescription.setLabel(component.getLabel());
        sequenceDescription.setPage(component.getPage());
        return sequenceDescription;
    }

    /** Remove page number in subsequences that does not have any declaration. */
    private void removeSubsequencePage() {
        lunaticComponentsMap.values().forEach(componentType -> {
            if (componentType instanceof Subsequence subsequence) {
                if (subsequence.getDeclarations().isEmpty()) {
                    subsequence.setPage(null);
                }
            }
        });
    }

    private void addCommentQuestion(Questionnaire lunaticQuestionnaire) {
        //
        VariableType commentVariable = new VariableType();
        commentVariable.setVariableType(VariableTypeEnum.COLLECTED);
        commentVariable.setName("COMMENT_QE");
        //
        int pageNumber = Integer.parseInt(lunaticQuestionnaire.getMaxPage()) + 1;
        //
        SequenceType commentSequence = new SequenceType();
        commentSequence.setId("COMMENT-SEQ");
        commentSequence.setComponentType(ComponentTypeEnum.SEQUENCE);
        commentSequence.setPage(String.valueOf(pageNumber)); pageNumber ++;
        commentSequence.setLabel("Commentaire");
        commentSequence.setConditionFilter(new ConditionFilterType());
        commentSequence.getConditionFilter().setValue("true");
        Hierarchy commentSequenceHierarchy = new Hierarchy();
        commentSequenceHierarchy.setSequence(new SequenceDescription());
        commentSequenceHierarchy.getSequence().setId(commentSequence.getId());
        commentSequenceHierarchy.getSequence().setLabel(commentSequence.getLabel());
        commentSequenceHierarchy.getSequence().setPage(commentSequence.getPage());
        commentSequence.setHierarchy(commentSequenceHierarchy);
        //
        Textarea commentQuestion = new Textarea();
        commentQuestion.setId("COMMENT-QUESTION");
        commentQuestion.setMandatory(false);
        commentQuestion.setPage(String.valueOf(pageNumber)); pageNumber ++;
        commentQuestion.setMaxLength(BigInteger.valueOf(2000));
        commentQuestion.setLabel("Avez-vous des remarques concernant l'enquête ou des commentaires\u00a0?");
        commentQuestion.setConditionFilter(new ConditionFilterType());
        commentQuestion.getConditionFilter().setValue("true");
        commentQuestion.setHierarchy(commentSequenceHierarchy);
        commentQuestion.getBindingDependencies().add(commentVariable.getName());
        commentQuestion.setResponse(new ResponseType());
        commentQuestion.getResponse().setName(commentVariable.getName());
        //
        commentVariable.setComponentRef(commentQuestion.getId());
        //
        lunaticQuestionnaire.getVariables().add(commentVariable);
        lunaticQuestionnaire.getComponents().add(commentSequence);
        lunaticQuestionnaire.getComponents().add(commentQuestion);
        lunaticQuestionnaire.setMaxPage(String.valueOf(pageNumber - 1));
    }

}
