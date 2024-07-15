package fr.insee.eno.core.converter;

import fr.insee.ddi.lifecycle33.datacollection.*;
import fr.insee.ddi.lifecycle33.reusable.RepresentationType;
import fr.insee.ddi.lifecycle33.reusable.TextDomainType;
import fr.insee.eno.core.exceptions.technical.ConversionException;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.question.table.*;

public class DDITableCellsConversion {

    private DDITableCellsConversion() {}

    static EnoObject instantiateFrom(GridResponseDomainInMixedType gridResponseDomainInMixedType) {
        RepresentationType representationType = gridResponseDomainInMixedType.getResponseDomain();
        if (representationType instanceof NominalDomainType) {
            return new BooleanCell();
        }
        if (representationType instanceof TextDomainType) {
            return new TextCell();
        }
        if (representationType instanceof NumericDomainType) {
            return new NumericCell();
        }
        if (representationType instanceof DateTimeDomainType) {
            return new DateCell();
        }
        if (representationType instanceof CodeDomainType) {
            String ddiOutputFormat = gridResponseDomainInMixedType.getResponseDomain().getGenericOutputFormat().getStringValue();
            if (DDIQuestionItemConversion.DDI_SUGGESTER_OUTPUT_FORMAT.equals(ddiOutputFormat))
                return new SuggesterCell();
            return new UniqueChoiceCell();
        }
        //
        throw new ConversionException(
                "Unable to identify cell type in DDI GridResponseDomainInMixed object " +
                        "with response domain of type "+representationType.getClass()+".");
    }

}
