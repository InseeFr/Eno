package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.lunatic.model.flat.CheckboxGroup;
import fr.insee.lunatic.model.flat.LabelTypeEnum;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SimpleUniqueChoiceQuestionTest {

    @Test
    void detailResponses_integrationTest() throws DDIParsingException {
        //
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-other-specify.xml"),
                EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI));
        Questionnaire lunaticQuestionnaire = new Questionnaire();

        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);

        //
        List<CheckboxGroup> checkboxGroupList = lunaticQuestionnaire.getComponents().stream()
                .filter(CheckboxGroup.class::isInstance).map(CheckboxGroup.class::cast).toList();
        assertEquals(1, checkboxGroupList.size());
        CheckboxGroup checkboxGroup = checkboxGroupList.getFirst();
        //
        assertEquals(4, checkboxGroup.getResponses().size());
        assertNull(checkboxGroup.getResponses().get(0).getDetail());
        assertNull(checkboxGroup.getResponses().get(1).getDetail());
        assertEquals("\"Please, specify about option C:\"",
                checkboxGroup.getResponses().get(2).getDetail().getLabel().getValue());
        assertEquals("\"Please, specify about option D:\"",
                checkboxGroup.getResponses().get(3).getDetail().getLabel().getValue());
        assertEquals(LabelTypeEnum.VTL_MD, checkboxGroup.getResponses().get(2).getDetail().getLabel().getTypeEnum());
        assertEquals(LabelTypeEnum.VTL_MD, checkboxGroup.getResponses().get(3).getDetail().getLabel().getTypeEnum());
        assertEquals("MCQ_codeC", checkboxGroup.getResponses().get(2).getDetail().getResponse().getName());
        assertEquals("MCQ_codeD", checkboxGroup.getResponses().get(3).getDetail().getResponse().getName());
    }

}
