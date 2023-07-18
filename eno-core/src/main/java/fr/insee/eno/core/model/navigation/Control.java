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

    public static Criticality convertDDICriticality(String ddiCriticality) {
        return switch (ddiCriticality) {
            case "informational" -> Criticality.INFO;
            case "warning" -> Criticality.WARN;
            case "stumblingblock" -> Criticality.ERROR;
            default -> throw new MappingException(String.format("Unknown DDI criticality '%s'", ddiCriticality));
        };
    }

    public static ControlCriticityEnum convertCriticalityToLunatic(Criticality criticality) {
        return switch (criticality) {
            case INFO -> ControlCriticityEnum.INFO;
            case WARN -> ControlCriticityEnum.WARN;
            case ERROR -> ControlCriticityEnum.ERROR;
        };
    }

    public static ControlTypeOfControlEnum convertTypeOfControlToLunatic(TypeOfControl typeOfControl) {
        return switch (typeOfControl) {
            case FORMAT -> ControlTypeOfControlEnum.FORMAT;
            case CONSISTENCY -> ControlTypeOfControlEnum.CONSISTENCY;
        };
    }

    /** Control criticality. */
    @DDI(contextType = ComputationItemType.class,
            field = "T(fr.insee.eno.core.model.navigation.Control).convertDDICriticality(" +
                    "getTypeOfComputationItem().getStringValue())")
    @Lunatic(contextType = ControlType.class,
            field = "setCriticality(T(fr.insee.eno.core.model.navigation.Control).convertCriticalityToLunatic(#param))")
    private Criticality criticality;

    @DDI(contextType = ComputationItemType.class,
            field = "T(fr.insee.eno.core.model.navigation.Control.TypeOfControl).CONSISTENCY")
    @Lunatic(contextType = ControlType.class,
            field = "setTypeOfControl(T(fr.insee.eno.core.model.navigation.Control).convertTypeOfControlToLunatic(#param))")
    private TypeOfControl typeOfControl;

    /** Label typed in Pogues, unused in Lunatic. */
    @DDI(contextType = ComputationItemType.class,
            field = "getDescription().getContentArray(0).getStringValue()") // TODO: (NOTE:) getConstructNameArray(0).getStringArray(0).getStringValue() has the same information
    private String label;

    /** Expression that determines if the control is triggered or not. */
    @DDI(contextType = ComputationItemType.class,
            field = "getCommandCode().getCommandArray(0)")
    @Lunatic(contextType = ControlType.class, field = "setControl(#param)")
    private CalculatedExpression expression;

    /** Message displayed if the control is triggered. */
    @DDI(contextType = ComputationItemType.class,
            field = "#index.get(#this.getInterviewerInstructionReferenceArray(0).getIDArray(0).getStringValue())" +
                    ".getInstructionTextArray(0)")
    @Lunatic(contextType = ControlType.class, field = "setErrorMessage(#param)")
    private DynamicLabel message; //TODO: DDI mapping

    /* TODO: later on references in message will be authorized (should be analog as in declaration labels,
        i.e. no additional info required here, a processing that uses regex with the special character should work) */

}
