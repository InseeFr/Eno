package fr.insee.eno.core.i18n.date;

import fr.insee.eno.core.parameter.EnoParameters;

public class DateFormatterFactory {

    private DateFormatterFactory() {}

    public static DateFormatter forLanguage(EnoParameters.Language language) {
        if (EnoParameters.Language.FR.equals(language))
            return new Iso8601ToFrench();
        // By default, return the implementation that returns date values in ISO-8601 format
        return new Iso8601Formatter();
    }

}
