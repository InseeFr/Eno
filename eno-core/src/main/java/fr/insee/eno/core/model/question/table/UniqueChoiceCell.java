package fr.insee.eno.core.model.question.table;

import fr.insee.ddi.lifecycle33.datacollection.GridResponseDomainInMixedType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.code.CodeItem;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.BodyCell;
import fr.insee.lunatic.model.flat.Orientation;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static fr.insee.eno.core.annotations.Contexts.Context;

/** A UniqueChoiceCell object is the content of a table.
 * A cell is neither part of the header nor of the left column. */
@Getter @Setter
@Context(format = Format.DDI, type = GridResponseDomainInMixedType.class)
@Context(format = Format.LUNATIC, type = BodyCell.class)
public class UniqueChoiceCell extends ResponseCell {

    /**
     * Mapping uses the same logic as in unique choice question.
     * For DDI, an analog method defined for the GridResponseDomainInMixed object is used.
     * For Lunatic, method from Eno unique choice question class is reused.
     * */
    @DDI("T(fr.insee.eno.core.model.question.table.UniqueChoiceCell).convertDDIOutputFormat(#this)")
    @Lunatic("setComponentType(" +
            "T(fr.insee.eno.core.model.question.UniqueChoiceQuestion).convertDisplayFormatToLunatic(#param))")
    UniqueChoiceQuestion.DisplayFormat displayFormat;

    /** Lunatic property for the orientation of modalities.
     * Horizontal for table cells. */
    @Lunatic("setOrientation(T(fr.insee.lunatic.model.flat.Orientation).valueOf(#param))")
    String orientation = Orientation.HORIZONTAL.toString();

    @DDI("getResponseDomain().getCodeListReference().getIDArray(0).getStringValue()")
    String codeListReference;

    @Lunatic("getOptions()")
    List<CodeItem> codeItems = new ArrayList<>();

    /** Analog to the method in UniqueChoiceQuestion due to poor polymorphism in DDI. */
    public static UniqueChoiceQuestion.DisplayFormat convertDDIOutputFormat(
            GridResponseDomainInMixedType gridResponseDomainInMixedType) {
        String ddiOutputFormat = gridResponseDomainInMixedType.getResponseDomain().getGenericOutputFormat().getStringValue();
        Optional<UniqueChoiceQuestion.DisplayFormat> convertedDisplayFormat = UniqueChoiceQuestion.ddiValueToDisplayFormat(ddiOutputFormat);
        if (convertedDisplayFormat.isEmpty())
            throw new MappingException(
                    "Unknown output format '" + ddiOutputFormat + "' found in DDI 'GridResponseDomainInMixed' object.");
        return convertedDisplayFormat.get();
    }

}
