package fr.insee.eno.core.processing.impl;

import fr.insee.lunatic.conversion.JSONDeserializer;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LunaticAddHierarchyTest {

    @Test
    public void testLunaticAddHierarchy() throws JAXBException {
        JSONDeserializer lunaticDeserializer = new JSONDeserializer();
        Questionnaire lunaticQuestionnaire = lunaticDeserializer.deserialize(
                Path.of("src/test/resources/out/lunatic/sandbox.json").toAbsolutePath().toString());
        //
        assertNotNull(lunaticQuestionnaire); //TODO: I didn't manage to make Lunatic deserializer work so far
    }
}
