package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.EnoComponent;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Filter;
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
                //
                EnoObject enoObject = enoQuestionnaire.get(componentId);
                // Filter: set parent relationship
                if (enoObject instanceof Filter filter1)
                    filter1.setParentFilter(filter);
                // Component: set the filter
                else if (enoObject instanceof EnoComponent enoComponent)
                    enoComponent.setFilter(filter);
                //
                else
                    throw new RuntimeException(String.format(
                            "Filter '%s' has an object in its scope that is neither a component or a filter. " +
                                    "Object in question: %s",
                            filter.getId(), enoObject));
            }
        }
    }

}
