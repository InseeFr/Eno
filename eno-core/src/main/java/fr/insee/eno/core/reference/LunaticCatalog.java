package fr.insee.eno.core.reference;

import fr.insee.lunatic.model.flat.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Class designed to be used in processing to easily access different kinds of Lunatic objects. */
public class LunaticCatalog {

    public static final List<Class<?>> sequenceClassTypes =
            List.of(Sequence.class, Subsequence.class);
    public static final List<Class<?>> questionClassTypes =
            List.of(Input.class, Textarea.class, InputNumber.class, CheckboxBoolean.class, Datepicker.class,
                    CheckboxOne.class, Radio.class, Dropdown.class, CheckboxGroup.class, Table.class);

    /** Map to get Lunatic components by id.*/
    private final Map<String, ComponentType> componentMap = new HashMap<>();

    public LunaticCatalog(Questionnaire lunaticQuestionnaire) {
        indexLunaticComponents(lunaticQuestionnaire);
    }

    private void indexLunaticComponents(Questionnaire lunaticQuestionnaire) {
        lunaticQuestionnaire.getComponents().forEach(component ->
                componentMap.put(component.getId(), component));
    }

    public ComponentType getComponent(String componentId) {
        return componentMap.get(componentId);
    }

}
