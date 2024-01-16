package fr.insee.eno.core.model.navigation;

import datacollection33.ComputationItemType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.EnoObjectWithExpression;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.label.DynamicLabel;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.ControlCriticityEnum;
import fr.insee.lunatic.model.flat.ControlType;
import fr.insee.lunatic.model.flat.ControlTypeOfControlEnum;
import lombok.Getter;
import lombok.Setter;

/** Consistency check. */
@Getter
@Setter
@Context(format = Format.DDI, type = ComputationItemType.class)
@Context(format = Format.LUNATIC, type = ControlType.class)
public class Control extends EnoIdentifiableObject implements EnoObjectWithExpression {

    public enum Criticality {INFO, WARN, ERROR}

    public enum TypeOfControl { CONSISTENCY, FORMAT }

    // TODO: controls "criticality" temporary set to INFO

    public static Criticality convertDDICriticality(String ddiCriticality) {
        return switch (ddiCriticality) {
            case "informational" -> Criticality.INFO;
            case "warning" -> Criticality.INFO; // WARN
            case "stumblingblock" -> Criticality.INFO; // ERROR
            default -> throw new MappingException(String.format("Unknown DDI criticality '%s'", ddiCriticality));
        };
    }

    public static ControlCriticityEnum convertCriticalityToLunatic(Criticality criticality) {
        return switch (criticality) {
            case INFO -> ControlCriticityEnum.INFO;
            case WARN -> ControlCriticityEnum.INFO; // WARN
            case ERROR -> ControlCriticityEnum.INFO; // ERROR
        };
    }

    public static ControlTypeOfControlEnum convertTypeOfControlToLunatic(TypeOfControl typeOfControl) {
        return switch (typeOfControl) {
            case FORMAT -> ControlTypeOfControlEnum.FORMAT;
            case CONSISTENCY -> ControlTypeOfControlEnum.CONSISTENCY;
        };
    }

    /** Control criticality. */
    @DDI("T(fr.insee.eno.core.model.navigation.Control).convertDDICriticality(" +
            "getTypeOfComputationItem().getStringValue())")
    @Lunatic("setCriticality(T(fr.insee.eno.core.model.navigation.Control).convertCriticalityToLunatic(#param))")
    private Criticality criticality;

    @DDI("T(fr.insee.eno.core.model.navigation.Control.TypeOfControl).CONSISTENCY")
    @Lunatic("setTypeOfControl(T(fr.insee.eno.core.model.navigation.Control).convertTypeOfControlToLunatic(#param))")
    private TypeOfControl typeOfControl;

    /** Label typed in Pogues, unused in Lunatic. */
    @DDI("getDescription().getContentArray(0).getStringValue()") // NOTE: getConstructNameArray(0).getStringArray(0).getStringValue() has the same information
    private String label;

    /** Expression that determines if the control is triggered or not. */
    @DDI("getCommandCode().getCommandArray(0)")
    @Lunatic("setControl(#param)")
    private CalculatedExpression expression;

    /** Message displayed if the control is triggered. */
    @DDI("#index.get(#this.getInterviewerInstructionReferenceArray(0).getIDArray(0).getStringValue())" +
            ".getInstructionTextArray(0)")
    @Lunatic("setErrorMessage(#param)")
    private DynamicLabel message;

}
