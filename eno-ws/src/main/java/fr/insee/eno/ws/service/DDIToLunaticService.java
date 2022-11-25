package fr.insee.eno.ws.service;

import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.exceptions.DDIParsingException;
import fr.insee.eno.core.exceptions.LunaticSerializationException;
import fr.insee.eno.core.parameter.EnoParameters;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class DDIToLunaticService {

    public String transform(InputStream ddiInputStream, EnoParameters parameterInputStream)
            throws IOException, DDIParsingException, LunaticSerializationException {
        return DDIToLunatic.transform(ddiInputStream, parameterInputStream);
    }

}
