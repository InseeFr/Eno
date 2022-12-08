package fr.insee.eno.core.processing.impl;

import fr.insee.lunatic.conversion.JSONDeserializer;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LunaticAddHierarchyTest {

    @Test
    public void testLunaticAddHierarchy() throws JAXBException, URISyntaxException {
        //
        URL url = this.getClass().getClassLoader().getResource("out/lunatic/sandbox.json");
        assert url != null;
        //
        JSONDeserializer lunaticDeserializer = new JSONDeserializer();
        Questionnaire lunaticQuestionnaire = lunaticDeserializer.deserialize(
                new File(url.toURI()).getAbsolutePath());
        //
        assertNotNull(lunaticQuestionnaire); //TODO: I didn't manage to make Lunatic deserializer work so far
    }

}
