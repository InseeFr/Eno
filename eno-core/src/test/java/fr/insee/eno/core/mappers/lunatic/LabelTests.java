package fr.insee.eno.core.mappers.lunatic;

import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.label.DynamicLabel;
import fr.insee.eno.core.model.label.Label;
import fr.insee.eno.core.model.label.QuestionnaireLabel;
import fr.insee.lunatic.model.flat.LabelType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LabelTests {

    @Test
    void basicLabel_type() {
        //
        Label enoLabel = new Label();
        LabelType lunaticLabel = new LabelType();
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoLabel, lunaticLabel);
        //
        assertEquals("VTL|MD", lunaticLabel.getType());
    }

    @Test
    void dynamicLabel_type() {
        //
        DynamicLabel enoLabel = new DynamicLabel();
        LabelType lunaticLabel = new LabelType();
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoLabel, lunaticLabel);
        //
        assertEquals("VTL|MD", lunaticLabel.getType());
    }

    @Test
    void questionnaireLabel_type() {
        //
        QuestionnaireLabel enoLabel = new QuestionnaireLabel();
        LabelType lunaticLabel = new LabelType();
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoLabel, lunaticLabel);
        //
        assertEquals("VTL|MD", lunaticLabel.getType());
    }

    @Test
    void calculatedExpression_type() {
        //
        CalculatedExpression enoCalculatedExpression = new CalculatedExpression();
        LabelType lunaticLabel = new LabelType();
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoCalculatedExpression, lunaticLabel);
        //
        assertEquals("VTL", lunaticLabel.getType());
    }

}
