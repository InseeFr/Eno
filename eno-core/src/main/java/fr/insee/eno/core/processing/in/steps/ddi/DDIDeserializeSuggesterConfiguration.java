package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.suggester.SuggesterConfigurationDTO;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.serialize.DDISuggesterDeserializer;

public class DDIDeserializeSuggesterConfiguration implements ProcessingStep<EnoQuestionnaire> {

    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        enoQuestionnaire.getCodeLists().stream()
                .filter(codeList -> codeList.getXmlSuggesterConfiguration() != null)
                .forEach(this::deserializeXmlContent);
    }

    private void deserializeXmlContent(CodeList codeList) {
        SuggesterConfigurationDTO suggesterConfigurationDTO = new DDISuggesterDeserializer().deserialize(
                codeList.getXmlSuggesterConfiguration());
        codeList.setSuggesterConfiguration(suggesterConfigurationDTO);
    }

}
