package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.processing.OutProcessingInterface;
import fr.insee.lunatic.model.flat.Questionnaire;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LunaticAddGeneratingDate implements OutProcessingInterface<Questionnaire> {

    public void apply(Questionnaire lunaticQuestionnaire) {
        lunaticQuestionnaire.setGeneratingDate(
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(LocalDateTime.now()));
    }

}
