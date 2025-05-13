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
            String expected = "\"La durée saisie doit être comprise entre 1 an et 6 mois minimum et 3 ans et 1 mois maximum\"";
            assertEquals(expected, result);
        }

        @Test
        void messageTest_between2() {
            YearMonthValue minValue = new YearMonthValue(2, 6);
            YearMonthValue maxValue = new YearMonthValue(3, 0);
            //
            String result = logicClass.generateControlMessage(minValue, maxValue);
            //
            String expected = "\"La durée saisie doit être comprise entre 2 ans et 6 mois minimum et 3 ans maximum\"";
            assertEquals(expected, result);
        }

        @Test
        void messageTest_lowerThan() {
            YearMonthValue minValue = new YearMonthValue(0, 0);
            YearMonthValue maxValue = new YearMonthValue(5, 0);
            //
            String result = logicClass.generateControlMessage(minValue, maxValue);
            //
            String expected = "\"La durée saisie doit être inférieure à 5 ans\"";
            assertEquals(expected, result);
        }

        @Test
        void messageTest_minGreaterThanMax1() {
            YearMonthValue minValue = new YearMonthValue(1, 0);
            YearMonthValue maxValue = new YearMonthValue(0, 6);

            DurationControlMessage.YearMonthControlMessage logicClass1 = new DurationControlMessage.YearMonthControlMessage();

            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                logicClass1.generateControlMessage(minValue, maxValue);
            });

            assertEquals("The minimum duration cannot be greater than the maximum duration.", exception.getMessage());
        }

        @Test
        void messageTest_minGreaterThanMax2() {
            YearMonthValue minValue = new YearMonthValue(2, 6);
            YearMonthValue maxValue = new YearMonthValue(2, 4);

            DurationControlMessage.YearMonthControlMessage logicClass1 = new DurationControlMessage.YearMonthControlMessage();

            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                logicClass1.generateControlMessage(minValue, maxValue);
            });

            assertEquals("The minimum duration cannot be greater than the maximum duration.", exception.getMessage());
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
            String expected = "\"La durée saisie doit être comprise entre 1 heure et 30 minutes minimum et 3 heures maximum\"";
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
            String expected = "\"La durée saisie doit être inférieure à 12 heures\"";
            assertEquals(expected, result);
        }

        @Test
        void messageTest_minGreaterThanMax1() {
            HourMinuteValue minValue = new HourMinuteValue(1, 0);
            HourMinuteValue maxValue = new HourMinuteValue(0, 6);

            DurationControlMessage.HourMinuteControlMessage logicClass1 = new DurationControlMessage.HourMinuteControlMessage();

            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                logicClass1.generateControlMessage(minValue, maxValue);
            });

            assertEquals("The minimum duration cannot be greater than the maximum duration.", exception.getMessage());
        }

        @Test
        void messageTest_minGreaterThanMax2() {
            HourMinuteValue minValue = new HourMinuteValue(2, 6);
            HourMinuteValue maxValue = new HourMinuteValue(2, 4);

            DurationControlMessage.HourMinuteControlMessage logicClass1 = new DurationControlMessage.HourMinuteControlMessage();

            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                logicClass1.generateControlMessage(minValue, maxValue);
            });

            assertEquals("The minimum duration cannot be greater than the maximum duration.", exception.getMessage());
        }

        @Test
        void formatDurationValueTests() {
            assertEquals("1 heure et 30 minutes", logicClass.formatDuration(new HourMinuteValue(1, 30)));
            assertEquals("1 minute", logicClass.formatDuration(new HourMinuteValue(0, 1)));
            assertEquals("45 minutes", logicClass.formatDuration(new HourMinuteValue(0, 45)));
            assertEquals("1 heure", logicClass.formatDuration(new HourMinuteValue(1, 0)));
            assertEquals("2 heures", logicClass.formatDuration(new HourMinuteValue(2, 0)));
            HourMinuteValue zeroValue = new HourMinuteValue(0, 0);
            assertThrows(IllegalArgumentException.class, () -> logicClass.formatDuration(zeroValue));
        }
    }

}
