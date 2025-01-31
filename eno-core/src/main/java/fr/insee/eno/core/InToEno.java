package fr.insee.eno.core;

import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;

import java.io.InputStream;

public interface InToEno {

    /**
     * @deprecated use other transform method.
     */
    @Deprecated(since = "3.33.0")
    EnoQuestionnaire transform(InputStream inputStream, EnoParameters enoParameters) throws ParsingException;

    EnoQuestionnaire transform(EnoParameters enoParameters);

}
