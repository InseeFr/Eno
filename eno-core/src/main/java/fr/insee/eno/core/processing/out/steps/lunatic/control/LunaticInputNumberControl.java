package fr.insee.eno.core.processing.out.steps.lunatic.control;

import fr.insee.lunatic.model.flat.ControlType;
import fr.insee.lunatic.model.flat.InputNumber;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class LunaticInputNumberControl implements LunaticFormatControl<InputNumber> {

    /**
     * Create controls for an input number component
     * @param inputNumber input number to process
     */
    @Override
    public List<ControlType> generateFormatControls(InputNumber inputNumber) {
        return getFormatControlsFromInputNumberAttributes(
                inputNumber.getId(),
                inputNumber.getMin(),
                inputNumber.getMax(),
                inputNumber.getDecimals().intValue(),
                inputNumber.getResponse().getName());
    }

    /**
     * Create controls from input number attributes
     * @param id input number id
     * @param min min value
     * @param max max value
     * @param decimalsCount number of decimals allowed
     * @param responseName input number response attribute
     */
    static List<ControlType> getFormatControlsFromInputNumberAttributes(String id, Double min, Double max, int decimalsCount, String responseName) {
        String controlIdPrefix = id + "-format";
        List<ControlType> controls = new ArrayList<>();

        if(min != null && max != null) {
            String minValue = formatDoubleValue(min, decimalsCount);
            String maxValue = formatDoubleValue(max, decimalsCount);
            String controlExpression = String.format("not(not(isnull(%s)) and (%s>%s or %s<%s))", responseName, minValue, responseName, maxValue, responseName);
            String controlErrorMessage = String.format("\" La valeur doit être comprise entre %s et %s\"", minValue, maxValue);
            controls.addFirst(LunaticFormatControl.createFormatControl(
                    controlIdPrefix+"-borne-inf-sup", controlExpression, controlErrorMessage));
        }

        if(min == null && max != null) {
            String maxValue = formatDoubleValue(max, decimalsCount);
            String controlExpression = String.format("not(not(isnull(%s)) and %s<%s)", responseName, maxValue, responseName);
            String controlErrorMessage = String.format("\" La valeur doit être inférieure à %s\"", maxValue);
            controls.addFirst(LunaticFormatControl.createFormatControl(
                    controlIdPrefix+"-borne-sup", controlExpression, controlErrorMessage));
        }

        if(min != null && max == null) {
            String minValue = formatDoubleValue(min, decimalsCount);
            String controlExpression = String.format("not(not(isnull(%s)) and %s>%s)", responseName, minValue, responseName);
            String controlErrorMessage = String.format("\" La valeur doit être supérieure à %s\"", minValue);
            controls.addFirst(LunaticFormatControl.createFormatControl(
                    controlIdPrefix+"-borne-inf", controlExpression, controlErrorMessage));
        }

        controls.add(createDecimalsFormatControl(controlIdPrefix, responseName, decimalsCount));
        return controls;
    }

    /**
     *
     * @param controlId control id
     * @param responseName component response name
     * @param decimalsCount decimals count allowed after semicolon
     * @return control for a decimal count
     */
    private static ControlType createDecimalsFormatControl(String controlId, String responseName, int decimalsCount) {
        String controlExpression = String.format("not(not(isnull(%s))  and round(%s,%d)<>%s)", responseName, responseName, decimalsCount, responseName);
        String controlErrorMessage = String.format("\"Le nombre doit comporter au maximum %d chiffre(s) après la virgule\"", decimalsCount);
        return LunaticFormatControl.createFormatControl(
                controlId+"-decimal", controlExpression, controlErrorMessage);
    }

    private static String formatDoubleValue(Double value, int decimalCount) {
        return BigDecimal.valueOf(value).setScale(decimalCount, RoundingMode.CEILING).toPlainString();
    }

}
