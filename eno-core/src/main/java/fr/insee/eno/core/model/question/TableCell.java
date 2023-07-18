package fr.insee.eno.core.model.question;

import datacollection33.GridResponseDomainInMixedType;
import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.code.CodeItem;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.BodyCell;
import fr.insee.lunatic.model.flat.Datepicker;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static fr.insee.eno.core.annotations.Contexts.Context;

/** A TableCell object is the content of a table.
 * A cell is neither part of the header nor of the left column. */
@Getter
@Setter
@Context(format = Format.DDI, type = GridResponseDomainInMixedType.class)
@Context(format = Format.LUNATIC, type = BodyCell.class)
public abstract class TableCell extends EnoObject {

    @DDI("T(java.lang.Integer).parseInt(getGridAttachmentArray(0).getCellCoordinatesAsDefinedArray(0).getSelectDimensionList()" +
            ".?[#this.getRank().intValue() == 1].get(0).getRangeMinimum())") // range maximum is the same in Insee DDI
    int rowNumber;

    @DDI("T(java.lang.Integer).parseInt(getGridAttachmentArray(0).getCellCoordinatesAsDefinedArray(0).getSelectDimensionList()" +
            ".?[#this.getRank().intValue() == 2].get(0).getRangeMinimum())") // range maximum is the same in Insee DDI
    int columnNumber;

    // TODO: refactor DDI response domain mapping in questions and table cells

    @Getter @Setter
    @Context(format = Format.DDI, type = GridResponseDomainInMixedType.class)
    @Context(format = Format.LUNATIC, type = BodyCell.class)
    public static class BooleanCell extends TableCell {}

    @Getter @Setter
    @Context(format = Format.DDI, type = GridResponseDomainInMixedType.class)
    @Context(format = Format.LUNATIC, type = BodyCell.class)
    public static class TextCell extends TableCell {
        @DDI("getResponseDomain().getMaxLength().intValue()")
        @Lunatic("setMaxLength(#param)")
        BigInteger maxLength;
    }

    @Getter @Setter
    @Context(format = Format.DDI, type = GridResponseDomainInMixedType.class)
    @Context(format = Format.LUNATIC, type = BodyCell.class)
    public static class NumericCell extends TableCell {
        @DDI("getResponseDomain()?.getNumberRangeList()?.get(0)?.getLow()?.getStringValue() != null ? " +
                "T(java.lang.Double).valueOf(getResponseDomain().getNumberRangeArray(0).getLow().getStringValue()) : null")
        @Lunatic("setMin(#param)")
        Double minValue;

        @DDI("getResponseDomain()?.getNumberRangeList()?.get(0)?.getHigh()?.getStringValue() != null ? " +
                "T(java.lang.Double).valueOf(getResponseDomain().getNumberRangeArray(0).getHigh().getStringValue()) : null")
        @Lunatic("setMax(#param)")
        Double maxValue;

        @DDI("getResponseDomain()?.getDecimalPositions() ?: T(java.math.BigInteger).valueOf('0')")
        @Lunatic("setDecimals(#param)")
        BigInteger numberOfDecimals;

        /** Unit not accessible here in DDI.
         * In Lunatic, the value is set there during the table processing. */
        String unit;
    }

    @Getter @Setter
    @Context(format = Format.DDI, type = GridResponseDomainInMixedType.class)
    @Context(format = Format.LUNATIC, type = BodyCell.class)
    public static class DateCell extends TableCell {

        // Note: with current Lunatic-Model implementation, it is impossible to set min & max in this case.
        // TODO: issue sent to Lunatic-Model, come back here when the issue has been solved.

        @DDI("getResponseDomain().getRangeArray(0).getMinimumValue().getStringValue()")
        private String minValue;

        @DDI("getResponseDomain().getRangeArray(0).getMaximumValue().getStringValue()")
        private String maxValue;

        @DDI("getResponseDomain().getDateFieldFormat().getStringValue()")
        @Lunatic("setDateFormat(#param)")
        private String format;
    }

    @Getter @Setter
    @Context(format = Format.DDI, type = GridResponseDomainInMixedType.class)
    @Context(format = Format.LUNATIC, type = BodyCell.class)
    public static class UniqueChoiceCell extends TableCell {
        @DDI("getResponseDomain().getGenericOutputFormat().getStringValue().equals('radio-button') ? " +
                "T(fr.insee.eno.core.model.question.UniqueChoiceQuestion.DisplayFormat).RADIO : " +
                "getResponseDomain().getGenericOutputFormat().getStringValue().equals('checkbox') ? " +
                "T(fr.insee.eno.core.model.question.UniqueChoiceQuestion.DisplayFormat).CHECKBOX : " +
                "getResponseDomain().getGenericOutputFormat().getStringValue().equals('drop-down-list') ? " +
                "T(fr.insee.eno.core.model.question.UniqueChoiceQuestion.DisplayFormat).DROPDOWN : null")
        UniqueChoiceQuestion.DisplayFormat displayFormat;

        @DDI("#index.get(#this.getResponseDomain().getCodeListReference().getIDArray(0).getStringValue()).getCodeList()")
        @Lunatic("getOptions()")
        List<CodeItem> codeList = new ArrayList<>();
    }

}
