package fr.insee.eno.core.model;

import fr.insee.eno.core.annotations.DDI;
import lombok.Getter;
import lombok.Setter;
import reusable33.ReferenceType;

@Getter
@Setter
public class SequenceItem extends EnoObject {

    public enum SequenceItemType {SUBSEQUENCE, QUESTION, LOOP, FILTER, CONTROL, DECLARATION}

    @DDI(contextType = ReferenceType.class,
            field = "getTypeOfObject().toString() == 'QuestionConstruct' ? " +
                    "#index.get(#this.getIDArray(0).getStringValue())" +
                    ".getQuestionReference().getIDArray(0).getStringValue() : " +
                    "getIDArray(0).getStringValue()")
    private String id;

    @DDI(contextType = ReferenceType.class,
            field = "T(fr.insee.eno.core.model.SequenceItem).convertDDITypeOfObject(" +
                    "#this.getTypeOfObject().toString())")
    private SequenceItemType type;

    public static SequenceItemType convertDDITypeOfObject(String typeOfObject) {
        return switch (typeOfObject) {
            case "Sequence" -> SequenceItemType.SUBSEQUENCE;
            case "QuestionConstruct" -> SequenceItemType.QUESTION;
            case "ComputationItem" -> SequenceItemType.CONTROL;
            case "IfThenElse" -> SequenceItemType.FILTER;
            case "StatementItem" -> SequenceItemType.DECLARATION;
            case "Loop" -> SequenceItemType.LOOP;
            default -> throw new RuntimeException("Unexpected type of object found in DDI control construct scheme.");
        };
    }

}
