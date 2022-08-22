package fr.insee.eno.core.model;

import datacollection33.ComputationItemType;
import datacollection33.impl.ComputationItemTypeImpl;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.lunatic.model.flat.ControlCriticityEnum;
import fr.insee.lunatic.model.flat.ControlType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** Consistency check. */
@Getter
@Setter
public class Control extends EnoObject {

    public enum Criticality {INFO, WARN, ERROR}

    public static Criticality convertDDICriticality(String ddiCriticality) {
        return switch (ddiCriticality) {
            case "informational" -> Criticality.INFO;
            case "warning" -> Criticality.WARN;
            case "stumblingblock" -> Criticality.ERROR;
            default -> throw new RuntimeException(String.format("Unknown DDI criticality '%s'", ddiCriticality));
        };
    }

    public static ControlCriticityEnum convertCriticalityToLunatic(Criticality criticality) {
        return switch (criticality) {
            case INFO -> ControlCriticityEnum.INFO;
            case WARN -> ControlCriticityEnum.WARN;
            case ERROR -> ControlCriticityEnum.ERROR;
        };
    }

    @DDI(contextType = ComputationItemType.class, field = "getIDArray(0).getStringValue()")
    @Lunatic(contextType = ControlType.class, field = "setId(#param)")
    private String id;

    /** Control criticality. */
    @DDI(contextType = ComputationItemType.class,
            field = "T(fr.insee.eno.core.model.Control).convertDDICriticality(" +
                    "getTypeOfComputationItem().getStringValue())")
    @Lunatic(contextType = ControlType.class,
            field = "setCriticality(T(fr.insee.eno.core.model.Control).convertCriticalityToLunatic(#param))")
    private Criticality criticality;

    /** Expression that determines if the control is triggered or not. */
    @DDI(contextType = ComputationItemType.class,
            field = "getCommandCode().getCommandArray(0).getCommandContent()")
    @Lunatic(contextType = ControlType.class, field = "setControl(#param)")
    private String expression;

    /** DDI references in the expression. */
    @DDI(contextType = ComputationItemType.class,
            field = "getCommandCode().getCommandArray(0).getInParameterList()")
    private List<BindingReference> bindingReferences = new ArrayList<>();

    /** Message displayed if the control is triggered. */
    @DDI(contextType = ComputationItemType.class,
            field = "getDescription().getContentArray(0).getStringValue()") // TODO: (NOTE:) getConstructNameArray(0).getStringArray(0).getStringValue() has the same information
    @Lunatic(contextType = ControlType.class, field = "setErrorMessage(#param)")
    private String message;

    /* TODO: later on references in message will be authorized (should be analog as in declaration labels,
        i.e. no additional info required here, a processing that uses regex with the special character should work) */

}
