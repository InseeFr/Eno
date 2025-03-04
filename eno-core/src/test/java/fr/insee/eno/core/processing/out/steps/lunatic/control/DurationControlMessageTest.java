package fr.insee.eno.core.processing.out.steps.lunatic.control;

import fr.insee.eno.core.processing.out.steps.lunatic.control.DurationControlMessage.HourMinuteControlMessage;
import fr.insee.eno.core.processing.out.steps.lunatic.control.DurationControlMessage.YearMonthControlMessage;
import fr.insee.eno.core.processing.out.steps.lunatic.control.LunaticDurationControl.HourMinuteValue;
import fr.insee.eno.core.processing.out.steps.lunatic.control.LunaticDurationControl.YearMonthValue;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DurationControlMessageTest {

    @Nested
    class YearMonthCase {
        YearMonthControlMessage logicClass = new YearMonthControlMessage();

        @Test
        void messageTest_between() {
            YearMonthValue minValue = new YearMonthValue(1, 6);
            YearMonthValue maxValue = new YearMonthValue(3, 1);
            //
            String result = logicClass.generateControlMessage(minValue, maxValue);
            //
            String expected = "\"La durée saisie doit être comprise entre 1 an et 6 mois et 3 ans et 1 mois.\"";
            assertEquals(expected, result);
        }

        @Test
        void messageTest_between2() {
            YearMonthValue minValue = new YearMonthValue(2, 6);
            YearMonthValue maxValue = new YearMonthValue(3, 0);
            //
            String result = logicClass.generateControlMessage(minValue, maxValue);
            //
            String expected = "\"La durée saisie doit être comprise entre 2 ans et 6 mois et 3 ans.\"";
            assertEquals(expected, result);
        }

        @Test
        void messageTest_lowerThan() {
            YearMonthValue minValue = new YearMonthValue(0, 0);
            YearMonthValue maxValue = new YearMonthValue(5, 0);
            //
            String result = logicClass.generateControlMessage(minValue, maxValue);
            //
            String expected = "\"La durée saisie doit être inférieure à 5 ans.\"";
            assertEquals(expected, result);
        }

        @Test
        void formatDurationTest() {
            assertEquals("1 an et 6 mois", logicClass.formatDuration(new YearMonthValue(1, 6)));
            assertEquals("1 an", logicClass.formatDuration(new YearMonthValue(1, 0)));
            assertEquals("10 ans", logicClass.formatDuration(new YearMonthValue(10, 0)));
            assertEquals("1 mois", logicClass.formatDuration(new YearMonthValue(0, 1)));
            assertEquals("3 mois", logicClass.formatDuration(new YearMonthValue(0, 3)));
            YearMonthValue zeroValue = new YearMonthValue(0, 0);
            assertThrows(IllegalArgumentException.class, () -> logicClass.formatDuration(zeroValue));
        }
    }

    @Nested
    class HourMinuteCase {
        HourMinuteControlMessage logicClass = new HourMinuteControlMessage();

        @Test
        void messageTest_between() {
            //
            HourMinuteValue minValue = new HourMinuteValue(1, 30);
            HourMinuteValue maxValue = new HourMinuteValue(3, 0);
            //
            String result = logicClass.generateControlMessage(minValue, maxValue);
            //
            String expected = "\"La durée saisie doit être comprise entre 1 heure 30 minutes et 3 heures.\"";
            assertEquals(expected, result);
        }

        @Test
        void messageTest_lowerThan() {
            //
            HourMinuteValue minValue = new HourMinuteValue(0, 0);
            HourMinuteValue maxValue = new HourMinuteValue(12, 0);
            //
            String result = logicClass.generateControlMessage(minValue, maxValue);
            //
            String expected = "\"La durée saisie doit être inférieure à 12 heures.\"";
            assertEquals(expected, result);
        }

        @Test
        void formatDurationValueTests() {
            assertEquals("1 heure 30 minutes", logicClass.formatDuration(new HourMinuteValue(1, 30)));
            assertEquals("1 minute", logicClass.formatDuration(new HourMinuteValue(0, 1)));
            assertEquals("45 minutes", logicClass.formatDuration(new HourMinuteValue(0, 45)));
            assertEquals("1 heure", logicClass.formatDuration(new HourMinuteValue(1, 0)));
            assertEquals("2 heures", logicClass.formatDuration(new HourMinuteValue(2, 0)));
            HourMinuteValue zeroValue = new HourMinuteValue(0, 0);
            assertThrows(IllegalArgumentException.class, () -> logicClass.formatDuration(zeroValue));
        }
    }

}
