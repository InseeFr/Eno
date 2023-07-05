package fr.insee.eno.core.model.sequence;

import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.sequence.ItemReference.ItemType;
import lombok.*;

/**
 * <Code>ComponentReference</Code> objects are designed to be used in lists holding the questionnaire structure.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StructureItemReference extends EnoObject {

    /** Represents a kind of component that is a part of questionnaire's structure.
     * Note: value names match ItemReferenceType ones since same concepts are the same. */
    public enum StructureItemType {SEQUENCE, SUBSEQUENCE, QUESTION}

    private String id;
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

}
