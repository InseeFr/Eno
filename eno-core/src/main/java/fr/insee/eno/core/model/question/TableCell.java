package fr.insee.eno.core.model.question;

import datacollection33.GridResponseDomainInMixedType;
import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Format;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.lunatic.model.flat.BodyLine;
import fr.insee.lunatic.model.flat.Datepicker;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static fr.insee.eno.core.annotations.Contexts.Context;

/** A TableCell object is the content of a table.
 * A cell is neither part of the header nor of the left column. */
@Context(format = Format.DDI, type = GridResponseDomainInMixedType.class)
@Getter
@Setter
public abstract class TableCell extends EnoObject {

    @DDI(contextType = GridResponseDomainInMixedType.class,
            field = "T(java.lang.Integer).parseInt(getGridAttachmentArray(0).getCellCoordinatesAsDefinedArray(0).getSelectDimensionList()" +
                    ".?[#this.getRank().intValue() == 1].get(0).getRangeMinimum())") // range maximum is the same in Insee DDI
    int rowNumber;

    @DDI(contextType = GridResponseDomainInMixedType.class,
            field = "T(java.lang.Integer).parseInt(getGridAttachmentArray(0).getCellCoordinatesAsDefinedArray(0).getSelectDimensionList()" +
                    ".?[#this.getRank().intValue() == 2].get(0).getRangeMinimum())") // range maximum is the same in Insee DDI
    int columnNumber;

    // TODO: refactor DDI response domain mapping in questions and table cells

    @Getter @Setter
    public static class BooleanCell extends TableCell {}

    @Getter @Setter
    public static class TextCell extends TableCell {
        @DDI(contextType = GridResponseDomainInMixedType.class, field = "getResponseDomain().getMaxLength().intValue()")
        @Lunatic(contextType = BodyLine.class, field = "setMaxLength(#param)")
        BigInteger maxLength;
    }

    @Getter @Setter
    public static class NumericCell extends TableCell {
        @DDI(contextType = QuestionItemType.class,
                field = "getResponseDomain()?.getNumberRangeList()?.get(0)?.getLow()?.getStringValue() != null ? " +
                        "T(java.lang.Double).valueOf(getResponseDomain().getNumberRangeArray(0).getLow().getStringValue()) : null")
        @Lunatic(contextType = BodyLine.class, field = "setMin(#param)")
        double minValue;

        @DDI(contextType = QuestionItemType.class,
                field = "getResponseDomain()?.getNumberRangeList()?.get(0)?.getLow()?.getStringValue() != null ? " +
                        "T(java.lang.Double).valueOf(getResponseDomain().getNumberRangeArray(0).getLow().getStringValue()) : null")
        @Lunatic(contextType = BodyLine.class, field = "setMax(#param)")
        double maxValue;

        @DDI(contextType = QuestionItemType.class,
                field = "getResponseDomain()?.getDecimalPositions() ?: T(java.math.BigInteger).valueOf('0')")
        @Lunatic(contextType = BodyLine.class, field = "setDecimals(#param)")
        BigInteger numberOfDecimals;

        /** Unit not accessible here in DDI.
         * In Lunatic, the value is set there during the table processing. */
        String unit;
    }

    @Getter @Setter
    public static class DateCell extends TableCell {

        // Note: with current Lunatic-Model implementation, it is impossible to set min & max in this case.
        // TODO: issue sent to Lunatic-Model, come back here when the issue has been solved.

        @DDI(contextType = QuestionItemType.class, field = "getResponseDomain().getRangeArray(0).getMinimumValue().getStringValue()")
        private String minValue;

        @DDI(contextType = QuestionItemType.class, field = "getResponseDomain().getRangeArray(0).getMaximumValue().getStringValue()")
        private String maxValue;

        @DDI(contextType = QuestionItemType.class, field = "getResponseDomain().getDateFieldFormat().getStringValue()")
        @Lunatic(contextType = Datepicker.class, field = "setDateFormat(#param)")
        private String format;
    }

    @Getter @Setter
    public static class UniqueChoiceCell extends TableCell {
        @DDI(contextType = QuestionItemType.class,
                field = "getResponseDomain().getGenericOutputFormat().getStringValue().equals('radio-button') ? " +
                        "T(fr.insee.eno.core.model.question.UniqueChoiceQuestion.DisplayFormat).RADIO : " +
                        "getResponseDomain().getGenericOutputFormat().getStringValue().equals('checkbox') ? " +
                        "T(fr.insee.eno.core.model.question.UniqueChoiceQuestion.DisplayFormat).CHECKBOX : " +
                        "getResponseDomain().getGenericOutputFormat().getStringValue().equals('drop-down-list') ? " +
                        "T(fr.insee.eno.core.model.question.UniqueChoiceQuestion.DisplayFormat).DROPDOWN : null")
        UniqueChoiceQuestion.DisplayFormat displayFormat;

        @DDI(contextType = QuestionItemType.class,
                field = "#index.get(#this.getResponseDomain().getCodeListReference().getIDArray(0).getStringValue()).getCodeList()")
        @Lunatic(contextType = BodyLine.class, field = "getOptions()")
        List<CodeList.CodeItem> codeList = new ArrayList<>();
    }

}
