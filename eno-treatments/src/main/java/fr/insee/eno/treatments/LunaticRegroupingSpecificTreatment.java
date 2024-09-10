package fr.insee.eno.treatments;

import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.treatments.dto.Regroupement;
import fr.insee.eno.treatments.dto.Regroupements;
import fr.insee.lunatic.model.flat.Pagination;
import fr.insee.lunatic.model.flat.Questionnaire;

import java.util.List;

public class LunaticRegroupingSpecificTreatment implements ProcessingStep<Questionnaire> {

    private final Regroupements regroupements;

    public LunaticRegroupingSpecificTreatment(List<Regroupement> regroupementList) {
        super();
        this.regroupements = new Regroupements(regroupementList);
    }

    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        if (! Pagination.QUESTION.equals(lunaticQuestionnaire.getPaginationEnum()))
            throw new IllegalArgumentException(
                    "Regrouping questions is only possible for a questionnaire paginated in 'question' mode.");
        new LunaticPaginationRegrouping(regroupements).apply(lunaticQuestionnaire);
    }

}
