package fr.insee.eno.core;

import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.parameter.EnoParameters;

import java.io.InputStream;

public interface InToOut<T> {

    T transform(InputStream inputStream, EnoParameters enoParameters) throws ParsingException;

}
