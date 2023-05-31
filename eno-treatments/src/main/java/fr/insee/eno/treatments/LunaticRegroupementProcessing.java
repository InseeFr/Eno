package fr.insee.eno.treatments;

import fr.insee.eno.core.processing.OutProcessingInterface;
import fr.insee.eno.treatments.dto.Regroupement;
import fr.insee.eno.treatments.dto.Regroupements;
import fr.insee.lunatic.model.flat.ComponentType;
import fr.insee.lunatic.model.flat.Questionnaire;

import java.util.List;

public class LunaticRegroupementProcessing implements OutProcessingInterface<Questionnaire> {
    private final Regroupements regroupements;

    public LunaticRegroupementProcessing(List<Regroupement> regroupements) {
        this.regroupements = new Regroupements(regroupements);
    }

    @Override
    public void apply(Questionnaire questionnaire) {
        int numPage = 1;
        List<ComponentType> components = questionnaire.getComponents();
        for(ComponentType component: components) {
            if(component.getPage() != null && !component.getPage().isEmpty()) {
                component.setPage(String.valueOf(numPage));
            }
            numPage++;
        }
    }
}
