package fr.insee.eno.core.converter;

import fr.insee.ddi.lifecycle33.datacollection.*;
import fr.insee.ddi.lifecycle33.reusable.RepresentationType;
import fr.insee.ddi.lifecycle33.reusable.TextDomainType;
import fr.insee.eno.core.exceptions.business.IllegalDDIElementException;
import fr.insee.eno.core.exceptions.technical.ConversionException;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.question.table.*;

public class DDITableCellsConversion {

    private DDITableCellsConversion() {}

    static EnoObject instantiateFrom(GridResponseDomainInMixedType gridResponseDomainInMixedType) {
        RepresentationType representationType = gridResponseDomainInMixedType.getResponseDomain();
        switch (representationType) {
            case null ->
                    throw new IllegalDDIElementException("Response domain is null in a table cell.");
            case NominalDomainType nominalDomainType -> {
                return new BooleanCell();
            }
            case TextDomainType textDomainType -> {
                return new TextCell();
            }
            case NumericDomainType numericDomainType -> {
                return new NumericCell();
            }
            case DateTimeDomainType dateTimeDomainType -> {
                return new DateCell();
            }
            case CodeDomainType codeDomainType -> {
                String ddiOutputFormat = gridResponseDomainInMixedType.getResponseDomain().getGenericOutputFormat().getStringValue();
                if (DDIQuestionItemConversion.DDI_SUGGESTER_OUTPUT_FORMAT.equals(ddiOutputFormat))
                    return new SuggesterCell();
                return new UniqueChoiceCell();
            }
            default ->
                    throw new ConversionException(
                            "Unable to identify cell type in DDI GridResponseDomainInMixed object " +
                                    "with response domain of type "+representationType.getClass()+".");
        }
    }

}
