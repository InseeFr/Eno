package fr.insee.eno.treatments;

import fr.insee.eno.core.processing.OutProcessingInterface;
import fr.insee.lunatic.model.flat.Questionnaire;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LunaticPostProcessings implements OutProcessingInterface<Questionnaire> {

    private final List<OutProcessingInterface<Questionnaire>> lunaticProcessings;

    public LunaticPostProcessings() {
        this.lunaticProcessings = new ArrayList<>();
    }

    public void addPostProcessing(OutProcessingInterface<Questionnaire> lunaticProcessing) {
        log.info("Coucou");
        lunaticProcessings.add(lunaticProcessing);
    }

    @Override
    public void apply(Questionnaire questionnaire) {
        for(OutProcessingInterface<Questionnaire> lunaticProcessing : lunaticProcessings) {
            lunaticProcessing.apply(questionnaire);
        }
    }
}
