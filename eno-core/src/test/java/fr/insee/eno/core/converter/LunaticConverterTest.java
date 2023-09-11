package fr.insee.eno.core.converter;

import fr.insee.eno.core.model.question.ComplexMultipleChoiceQuestion;
import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.eno.core.model.question.SimpleMultipleChoiceQuestion;
import fr.insee.eno.core.model.question.TableQuestion;
import fr.insee.lunatic.model.flat.CheckboxGroup;
import fr.insee.lunatic.model.flat.RosterForLoop;
import fr.insee.lunatic.model.flat.Table;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LunaticConverterTest {

    @Test
    void convertSimpleMultipleChoiceQuestion() {
        //
        SimpleMultipleChoiceQuestion enoSimpleMCQ = new SimpleMultipleChoiceQuestion();
        //
        Object result = LunaticConverter.instantiateFromEnoObject(enoSimpleMCQ);
        //
        assertTrue(result instanceof CheckboxGroup);
    }

    @Test
    void convertComplexMultipleChoiceQuestion() {
        //
        ComplexMultipleChoiceQuestion enoComplexMCQ = new ComplexMultipleChoiceQuestion();
        //
        Object result = LunaticConverter.instantiateFromEnoObject(enoComplexMCQ);
        //
        assertTrue(result instanceof Table);
    }

    @Test
    void convertEnoTableQuestion() {
        //
        TableQuestion enoTableQuestion = new TableQuestion();
        //
        Object result = LunaticConverter.instantiateFromEnoObject(enoTableQuestion);
        //
        assertTrue(result instanceof Table);
    }

    @Test
    void convertEnoDynamicTableQuestion() {
        //
        DynamicTableQuestion enoDynamicTableQuestion = new DynamicTableQuestion();
        //
        Object result = LunaticConverter.instantiateFromEnoObject(enoDynamicTableQuestion);
        //
        assertTrue(result instanceof RosterForLoop);
    }

}
