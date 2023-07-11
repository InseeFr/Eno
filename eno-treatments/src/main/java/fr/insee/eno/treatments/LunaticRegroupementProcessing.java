package fr.insee.eno.treatments;

import fr.insee.eno.core.processing.impl.lunatic.pagination.LunaticAddPageNumbersQuestionMode;
import fr.insee.eno.treatments.dto.Regroupement;
import fr.insee.eno.treatments.dto.Regroupements;
import fr.insee.lunatic.model.flat.*;

import java.util.*;

/**
 * Post processing of a lunatic questionnaire. Without this processing, one question is displayed on each page.
 * This post processing permits to regroup questions of a questionnaire in same pages
 * /!\ We assume variables defined in a regroupement are consecutive and in the same order as their
 * corresponding components in the questionnaire
 */
public class LunaticRegroupementProcessing extends LunaticAddPageNumbersQuestionMode {
    private final Regroupements regroupements;

    /**
     * @param regroupements questions regroupements for a questionnaire
     */
    public LunaticRegroupementProcessing(List<Regroupement> regroupements) {
        super();
        this.regroupements = new Regroupements(regroupements);
    }

    /**
     * Check if the page attribute for a component can be incremented
     * @param component component to check
     * @param isParentPaginated is the parent component paginated or not
     * @return true if the numpage for this component can be incremented, false otherwise
     */
    @Override
    public boolean canIncrementPageCount(ComponentType component, boolean isParentPaginated) {
        if(!super.canIncrementPageCount(component, isParentPaginated)) {
            return false;
        }

        String responseName = null;

        if(component instanceof ComponentSimpleResponseType simpleResponse) {
            responseName = simpleResponse.getResponse().getName();
        }

        // no response name, so no regroupement, we can increment
        if(responseName == null) {
            return true;
        }

        // no regroupements, we can increment
        if(regroupements == null) {
            return true;
        }

        Optional<Regroupement> optRegroupement = regroupements.getRegroupementForVariable(responseName);
        // if no regroupement, we can increment
        if(optRegroupement.isEmpty()) {
            return true;
        }

        Regroupement regroupement = optRegroupement.get();

        //if variable is the first variable of the regroupement, we can increment
        return regroupement.isFirstVariable(responseName);
    }
}
