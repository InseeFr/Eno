package fr.insee.eno.core;

import fr.insee.ddi.lifecycle33.instance.DDIInstanceDocument;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import fr.insee.lunatic.model.flat.Questionnaire;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PoguesDDIToLunatic {

    private final fr.insee.pogues.model.Questionnaire poguesQuestionnaire;
    private final DDIInstanceDocument ddiQuestionnaire;

    public static PoguesDDIToLunatic fromInputStreams(
            InputStream poguesInputStream, InputStream ddiInputStream) throws ParsingException {
        return new PoguesDDIToLunatic(
                PoguesDeserializer.deserialize(poguesInputStream),
                DDIDeserializer.deserialize(ddiInputStream));
    }

    public static PoguesDDIToLunatic fromObjects(
            fr.insee.pogues.model.Questionnaire poguesQuestionnaire, DDIInstanceDocument ddiQuestionnaire) {
        return new PoguesDDIToLunatic(poguesQuestionnaire, ddiQuestionnaire);
    }

    public Questionnaire transform(EnoParameters enoParameters) {
        //
        EnoQuestionnaire enoQuestionnaire = PoguesDDIToEno.fromObjects(poguesQuestionnaire, ddiQuestionnaire)
                .transform(enoParameters);
        //
        return new EnoToLunatic().transform(enoQuestionnaire, enoParameters);

    }


}
