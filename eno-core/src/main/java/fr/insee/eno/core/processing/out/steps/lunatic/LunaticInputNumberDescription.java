package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.*;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Generate a description for input number components.
 * Note: this processing must be called before wrapping components in Question components.
 */
public class LunaticInputNumberDescription implements ProcessingStep<Questionnaire> {

    private static final Map<EnoParameters.Language, String> INPUT_NUMBER_DESCRIPTION_CANVAS = Map.of(
            EnoParameters.Language.FR, "Format attendu : un nombre%s entre %s et %s",
            EnoParameters.Language.EN, "Expected format: a number%s between %s and %s");
    private static final Map<EnoParameters.Language, String> UNIT_DESCRIPTION_PREFIX = Map.of(
            EnoParameters.Language.FR, " en ",
            EnoParameters.Language.EN, " in ");

    private final EnoParameters.Language language;
    private final Locale locale;

    public LunaticInputNumberDescription(EnoParameters.Language language) {
        this.language = language;
        locale = switch (language) {
            case FR -> Locale.FRENCH;
            case EN -> Locale.ENGLISH;
            case IT, ES, DE -> throw new UnsupportedOperationException("Language " + language + " is not supported.");
        };
    }

    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        //
        generateInputNumberDescription(lunaticQuestionnaire.getComponents());
        //
        lunaticQuestionnaire.getComponents().stream()
                .filter(Loop.class::isInstance).map(Loop.class::cast)
                .map(Loop::getComponents)
                .forEach(this::generateInputNumberDescription);
    }

    private void generateInputNumberDescription(List<ComponentType> lunaticComponents) {
        lunaticComponents.stream()
                .filter(InputNumber.class::isInstance).map(InputNumber.class::cast)
                .forEach(this::generateInputNumberDescription);
    }

    private void generateInputNumberDescription(InputNumber inputNumber) {
        //
        String unit = inputNumber.getUnit();
        int decimals = inputNumber.getDecimals() != null ? inputNumber.getDecimals().intValue() : 0;
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        decimalFormat.setMinimumFractionDigits(decimals);
        String minDescription = decimalFormat.format(inputNumber.getMin());
        String maxDescription = decimalFormat.format(inputNumber.getMax());
        //
        String unitDescription = unit != null ? UNIT_DESCRIPTION_PREFIX.get(language) + unit : "";
        String generatedDescription = String.format(INPUT_NUMBER_DESCRIPTION_CANVAS.get(language),
                unitDescription, minDescription, maxDescription);
        //
        LabelType description = new LabelType();
        description.setValue(generatedDescription);
        description.setType(LabelTypeEnum.TXT);
        inputNumber.setDescription(description);
    }

}
