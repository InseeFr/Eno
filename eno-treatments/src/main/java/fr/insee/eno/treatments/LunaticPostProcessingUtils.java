package fr.insee.eno.treatments;

import fr.insee.lunatic.model.flat.*;

import java.util.Optional;

public class LunaticPostProcessingUtils {

    private LunaticPostProcessingUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * ugly method to retrieve response name of a component (if the component allows it). Maybe need more abstraction in lunatic model ?
     * @param component component to check
     */
    static Optional<String> getResponseName(ComponentType component) {
        String responseName;
        switch (component.getComponentType()) {
            case INPUT            -> responseName = ((Input) component).getResponse().getName();
            case INPUT_NUMBER     -> responseName = ((InputNumber) component).getResponse().getName();
            case TEXTAREA         -> responseName = ((Textarea) component).getResponse().getName();
            case CHECKBOX_ONE     -> responseName = ((CheckboxOne) component).getResponse().getName();
            case CHECKBOX_BOOLEAN -> responseName = ((CheckboxBoolean) component).getResponse().getName();
            case DATEPICKER       -> responseName = ((Datepicker) component).getResponse().getName();
            case RADIO            -> responseName = ((Radio) component).getResponse().getName();
            case DROPDOWN         -> responseName = ((Dropdown) component).getResponse().getName();
            default               -> { return Optional.empty(); }
        }
        return Optional.of(responseName);
    }
}
