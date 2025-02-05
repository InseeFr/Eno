package fr.insee.eno.ws.service;

import fr.insee.eno.treatments.LunaticPostProcessing;
import fr.insee.eno.treatments.LunaticRegroupingSpecificTreatment;
import fr.insee.eno.treatments.LunaticSuggesterSpecificTreatment;
import fr.insee.eno.treatments.SpecificTreatmentsDeserializer;
import fr.insee.eno.treatments.dto.EnoSuggesterType;
import fr.insee.eno.treatments.dto.Regroupement;
import fr.insee.eno.treatments.dto.SpecificTreatments;
import fr.insee.eno.ws.exception.EnoControllerException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class SpecificTreatmentsService {

    private void validatePostProcessingFile(MultipartFile specificTreatmentsFile)
            throws EnoControllerException {
        String fileName = specificTreatmentsFile.getOriginalFilename();
        if (fileName == null)
            throw new EnoControllerException("Specific treatments file name is null.");
        if (! fileName.endsWith(".json"))
            throw new EnoControllerException("Eno Java specific treatments file name must end with '.json'.");
    }

    public LunaticPostProcessing generateFrom(MultipartFile specificTreatmentsFile)
            throws EnoControllerException, IOException {
        validatePostProcessingFile(specificTreatmentsFile);

        LunaticPostProcessing lunaticPostProcessings = new LunaticPostProcessing();

        SpecificTreatmentsDeserializer deserializer = new SpecificTreatmentsDeserializer();
        SpecificTreatments treatments = deserializer.deserialize(specificTreatmentsFile.getInputStream());

        List<EnoSuggesterType> suggesters = treatments.suggesters();
        if(suggesters != null && !suggesters.isEmpty())
            lunaticPostProcessings.addPostProcessing(new LunaticSuggesterSpecificTreatment(suggesters));

        List<Regroupement> regroupements = treatments.regroupements();
        if(regroupements != null && !regroupements.isEmpty()) {
            lunaticPostProcessings.addPostProcessing(new LunaticRegroupingSpecificTreatment(regroupements));
        }

        return lunaticPostProcessings;
    }
}
