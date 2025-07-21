package fr.insee.eno.core.model.sequence;

import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.exceptions.business.IllegalPoguesElementException;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.sequence.ItemReference.ItemType;
import fr.insee.eno.core.parameter.Format;
import fr.insee.pogues.model.ComponentType;
import fr.insee.pogues.model.QuestionType;
import fr.insee.pogues.model.SequenceType;
import lombok.*;

/**
 * Reference of an item that is part of a questionnaire's structure:
 * that is to say either sequence, subsequence or question.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Context(format = Format.POGUES, type = ComponentType.class)
public class StructureItemReference extends EnoObject {

    /** Represents a kind of component that is a part of questionnaire's structure.
     * Note: value names match ItemReferenceType ones since same concepts are the same. */
    public enum StructureItemType {SEQUENCE, SUBSEQUENCE, QUESTION}

    @Pogues("getId()")
    private String id;

    @Pogues("T(fr.insee.eno.core.model.sequence.StructureItemReference).convertPoguesSequenceType(#this)")
    private StructureItemType type;

    public static StructureItemReference from(ItemReference itemReference) {
        if (! hasValidType(itemReference))
            throw new IllegalArgumentException("Illegal item reference type to create a component reference.");
        return StructureItemReference.builder()
                .id(itemReference.getId())
                .type(StructureItemType.valueOf(itemReference.getType().name()))
                .build();
    }

    private static boolean hasValidType(ItemReference itemReference) {
        return ItemType.SEQUENCE.equals(itemReference.getType())
                || ItemType.SUBSEQUENCE.equals(itemReference.getType())
                || ItemType.QUESTION.equals(itemReference.getType());
    }

    public static StructureItemType convertPoguesSequenceType(ComponentType poguesComponent) {
        if (poguesComponent instanceof SequenceType poguesSequence)
            return switch (poguesSequence.getGenericName()) {
                case QUESTIONNAIRE -> throw new IllegalPoguesElementException(
                        "A questionnaire cannot be a child of another Pogues element");
                case MODULE -> StructureItemType.SEQUENCE;
                case SUBMODULE -> StructureItemType.SUBSEQUENCE;
                // This case doesn't exist in this step (questionnaire is dereferenced)
                case EXTERNAL_ELEMENT -> null;
            };
        if (poguesComponent instanceof QuestionType)
            return StructureItemType.QUESTION;
        throw new MappingException("Unexpected pogues component of type " + poguesComponent.getClass());
    }

}
