package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.Questionnaire;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LunaticAddGeneratingDate implements ProcessingStep<Questionnaire> {

    public void apply(Questionnaire lunaticQuestionnaire) {
        lunaticQuestionnaire.setGeneratingDate(
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(LocalDateTime.now()));
    }

}
