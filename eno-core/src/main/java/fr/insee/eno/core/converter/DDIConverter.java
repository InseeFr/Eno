package fr.insee.eno.core.converter;

import fr.insee.ddi.lifecycle33.datacollection.GridResponseDomainInMixedType;
import fr.insee.ddi.lifecycle33.datacollection.LoopType;
import fr.insee.ddi.lifecycle33.datacollection.QuestionGridType;
import fr.insee.ddi.lifecycle33.datacollection.QuestionItemType;
import fr.insee.ddi.lifecycle33.logicalproduct.VariableType;
import fr.insee.eno.core.exceptions.technical.ConversionException;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.question.table.TableCell;
import fr.insee.eno.core.model.response.ModalityAttachment;
import fr.insee.eno.core.reference.DDIIndex;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Slf4j
public class DDIConverter implements InConverter {

    /** DDI index, required to retrieve information across a DDI for certain conversions. */
    private DDIIndex index;

    /**
     * Returns an Eno instance corresponding to the given DDI object.
     * @return A Eno model object.
     */
    @Override
    public EnoObject convertToEno(Object ddiObject, Class<?> enoType) {

        if (ddiObject instanceof LoopType loopType)
            return DDILoopConversion.instantiateFrom(loopType);

        if (ddiObject instanceof QuestionItemType questionItemType)
            return DDIQuestionItemConversion.instantiateFrom(questionItemType, index);

        if (ddiObject instanceof QuestionGridType questionGridType)
            return DDIQuestionGridConversion.instantiateFrom(questionGridType);

        if (ddiObject instanceof GridResponseDomainInMixedType gridResponseDomainInMixedType
                && ModalityAttachment.class.isAssignableFrom(enoType))
            return DDIMultipleChoiceModalityConversion.instantiateFrom(gridResponseDomainInMixedType);

        if (ddiObject instanceof GridResponseDomainInMixedType gridResponseDomainInMixedType
                && TableCell.class.isAssignableFrom(enoType))
            return DDITableCellsConversion.instantiateFrom(gridResponseDomainInMixedType);

        if (ddiObject instanceof VariableType variableType)
            return DDIVariableConversion.instantiateFrom(variableType);

        throw new ConversionException("Eno conversion for DDI type " + ddiObject.getClass() + " not implemented.");
    }

}
