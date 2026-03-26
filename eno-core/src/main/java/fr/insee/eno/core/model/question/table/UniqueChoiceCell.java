package fr.insee.eno.core.model.question.table;

import fr.insee.ddi.lifecycle33.datacollection.GridResponseDomainInMixedType;
import fr.insee.ddi.lifecycle33.reusable.RepresentationType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.code.CodeItem;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.BodyCell;
import fr.insee.lunatic.model.flat.Orientation;
import fr.insee.pogues.model.ResponseType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static fr.insee.eno.core.annotations.Contexts.Context;

/** A UniqueChoiceCell object is the content of a table.
 * A cell is neither part of the header nor of the left column. */
@Getter @Setter
@Context(format = Format.POGUES, type = ResponseType.class)
@Context(format = Format.DDI, type = GridResponseDomainInMixedType.class)
@Context(format = Format.LUNATIC, type = BodyCell.class)
public class UniqueChoiceCell extends ResponseCell {

    /**
     * Mapping uses the same logic as in unique choice question.
     * For DDI, an analog method defined for the GridResponseDomainInMixed object is used.
     * For Lunatic, method from Eno unique choice question class is reused.
     * */
    @Pogues("T(fr.insee.eno.core.model.question.UniqueChoiceQuestion).convertPoguesVisualizationHint(#this)")
    @DDI("T(fr.insee.eno.core.model.question.table.UniqueChoiceCell).convertDDIOutputFormat(#this)")
    @Lunatic("setComponentType(" +
            "T(fr.insee.eno.core.model.question.UniqueChoiceQuestion).convertDisplayFormatToLunatic(#param))")
    UniqueChoiceQuestion.DisplayFormat displayFormat;

    /** Lunatic property for the orientation of options.
     * Horizontal for table cells. */
    @Lunatic("setOrientation(T(fr.insee.lunatic.model.flat.Orientation).valueOf(#param))")
    String orientation = Orientation.HORIZONTAL.toString();

    @Pogues("getCodeListReference()")
    @DDI("getResponseDomain()?.getCodeListReference() != null ? " +
            "getResponseDomain().getCodeListReference().getIDArray(0).getStringValue() : null")
    String codeListReference;

    /** Variable providing the dynamic response options (UCQ based on an iteration (e.g. a loop)). */
    @Pogues("getChoiceType() == T(fr.insee.pogues.model.ChoiceTypeEnum).VARIABLE ? " +
            "#poguesIndex.get(#root.getVariableReference()).getName() : null")
    @Lunatic("setOptionSource(#param)")
    String optionSource;

    @Lunatic("getOptions()")
    List<CodeItem> codeItems = new ArrayList<>();

    /** Analog to the method in UniqueChoiceQuestion due to poor polymorphism in DDI. */
    public static UniqueChoiceQuestion.DisplayFormat convertDDIOutputFormat(
            GridResponseDomainInMixedType gridResponseDomainInMixedType) {
        RepresentationType responseDomain = gridResponseDomainInMixedType.getResponseDomain();
        // trick here: returning null so that the mapper doesn't overwrite Pogues value
        // (done due to variable-based UCQs not having a DDI modeling at some point)
        if (responseDomain == null)
            return null;
        String ddiOutputFormat = responseDomain.getGenericOutputFormat().getStringValue();
        Optional<UniqueChoiceQuestion.DisplayFormat> convertedDisplayFormat = UniqueChoiceQuestion.ddiValueToDisplayFormat(ddiOutputFormat);
        if (convertedDisplayFormat.isEmpty())
            throw new MappingException(
                    "Unknown output format '" + ddiOutputFormat + "' found in DDI 'GridResponseDomainInMixed' object.");
        return convertedDisplayFormat.get();
    }

}
