package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.lunatic.model.flat.CheckboxOne;
import fr.insee.lunatic.model.flat.LabelTypeEnum;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.Radio;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LunaticInsertUniqueChoiceDetailsTest {

    @Test
    void integrationTest() throws DDIParsingException {
        //
        EnoQuestionnaire enoQuestionnaire = DDIToEno.fromInputStream(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-other-specify.xml"))
                .transform(EnoParameters.of(Context.DEFAULT, ModeParameter.CAWI));
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);

        //
        new LunaticInsertUniqueChoiceDetails(enoQuestionnaire).apply(lunaticQuestionnaire);

        //
        Radio radio = lunaticQuestionnaire.getComponents().stream()
                .filter(Radio.class::isInstance).map(Radio.class::cast).findAny().orElse(null);
        CheckboxOne checkboxOne = lunaticQuestionnaire.getComponents().stream()
                .filter(CheckboxOne.class::isInstance).map(CheckboxOne.class::cast).findAny().orElse(null);
        assertNotNull(radio);
        assertNotNull(checkboxOne);
        //
        assertNull(radio.getOptions().get(0).getDetail());
        assertNull(radio.getOptions().get(1).getDetail());
        assertEquals("UCQ_codeC_RADIO", radio.getOptions().get(2).getDetail().getResponse().getName());
        assertEquals("\"Please, specify about option C:\"",
                radio.getOptions().get(2).getDetail().getLabel().getValue());
        assertEquals(LabelTypeEnum.VTL_MD, radio.getOptions().get(3).getDetail().getLabel().getType());
        //
        assertNull(checkboxOne.getOptions().get(0).getDetail());
        assertNull(checkboxOne.getOptions().get(1).getDetail());
        assertEquals("UCQ_codeC_CHECKBOX", checkboxOne.getOptions().get(2).getDetail().getResponse().getName());
        assertEquals("\"Please, specify about option C:\"",
                checkboxOne.getOptions().get(2).getDetail().getLabel().getValue());
        assertEquals(LabelTypeEnum.VTL_MD, checkboxOne.getOptions().get(3).getDetail().getLabel().getType());
    }

}
