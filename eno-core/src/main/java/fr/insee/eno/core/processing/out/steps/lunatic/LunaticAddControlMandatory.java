package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.utils.LunaticUtils;
import fr.insee.eno.core.utils.vtl.VtlSyntaxUtils;
import fr.insee.lunatic.model.flat.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Processing step to add blocking checks for components marked as "mandatory". */
public class LunaticAddControlMandatory implements ProcessingStep<Questionnaire> {

    private static final Map<EnoParameters.Language, String> MANDATORY_MESSAGE = Map.of(
            EnoParameters.Language.FR, "La réponse à cette question est obligatoire.",
            EnoParameters.Language.EN, "This question is required."
    );

    private final EnoParameters.Language language;

    /** The constructor of this class sets the language of generated messages.
     * 'FR' is the default value. */
    public LunaticAddControlMandatory() {
        this.language = EnoParameters.Language.FR;
    }

    /**
     * @param language Language for error message in generated controls.
     */
    public LunaticAddControlMandatory(EnoParameters.Language language) {
        switch (language) {
            case FR, EN -> this.language = language;
            case null -> this.language = EnoParameters.Language.FR; // null => default
            default -> this.language = EnoParameters.Language.EN; // non-translated cases => english
        }
    }

    /** Generates "mandatory" controls for components that have the "mandatory" attribute set to true. */
    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        addMandatoryControls(lunaticQuestionnaire.getComponents());
        lunaticQuestionnaire.getComponents().stream().filter(Loop.class::isInstance)
                .map(Loop.class::cast)
                .map(Loop::getComponents)
                .forEach(this::addMandatoryControls);
    }

    private void addMandatoryControls(List<ComponentType> lunaticComponents) {
        lunaticComponents.stream()
                .filter(ComponentSimpleResponseType.class::isInstance)
                // use the "simple response" interface to get the mandatory prop
                // TODO: having an interface for "mandatory" in Lunatic-Model would be better
                .map(ComponentSimpleResponseType.class::cast)
                .filter(ComponentSimpleResponseType::getMandatory)
                // re-cast as a Lunatic component object
                .map(ComponentType.class::cast)
                .forEach(this::addMandatoryControls);
    }

    private void addMandatoryControls(ComponentType lunaticComponent) {
        List<String> expressions = generateMandatoryControlExpressions(lunaticComponent);
        for (int i = expressions.size() - 1; i >= 0; i--) {
            // iterate in reverse order to have detail response checks (if any) after the "main" one
            String expression = expressions.get(i);
            String id = lunaticComponent.getId() + "-mandatory-check";
            if (expressions.size() > 1)
                id += "-" + (i + 1);
            lunaticComponent.getControls().addFirst(
                    createMandatoryControl(id, expression));
        }
    }

    /** Generates a "mandatory" control expression in function of the component type.
     * If the component is not concerned by the "mandatory" property, the returned value is empty. */
    private List<String> generateMandatoryControlExpressions(ComponentType lunaticComponent) {
        if (lunaticComponent.getComponentType() == null)
            throw new IllegalArgumentException("Component " + lunaticComponent.getId() + " has no type defined.");
        List<String> expressions = new ArrayList<>();
        String responseName = ((ComponentSimpleResponseType) lunaticComponent).getResponse().getName();
        switch (lunaticComponent.getComponentType()) {
            case INPUT, TEXTAREA ->
                    expressions.add(textMandatoryExpression(responseName));
            case CHECKBOX_BOOLEAN ->
                    expressions.add(booleanMandatoryExpression(responseName));
            case INPUT_NUMBER, DATEPICKER, DURATION ->
                    expressions.add(defaultMandatoryExpression(responseName));
            case RADIO, DROPDOWN, CHECKBOX_ONE -> {
                expressions.add(defaultMandatoryExpression(responseName));
                expressions.addAll(uniqueChoiceDetailExpressions(responseName, LunaticUtils.getOptions(lunaticComponent)));
            }
            case QUESTIONNAIRE, SEQUENCE, SUBSEQUENCE, QUESTION, ROSTER_FOR_LOOP, LOOP, ROUNDABOUT, TABLE,
                    PAIRWISE_LINKS, CHECKBOX_GROUP, SUGGESTER, TEXT, FILTER_DESCRIPTION, ACCORDION ->
                    doNothing();
        }
        // Note: in Lunatic control expressions must be logically inverted
        return expressions.stream().map(VtlSyntaxUtils::invertBooleanExpression).toList();
    }
    private static String defaultMandatoryExpression(String responseName) {
        return String.format("isnull(%s)", responseName);
    }
    private static String booleanMandatoryExpression(String responseName) {
        return String.format("nvl(%s, false) = false", responseName);
    }
    private static String textMandatoryExpression(String responseName) {
        return String.format("trim(nvl(%s, \"\")) = \"\"", responseName);
    }
    private static void doNothing() {
        /* No-op method */
    }
    private static List<String> uniqueChoiceDetailExpressions(String responseName, List<Option> lunaticUniqueChoiceOptions) {
        return lunaticUniqueChoiceOptions.stream()
                .filter(option -> option.getDetail() != null)
                .map(option -> uniqueChoiceDetailExpression(responseName, option))
                .toList();
    }
    /** For a unique choice with a detail response, the control condition is: "the option is checked and the detail
     * field has content". */
    private static String uniqueChoiceDetailExpression(String responseName, Option lunaticUniqueChoiceOption) {
        assert lunaticUniqueChoiceOption.getDetail() != null;
        String optionValue = lunaticUniqueChoiceOption.getValue();
        String detailResponseName = lunaticUniqueChoiceOption.getDetail().getResponse().getName();
        return VtlSyntaxUtils.joinByANDLogicExpression(
                String.format("%s = \"%s\"", responseName, optionValue),
                textMandatoryExpression(detailResponseName));
    }

    /**
     * Returns a Lunatic control with a message saying that the response is mandatory, with given id and expression.
     * @param id Identifier of the control object.
     * @param expression VTL expression of the control.
     * @return A Lunatic control object.
     */
    private ControlType createMandatoryControl(String id, String expression) {
        ControlType lunaticControl = new ControlType();
        lunaticControl.setId(id);
        lunaticControl.setType(ControlContextType.SIMPLE);
        lunaticControl.setTypeOfControl(ControlTypeEnum.MANDATORY);
        lunaticControl.setCriticality(ControlCriticalityEnum.ERROR);
        lunaticControl.setControl(new LabelType());
        lunaticControl.getControl().setValue(expression);
        lunaticControl.getControl().setType(LabelTypeEnum.VTL);
        lunaticControl.setErrorMessage(new LabelType());
        lunaticControl.getErrorMessage().setValue(MANDATORY_MESSAGE.get(language));
        lunaticControl.getErrorMessage().setType(LabelTypeEnum.TXT);
        return lunaticControl;
    }

}
