package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.processing.EnoProcessingInterface;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class EnoAddVersions implements EnoProcessingInterface {

    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        try {
            //
            Properties enoCoreProperties = new Properties();
            enoCoreProperties.load(new FileInputStream("gradle.properties"));
            //
            Properties enoProperties = new Properties();
            enoProperties.load(new FileInputStream("../gradle.properties"));
            //
            enoQuestionnaire.setEnoVersion(enoProperties.getProperty("version.eno"));
            enoQuestionnaire.setLunaticModelVersion(enoCoreProperties.getProperty("version.lunatic-model"));
        } catch (IOException e) {
            throw new RuntimeException("Unable to read gradle.properties file.");
        }
    }

}
