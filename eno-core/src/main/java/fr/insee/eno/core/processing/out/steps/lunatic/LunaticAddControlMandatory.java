package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
                .filter(ComponentMandatory.class::isInstance)
                .map(ComponentMandatory.class::cast)
                .filter(c -> Boolean.TRUE.equals(c.getMandatory()))
                .map(ComponentType.class::cast)
                .forEach(this::addMandatoryControl);
    }

    private void addMandatoryControl(ComponentType lunaticComponent) {
        Optional<String> expression = generateMandatoryControlExpression(lunaticComponent);
        expression.ifPresent(expr ->
                lunaticComponent.getControls().addFirst(
                    createMandatoryControl(lunaticComponent.getId() + "-mandatory-check", expr)));
    }

    /** Generates a "mandatory" control expression in function of the component type.
     * If the component is not concerned by the "mandatory" property, the returned value is empty. */
    private Optional<String> generateMandatoryControlExpression(ComponentType lunaticComponent) {
        if (lunaticComponent.getComponentType() == null)
            throw new IllegalArgumentException("Component " + lunaticComponent.getId() + " has no type defined.");

        return switch (lunaticComponent.getComponentType()) {

            case INPUT, TEXTAREA -> {
                String responseName = ((ComponentSimpleResponseType) lunaticComponent).getResponse().getName();
                yield Optional.of(String.format("not(trim(nvl(%s, \"\")) = \"\")", responseName));
            }

            case CHECKBOX_BOOLEAN -> {
                String responseName = ((ComponentSimpleResponseType) lunaticComponent).getResponse().getName();
                yield Optional.of(String.format("not(nvl(%s, false) = false)", responseName));
            }

            case INPUT_NUMBER, DATEPICKER, DURATION, RADIO, DROPDOWN, CHECKBOX_ONE -> {
                String responseName = ((ComponentSimpleResponseType) lunaticComponent).getResponse().getName();
                yield Optional.of(String.format("not(isnull(%s))", responseName));
            }

            case CHECKBOX_GROUP -> {
                CheckboxGroup checkboxGroup = (CheckboxGroup) lunaticComponent;
                List<ResponseCheckboxGroup> responses = checkboxGroup.getResponses();
                if (responses == null || responses.isEmpty()) yield Optional.empty();

                String joined = responses.stream()
                        .filter(r -> r.getDetail() == null)
                        .map(ResponseCheckboxGroup::getResponse)
                        .filter(Objects::nonNull)
                        .map(ResponseType::getName)
                        .filter(Objects::nonNull)
                        .map(name -> String.format("nvl(%s, false)", name))
                        .collect(Collectors.joining(" or "));

                if (joined.isBlank()) yield Optional.empty();
                yield Optional.of("(" + joined + ")");
            }

            case QUESTIONNAIRE, SEQUENCE, SUBSEQUENCE, QUESTION, ROSTER_FOR_LOOP, LOOP, ROUNDABOUT,
                 TABLE, PAIRWISE_LINKS, SUGGESTER, TEXT, FILTER_DESCRIPTION, ACCORDION ->
                    Optional.empty();
        };
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
