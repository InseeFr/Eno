package fr.insee.eno.core.model.sequence;

import datacollection33.SequenceType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.reference.DDIIndex;
import lombok.*;
import reusable33.ReferenceType;

/** Class designed to map DDI ControlConstructReference tags.
 * Note: The ControlConstructReference tag corresponds to the ReferenceType class.
 * The <Code>ItemReference</Code> objects are only use to hold information derived from DDI,
 * these are resolved into <Code>StructureItemReference</Code> objects in a DDI processing.
 * @see StructureItemReference
 * @see fr.insee.eno.core.processing.in.steps.ddi.DDIResolveSequencesStructure */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Context(format = Format.DDI, type = ReferenceType.class)
public class ItemReference extends EnoObject {

    /** Type of items that can be found in control construct references in DDI. */
    public enum ItemType {SEQUENCE, SUBSEQUENCE, QUESTION, LOOP, FILTER, CONTROL, DECLARATION}

    @DDI("getTypeOfObject().toString() == 'QuestionConstruct' ? " +
            "#index.get(#this.getIDArray(0).getStringValue())" +
            ".getQuestionReference().getIDArray(0).getStringValue() : " +
            "getIDArray(0).getStringValue()")
    private String id;

    @DDI("T(fr.insee.eno.core.model.sequence.ItemReference).convertDDITypeOfObject(" +
            "#this, #index)")
    private ItemType type;

    public static ItemType convertDDITypeOfObject(ReferenceType referenceType, DDIIndex ddiIndex) {
        String typeOfObject = referenceType.getTypeOfObject().toString();
        return switch (typeOfObject) {
            case "Sequence" -> sequenceCase(referenceType, ddiIndex);
            case "QuestionConstruct" -> ItemType.QUESTION;
            case "ComputationItem" -> ItemType.CONTROL;
            case "IfThenElse" -> ItemType.FILTER;
            case "StatementItem" -> ItemType.DECLARATION;
            case "Loop" -> ItemType.LOOP;
            default -> throw new MappingException(
                    "Unexpected type of object '"+typeOfObject+"' found in DDI control construct reference.");
        };
    }

    private static ItemType sequenceCase(ReferenceType referenceType, DDIIndex ddiIndex) {
        SequenceType ddiSequence = (SequenceType) ddiIndex.get(referenceType.getIDArray(0).getStringValue());
        String typeOfSequence = ddiSequence.getTypeOfSequenceArray(0).getStringValue();
        return switch (typeOfSequence) {
            // TODO: figure out a way to manage constant values uniformly
            case "module" -> ItemType.SEQUENCE;
            case "submodule" -> ItemType.SUBSEQUENCE;
            default -> throw new MappingException("Unexpected value: " + typeOfSequence);
        };
    }

}
