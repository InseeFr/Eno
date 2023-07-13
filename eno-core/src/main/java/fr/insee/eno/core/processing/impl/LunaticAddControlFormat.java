package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.Constant;
import fr.insee.eno.core.processing.OutProcessingInterface;
import fr.insee.lunatic.model.flat.*;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Processing adding format controls to components
 * Format controls are controls generated by the min/max/decimals attributes of the Datepicker/InputNumber components
 */
@AllArgsConstructor
public class LunaticAddControlFormat implements OutProcessingInterface<Questionnaire> {
    /**
     *
     * @param lunaticQuestionnaire lunatic questionnaire to be processed.
     */
    public void apply(Questionnaire lunaticQuestionnaire) {
        processComponents(lunaticQuestionnaire.getComponents());
    }

    private void processComponents(List<ComponentType> components) {
        components.parallelStream()
                .forEach(componentType -> {
                    if(componentType instanceof ComponentNestingType componentNesting) {
                        processComponents(componentNesting.getComponents());
                        return;
                    }

                    if(componentType instanceof InputNumber number) {
                        createFormatControlsForInputNumber(number);
                        return;
                    }

                    if(componentType instanceof Datepicker datepicker) {
                        createFormatControlsForDatepicker(datepicker);
                    }
                });
    }

    /**
     * Create controls for a input number component
     * @param number input number to process
     */
    private void createFormatControlsForInputNumber(InputNumber number) {
        String controlIdPrefix = number.getId() + "-format";
        Double min = number.getMin();
        Double max = number.getMax();
        int decimalsCount = number.getDecimals().intValue();
        String responseName = number.getResponse().getName();

        number.getControls().add(0, createDecimalsFormatControl(controlIdPrefix, responseName, decimalsCount));


        if(min != null && max != null) {
            String minValue = formatDoubleValue(min, decimalsCount);
            String maxValue = formatDoubleValue(max, decimalsCount);
            String controlExpression = String.format("not(not(isnull(%s)) and (%s>%s or %s<%s))", responseName, minValue, responseName, maxValue, responseName);
            String controlErrorMessage = String.format("\" La valeur doit être comprise entre %s et %s.\"", minValue, maxValue);
            number.getControls().add(0, createFormatControl(controlIdPrefix+"-borne-inf-sup", controlExpression, controlErrorMessage));
            return;
        }

        if(min == null && max != null) {
            String maxValue = formatDoubleValue(max, decimalsCount);
            String controlExpression = String.format("not(not(isnull(%s)) and %s<%s)", responseName, maxValue, responseName);
            String controlErrorMessage = String.format("\" La valeur doit être inférieure à %s.\"", maxValue);
            number.getControls().add(0, createFormatControl(controlIdPrefix+"-borne-sup", controlExpression, controlErrorMessage));
            return;
        }

        if(min != null && max == null) {
            String minValue = formatDoubleValue(min, decimalsCount);
            String controlExpression = String.format("not(not(isnull(%s)) and %s>%s)", responseName, minValue, responseName);
            String controlErrorMessage = String.format("\" La valeur doit être supérieure à %s.\"", minValue);
            number.getControls().add(0, createFormatControl(controlIdPrefix+"-borne-inf", controlExpression, controlErrorMessage));
        }
    }

    /**
     * Create controls for a date picker component
     * @param datepicker date picker to process
     */
    private void createFormatControlsForDatepicker(Datepicker datepicker) {
        String controlIdPrefix = datepicker.getId() + "-format-date";
        String minValue = datepicker.getMin();
        String maxValue = datepicker.getMax();
        String format = datepicker.getDateFormat();
        String responseName = datepicker.getResponse().getName();

        if(minValue != null && maxValue != null) {
            String controlExpression = String.format("not(not(isnull(%s)) and " +
                    "(cast(%s, date, \"%s\")<cast(\"%s\", date, \"%s\") or " +
                    "cast(%s, date, \"%s\")>cast(\"%s\", date, \"%s\")))",
                    responseName, responseName, format, minValue, format, responseName, format, maxValue, format);
            String controlErrorMessage = String.format("\"La date saisie doit être comprise entre %s et %s.\"", minValue, maxValue);
            datepicker.getControls().add(createFormatControl(controlIdPrefix+"-borne-inf-sup", controlExpression, controlErrorMessage));
        }

        if(minValue == null && maxValue != null) {
            String controlExpression = String.format("not(not(isnull(%s)) and (cast(%s, date, \"%s\")>cast(\"%s\", date, \"%s\")))",
                    responseName, responseName, format, maxValue, format);
            String controlErrorMessage = String.format("\"La date saisie doit être antérieure à à %s.\"", maxValue);
            datepicker.getControls().add(createFormatControl(controlIdPrefix+"-borne-sup", controlExpression, controlErrorMessage));
        }

        if(minValue != null && maxValue == null) {
            String controlExpression = String.format("not(not(isnull(%s)) and (cast(%s, date, \"%s\")<cast(\"%s\", date, \"%s\")))",
                    responseName, responseName, format, minValue, format);
            String controlErrorMessage = String.format("\"La date saisie doit être postérieure à %s.\"", minValue);
            datepicker.getControls().add(createFormatControl(controlIdPrefix+"-borne-inf", controlExpression, controlErrorMessage));
        }
    }

    /**
     *
     * @param controlId control id
     * @param responseName component response name
     * @param decimalsCount decimals count allowed after semicolon
     * @return control for a decimal count
     */
    private ControlType createDecimalsFormatControl(String controlId, String responseName, int decimalsCount) {
        String controlExpression = String.format("not(not(isnull(%s))  and round(%s,%d)<>%s)", responseName, responseName, decimalsCount, responseName);
        String controlErrorMessage = String.format("\"Le nombre doit comporter au maximum %d chiffre(s) après la virgule.\"", decimalsCount);
        return createFormatControl(controlId+"-decimal", controlExpression, controlErrorMessage);
    }

    /**
     *
     * @param id control id
     * @param controlExpression vtl expression for the control
     * @param controlErrorMessage error message for the control
     * @return a control format
     */
    private ControlType createFormatControl(String id, String controlExpression, String controlErrorMessage) {
        ControlType control = new ControlType();
        control.setTypeOfControl(ControlTypeOfControlEnum.FORMAT);
        control.setId(id);
        control.setCriticality(ControlCriticityEnum.ERROR);

        LabelType controlLabel = new LabelType();
        controlLabel.setType(Constant.LUNATIC_LABEL_VTL);
        controlLabel.setValue(controlExpression);
        control.setControl(controlLabel);

        LabelType controlErrorLabel = new LabelType();
        controlErrorLabel.setType(Constant.LUNATIC_LABEL_VTL_MD);
        controlErrorLabel.setValue(controlErrorMessage);
        control.setErrorMessage(controlErrorLabel);
        return control;
    }
    
    private String formatDoubleValue(Double value, int decimalCount) {
        return BigDecimal.valueOf(value).setScale(decimalCount, RoundingMode.CEILING).toPlainString();
    }
}
