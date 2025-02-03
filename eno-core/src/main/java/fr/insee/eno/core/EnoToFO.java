package fr.insee.eno.core;

import fr.insee.eno.core.mappers.FOMapper;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.model.fo.Questionnaire;

import javax.xml.parsers.ParserConfigurationException;

public class EnoToFO implements EnoToOut<Questionnaire> {


    @Override
    public Questionnaire transform(EnoQuestionnaire enoQuestionnaire, EnoParameters enoParameters) {
        //
        FOMapper foMapper = new FOMapper();
        Questionnaire foQuestionnaire;
        foQuestionnaire = new Questionnaire();
        foMapper.mapQuestionnaire(enoQuestionnaire, foQuestionnaire);
        //
        //
        return foQuestionnaire;
    }
}
