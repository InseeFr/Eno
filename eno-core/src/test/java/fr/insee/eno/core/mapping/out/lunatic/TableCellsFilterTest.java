package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.PoguesDDIToLunatic;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.Question;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.RosterForLoop;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class TableCellsFilterTest {

    private Questionnaire lunaticQuestionnaire;

    abstract Questionnaire mapQuestionnaire(EnoParameters enoParameters) throws ParsingException;

    static class PoguesDDITest extends TableCellsFilterTest {
        @Override
        Questionnaire mapQuestionnaire(EnoParameters enoParameters) throws ParsingException {
            ClassLoader classLoader = this.getClass().getClassLoader();
            return PoguesDDIToLunatic.fromInputStreams(
                            classLoader.getResourceAsStream("functional/pogues/cells-filtered/pogues-m92r209h.json"),
                            classLoader.getResourceAsStream("functional/ddi/cells-filtered/ddi-m92r209h.xml"))
                    .transform(enoParameters);
        }
    }

    @BeforeAll
    void integrationTest_fromPoguesOnly() throws ParsingException {
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        lunaticQuestionnaire = mapQuestionnaire(enoParameters);
    }

    @Test
    @DisplayName("Component filter for InputNumber in RosterForLoop.")
    void test01() {
        RosterForLoop rosterForLoop = (RosterForLoop) ((Question) lunaticQuestionnaire.getComponents().get(1)).getComponents().getFirst();
        assertEquals("AGE >= 18", rosterForLoop.getComponents().get(2).getConditionFilter().getValue());
    }
}
