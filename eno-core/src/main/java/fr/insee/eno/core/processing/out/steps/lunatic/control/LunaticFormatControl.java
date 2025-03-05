package fr.insee.eno.core.processing.out.steps.lunatic.control;

import fr.insee.lunatic.model.flat.*;

import java.util.List;

public interface LunaticFormatControl<T extends ComponentType> {

    default void insertFormatControls(T lunaticComponent) {
        // Format controls are inserted at index 0 (=> to be the firsts in the controls list)
        lunaticComponent.getControls().addAll(0, generateFormatControls(lunaticComponent));
    }

    List<ControlType> generateFormatControls(T lunaticComponent);

    /**
     * Creates a Lunatic format control object.
     * @param id Identifier of the control object.
     * @param expression VTL expression.
     * @param message Error message of the control.
     * @return Lunatic format control object.
     */
    static ControlType createFormatControl(String id, String expression, String message) {
        ControlType control = new ControlType();
        control.setTypeOfControl(ControlTypeEnum.FORMAT);
        control.setId(id);
        control.setCriticality(ControlCriticalityEnum.ERROR);

        LabelType controlLabel = new LabelType();
        controlLabel.setType(LabelTypeEnum.VTL);
        controlLabel.setValue(expression);
        control.setControl(controlLabel);

        LabelType controlErrorLabel = new LabelType();
        controlErrorLabel.setType(LabelTypeEnum.VTL_MD);
        controlErrorLabel.setValue(message);
        control.setErrorMessage(controlErrorLabel);
        return control;
    }

}
