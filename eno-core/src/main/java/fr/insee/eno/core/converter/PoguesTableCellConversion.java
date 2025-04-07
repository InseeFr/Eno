package fr.insee.eno.core.converter;

import fr.insee.eno.core.exceptions.technical.ConversionException;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.question.table.*;
import fr.insee.pogues.model.DatatypeTypeEnum;
import fr.insee.pogues.model.ResponseType;

class PoguesTableCellConversion {

    private PoguesTableCellConversion() {}

    static EnoObject instantiateFrom(ResponseType poguesResponse) {
        DatatypeTypeEnum typeName = poguesResponse.getDatatype().getTypeName();
        switch (poguesResponse.getDatatype().getTypeName()){
            case NUMERIC -> {
                return new NumericCell();
            }
            case TEXT -> {
                if (poguesResponse.getCodeListReference() != null)
                    // TODO: fix me, I can be SuggesterCell
                    return new UniqueChoiceCell();
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

}
