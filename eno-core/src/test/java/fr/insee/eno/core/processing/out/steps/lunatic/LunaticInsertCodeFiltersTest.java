package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.PoguesToEno;
import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import fr.insee.lunatic.model.flat.CheckboxGroup;
import fr.insee.lunatic.model.flat.Radio;
import fr.insee.pogues.model.Questionnaire;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LunaticInsertCodeFiltersTest {

    @Test
    void deserialize_largeQuestionnaire() throws URISyntaxException, PoguesDeserializationException {
        //
        URL testPoguesFileUrl = this.getClass().getClassLoader().getResource(
                "functional/pogues/codes-filtered/pogues-m8hgkyw0.json");
        assert testPoguesFileUrl != null;
        //
        Questionnaire poguesQuestionnaire = PoguesDeserializer.deserialize(testPoguesFileUrl);
        //
        assertNotNull(poguesQuestionnaire);
        assertEquals("m8hgkyw0", poguesQuestionnaire.getId());

        EnoQuestionnaire enoQuestionnaire = PoguesToEno.fromObject(poguesQuestionnaire).
                transform(EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI));

        fr.insee.lunatic.model.flat.Questionnaire lunaticQuestionnaire = new fr.insee.lunatic.model.flat.Questionnaire();
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);

        //
        new LunaticInsertCodeFilters(enoQuestionnaire).apply(lunaticQuestionnaire);

        //
        Radio radio = lunaticQuestionnaire.getComponents().stream()
                .filter(Radio.class::isInstance)
                .map(Radio.class::cast)
                .findAny().orElse(null);
        assertNotNull(radio);
        assertEquals("m8hgctb4", radio.getId());
        assertEquals("nvl(AGE, 0) > 18", radio.getOptions().stream()
                .filter(c -> "3".equals(c.getValue()))
                .toList()
                .getFirst()
                .getConditionFilter().getValue());
        //
        //
        CheckboxGroup checkboxGroup = lunaticQuestionnaire.getComponents().stream()
                .filter(CheckboxGroup.class::isInstance)
                .map(CheckboxGroup.class::cast)
                .findAny().orElse(null);
        assertNotNull(checkboxGroup);
        assertEquals("m8hgrsnb", checkboxGroup.getId());
        assertEquals("nvl(AGE, 0) > 50", checkboxGroup
                .getResponses().get(3)
                .getConditionFilter().getValue());
    }
}