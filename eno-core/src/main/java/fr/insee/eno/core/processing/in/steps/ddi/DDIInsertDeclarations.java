package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.exceptions.business.IllegalDDIElementException;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.declaration.Declaration;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.model.navigation.Loop;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.model.sequence.AbstractSequence;
import fr.insee.eno.core.model.sequence.ItemReference;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.Subsequence;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.reference.EnoIndex;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DDIInsertDeclarations implements ProcessingStep<EnoQuestionnaire> {

    private final EnoIndex enoIndex;

    public DDIInsertDeclarations(EnoIndex enoIndex) {
        this.enoIndex = enoIndex;
    }

    /** Same idea as in the DDI insert controls processing, but for declarations.
     * (Declarations are placed before the object they belong to in the sequence items lists.) */
    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        for (Sequence sequence : enoQuestionnaire.getSequences()) {
            List<String> declarationIdStack = new ArrayList<>();
            insertDeclarationsFromSequence(sequence, declarationIdStack);
        }
    }

    private void insertDeclarationsFromSequence(AbstractSequence sequence, List<String> declarationIdStack) {
        for (ItemReference sequenceItem : sequence.getSequenceItems()) {
            insertDeclarations(sequenceItem, declarationIdStack);
        }
    }

    private void insertDeclarationsFromFilter(Filter filter, List<String> declarationIdStack) {
        for (ItemReference filterItem : filter.getFilterItems()) {
            insertDeclarations(filterItem, declarationIdStack);
        }
    }

    private void insertDeclarationsFromLoop(Loop loop, List<String> declarationIdStack) {
        for (ItemReference loopItem : loop.getLoopItems()) {
            insertDeclarations(loopItem, declarationIdStack);
        }
    }

    private void insertDeclarations(ItemReference itemReference, List<String> declarationIdStack) {
        switch (itemReference.getType()) {
            case SEQUENCE -> {
                log.error("Error while inserting DDI StatementItem declarations.");
                throw new MappingException(
                        "A DDI sequence cannot contain another sequence (id=" + itemReference.getId() + ").");
            }
            case SUBSEQUENCE -> {
                noDeclarationCheck(declarationIdStack);
                Subsequence subsequence = (Subsequence) enoIndex.get(itemReference.getId());
                insertDeclarationsFromSequence(subsequence, declarationIdStack);
            }
            case LOOP -> {
                noDeclarationCheck(declarationIdStack);
                Loop loop = (Loop) enoIndex.get(itemReference.getId());
                insertDeclarationsFromLoop(loop, declarationIdStack);
            }
            case FILTER -> {
                noDeclarationCheck(declarationIdStack);
                Filter filter = (Filter) enoIndex.get(itemReference.getId());
                insertDeclarationsFromFilter(filter, declarationIdStack);
            }
            case QUESTION -> {
                Question question = (Question) enoIndex.get(itemReference.getId());
                declarationIdStack.forEach(declarationId ->
                        question.getDeclarations().add(
                                (Declaration) enoIndex.get(declarationId)));
                declarationIdStack.clear();
            }
            case DECLARATION -> declarationIdStack.add(itemReference.getId());
            case CONTROL -> log.debug("Control are ignored while inserting declarations in questions.");
        }
    }

    private void noDeclarationCheck(List<String> declarationIdStack) {
        if (! declarationIdStack.isEmpty()) {
            throw new IllegalDDIElementException("A DDI StatementItem can only be used in a question.");
        }
    }

}
