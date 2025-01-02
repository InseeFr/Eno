package fr.insee.eno.core;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;

public interface EnoToOut<T> {

    T transform(EnoQuestionnaire enoQuestionnaire, EnoParameters enoParameters);

}
