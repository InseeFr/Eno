package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.EnoComponent;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.Filter;
import fr.insee.eno.core.processing.InProcessingInterface;

public class DDIInsertFilters implements InProcessingInterface {

    /** This method iterates on filters of the given Eno questionnaire, and set the filter expression
     * in each concerned component. */
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        //
        assert enoQuestionnaire.getIndex() != null;
        //
        for (Filter filter : enoQuestionnaire.getFilters()) {
            for (String componentId : filter.getComponentReferences()) {
                EnoComponent enoComponent = (EnoComponent) enoQuestionnaire.get(componentId);
                enoComponent.setFilter(filter);
            }
        }
    }

}
