package fr.insee.eno.treatments;

import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.treatments.dto.Regroupement;
import fr.insee.eno.treatments.dto.Regroupements;
import fr.insee.lunatic.model.flat.Questionnaire;

import java.util.List;

public class LunaticRegroupingSpecificTreatment implements ProcessingStep<Questionnaire> {

    private final Regroupements regroupements;
    private final boolean dsfrParameter;

    public LunaticRegroupingSpecificTreatment(List<Regroupement> regroupementList, boolean dsfrParameter) {
        super();
        this.regroupements = new Regroupements(regroupementList);
        this.dsfrParameter = dsfrParameter;
    }

    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        if (! "question".equals(lunaticQuestionnaire.getPagination()))
            throw new IllegalArgumentException(
                    "Regrouping questions is only possible for a questionnaire paginated in 'question' mode.");
        new LunaticPaginationRegrouping(regroupements).apply(lunaticQuestionnaire);
        if (dsfrParameter)
            groupQuestionsComponents(lunaticQuestionnaire);
    }

    /** In DSFR mode, question components that are on the same page should be grouped in a single question component. */
    private void groupQuestionsComponents(Questionnaire lunaticQuestionnaire) {
        // TODO
    }

}
