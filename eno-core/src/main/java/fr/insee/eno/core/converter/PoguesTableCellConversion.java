package fr.insee.eno.core.converter;

import fr.insee.eno.core.exceptions.technical.ConversionException;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.question.table.TextCell;
import fr.insee.eno.core.model.question.table.UniqueChoiceCell;
import fr.insee.pogues.model.DatatypeTypeEnum;
import fr.insee.pogues.model.ResponseType;

class PoguesTableCellConversion {

    private PoguesTableCellConversion() {}

    static EnoObject instantiateFrom(ResponseType poguesResponse) {
        DatatypeTypeEnum typeName = poguesResponse.getDatatype().getTypeName();
        if (DatatypeTypeEnum.TEXT.equals(typeName)) {
            if (poguesResponse.getCodeListReference() != null)
                return new UniqueChoiceCell();
            return new TextCell();
        }
        throw new ConversionException("Conversion of Pogues table cell of type " + typeName + " not implemented.");
    }

}
