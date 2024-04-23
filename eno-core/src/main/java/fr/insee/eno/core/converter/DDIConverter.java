package fr.insee.eno.core.converter;

import datacollection33.GridResponseDomainInMixedType;
import datacollection33.LoopType;
import datacollection33.QuestionGridType;
import datacollection33.QuestionItemType;
import fr.insee.eno.core.exceptions.technical.ConversionException;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.question.table.TableCell;
import fr.insee.eno.core.model.response.ModalityAttachment;
import fr.insee.eno.core.reference.DDIIndex;
import logicalproduct33.VariableType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DDIConverter {

    private DDIConverter() {}

    /**
     * Return an Eno instance corresponding to the given DDI object.
     * @return A Eno model object.
     */
    public static EnoObject instantiateFromDDIObject(Object ddiObject, DDIIndex ddiIndex, Class<?> enoType) {

        if (ddiObject instanceof LoopType loopType)
            return DDILoopConversion.instantiateFrom(loopType);

        if (ddiObject instanceof QuestionItemType questionItemType)
            return DDIQuestionItemConversion.instantiateFrom(questionItemType, ddiIndex);

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
