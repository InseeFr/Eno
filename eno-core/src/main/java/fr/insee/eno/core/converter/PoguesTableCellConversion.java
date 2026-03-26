package fr.insee.eno.core.converter;

import fr.insee.eno.core.exceptions.technical.ConversionException;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.question.table.*;
import fr.insee.pogues.model.DatatypeTypeEnum;
import fr.insee.pogues.model.ResponseType;
import fr.insee.pogues.model.VisualizationHintEnum;


class PoguesTableCellConversion {

    private PoguesTableCellConversion() {}

    static EnoObject instantiateFrom(ResponseType poguesResponse) {
        DatatypeTypeEnum typeName = poguesResponse.getDatatype().getTypeName();
        switch (poguesResponse.getDatatype().getTypeName()){
            case DURATION ->
                throw new UnsupportedOperationException("Duration cell in tables is not supported.");
            case NUMERIC -> {
                return new NumericCell();
            }
            case TEXT -> {
                if (isAChoiceResponse(poguesResponse)) {
                    if (VisualizationHintEnum.SUGGESTER.equals(poguesResponse.getDatatype().getVisualizationHint())) {
                        return new SuggesterCell();
                    }
                    return new UniqueChoiceCell();
                }
                return new TextCell();
            }
            case DATE -> {
                return new DateCell();
            }
            case BOOLEAN -> {
                return new BooleanCell();
            }
        }
        throw new ConversionException("Conversion of Pogues table cell of type " + typeName + " not implemented.");
    }

    private static boolean isAChoiceResponse(ResponseType poguesResponse) {
        // Note:
        // With the introduction of 'choiceType' in Pogues-Model, the condition "choiceType isn't null" is sufficient.
        // Yet, the 'codeListReference' condition ensures backwards compatibility with old questionnaires.
        return poguesResponse.getCodeListReference() != null
                || poguesResponse.getChoiceType() != null;
    }

}
