package fr.insee.eno.core.model.sequence;

import fr.insee.ddi.lifecycle33.datacollection.QuestionConstructType;
import fr.insee.ddi.lifecycle33.datacollection.SequenceType;
import fr.insee.ddi.lifecycle33.reusable.ReferenceType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.exceptions.business.IllegalDDIElementException;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.reference.DDIIndex;
import lombok.*;

import java.util.Optional;

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

    @DDI("T(fr.insee.eno.core.model.sequence.ItemReference).mapDDIReferenceId(" +
            "#this, #index)")
    private String id;

    @DDI("T(fr.insee.eno.core.model.sequence.ItemReference).convertDDITypeOfObject(" +
            "#this, #index)")
    private ItemType type;

    // NOTE: The following is way too much complex, this needs some refactor to make things a bit nicer.

    // NOTE 2:  We also might want to figure out a way to manage DDI constant values uniformly.
    // These values are hardcoded, but it's quite hard to get a cleaner solution which wouldn't be too tedious.
    // Yet, DDI is used as a standard that shouldn't change that often, so it's not a big deal.

    /**
     * DDI "control construct" elements have a list of references to other control construct elements.
     * In some cases, some intermediate elements (e.g. intermediate question reference, or a "roundabout" sequence)
     * are referenced, making the retrieval of the identifier of the concrete (e.g. actual sequence, subsequence, loop
     * or filter) element harder.
     * This method returns the identifier of the concrete element.
     * @param referenceType A control construct reference.
     * @param ddiIndex DDI index that can be used to jump to the referenced element.
     * @return The identifier of the concrete element referenced by the control construct.
     */
    public static String mapDDIReferenceId(ReferenceType referenceType, DDIIndex ddiIndex) {
        String typeOfObject = referenceType.getTypeOfObject().toString();
        // In the question case, there is an intermediate "question construct" object before the concrete question object.
        if ("QuestionConstruct".equals(typeOfObject)) {
            QuestionConstructType questionConstructType = (QuestionConstructType) ddiIndex.get(
                    referenceType.getIDArray(0).getStringValue());
            return questionConstructType.getQuestionReference().getIDArray(0).getStringValue();
        }
        // In the sequence case, the sequence can be an intermediate "roundabout" sequence before the concrete loop.
        if ("Sequence".equals(typeOfObject)) {
            SequenceType sequenceType = (SequenceType) ddiIndex.get(referenceType.getIDArray(0).getStringValue());
            if (isRoundaboutSequence(sequenceType))
                return getLoopReference(sequenceType);
        }
        // In regular cases, simply return the id.
        return referenceType.getIDArray(0).getStringValue();
    }

    private static boolean isRoundaboutSequence(SequenceType sequenceType) {
        return "roundabout".equals(sequenceType.getTypeOfSequenceArray(0).getStringValue());
    }

    /**
     * "Roundabout" sequences are intermediate sequence objects that contain information about the roundabout, but does
     * not correspond to an actual sequence. In that case, we want to jump to the loop that corresponds to the
     * roundabout.
     * This method returns the identifier of that loop.
     * @param sequenceType A "roundabout" sequence object.
     * @return The identifier of the loop the corresponds to the roundabout sequence given.
     * @throws IllegalDDIElementException if the sequence doesn't reference a loop object.
     */
    private static String getLoopReference(SequenceType sequenceType) {
        // Redundant assertion to make sure the method is called on a roundabout sequence.
        assert isRoundaboutSequence(sequenceType);
        // Find the loop reference within the roundabout sequence
        Optional<ReferenceType> loopReference = sequenceType.getControlConstructReferenceList().stream()
                .filter(controlConstructReference -> "Loop".equals(controlConstructReference.getTypeOfObject().toString()))
                .findAny();
        // Note: we could also verify that there is exactly one referenced loop, but this tedious enough.
        if (loopReference.isEmpty())
            throw new IllegalDDIElementException(String.format(
                    "DDI roundabout sequence '%s' doesn't reference any loop.",
                    sequenceType.getIDArray(0).getStringValue()));
        return loopReference.get().getIDArray(0).getStringValue();
    }

    public static ItemType convertDDITypeOfObject(ReferenceType referenceType, DDIIndex ddiIndex) {
        String typeOfObject = referenceType.getTypeOfObject().toString();
        return switch (typeOfObject) {
            case "Sequence" -> getSequenceTypeOfObject(referenceType, ddiIndex);
            case "QuestionConstruct" -> ItemType.QUESTION;
            case "ComputationItem" -> ItemType.CONTROL;
            case "IfThenElse" -> ItemType.FILTER;
            case "StatementItem" -> ItemType.DECLARATION;
            case "Loop" -> ItemType.LOOP;
            default -> throw new MappingException(
                    "Unexpected type of object '"+typeOfObject+"' found in DDI control construct reference.");
        };
    }

    private static ItemType getSequenceTypeOfObject(ReferenceType referenceType, DDIIndex ddiIndex) {
        String id = referenceType.getIDArray(0).getStringValue();
        SequenceType ddiSequence = (SequenceType) ddiIndex.get(id);
        String typeOfSequence = ddiSequence.getTypeOfSequenceArray(0).getStringValue();
        return switch (typeOfSequence) {
            case "module" -> ItemType.SEQUENCE;
            case "submodule" -> ItemType.SUBSEQUENCE;
            case "roundabout" -> ItemType.LOOP;
            default ->
                throw new MappingException(String.format(
                        "Unexpected type '%s' found in sequence '%s'.", typeOfSequence, id));
        };
    }

}
