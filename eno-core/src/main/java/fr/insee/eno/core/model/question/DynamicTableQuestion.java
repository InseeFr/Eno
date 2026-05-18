package fr.insee.eno.core.model.question;

import fr.insee.ddi.lifecycle33.datacollection.GridDimensionType;
import fr.insee.ddi.lifecycle33.datacollection.QuestionGridType;
import fr.insee.ddi.lifecycle33.reusable.CommandCodeType;
import fr.insee.ddi.lifecycle33.reusable.CommandType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.exceptions.business.IllegalDDIElementException;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.navigation.Binding;
import fr.insee.eno.core.model.question.table.CellLabel;
import fr.insee.eno.core.model.question.table.NoDataCell;
import fr.insee.eno.core.model.question.table.ResponseCell;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.RosterForLoop;
import fr.insee.pogues.model.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Eno model class to represent dynamic table questions.
 * A dynamic table question is a table question where lines can be dynamically added/removed during data collection.
 * In DDI, it corresponds to a QuestionGrid similar to table questions.
 * In Lunatic, it corresponds to the RosterForLoop component.
 */
@Getter
@Setter
@Context(format = Format.POGUES, type = QuestionType.class)
@Context(format = Format.DDI, type = QuestionGridType.class)
@Context(format = Format.LUNATIC, type = RosterForLoop.class)
public class DynamicTableQuestion extends MultipleResponseQuestion implements EnoTable {

    @DDI("#this.getGridDimensionList().?[#this.getRank().intValue() == 2].get(0)" +
            ".getCodeDomain().getCodeListReference().getIDArray(0).getStringValue()")
    String headerCodeListReference;

    CodeList header;

    /** Maximum number of lines of the dynamic table.
     * Note: In DDI, for some reason, if this information is missing in the xml file, the default value is 1.
     * In Lunatic, this property is set in a processing class.
     * @see fr.insee.eno.core.processing.out.steps.lunatic.table.DynamicTableQuestionProcessing */
    @DDI("getGridDimensionList().?[#this.getRank().intValue() == 1].get(0)" +
            ".getRoster().getMinimumRequired()")
    BigInteger minLines;

    /** Minimum number of lines of the dynamic table.
     * In Lunatic, this property is set in a processing class.
     * @see fr.insee.eno.core.processing.out.steps.lunatic.table.DynamicTableQuestionProcessing */
    @DDI("getGridDimensionList().?[#this.getRank().intValue() == 1].get(0)" +
            ".getRoster().getMaximumAllowed()")
    BigInteger maxLines;

    /** VTL expression that defines the minimum number of lines of the dynamic table.
     * In DDI, this information cannot be implemented so it is missing. */
    @Pogues("T(fr.insee.eno.core.model.question.DynamicTableQuestion).mapPoguesMin(#this)")
    CalculatedExpression minSizeExpression;

    /** VTL expression that defines the maximum number of lines of the dynamic table. */
    @Pogues("T(fr.insee.eno.core.model.question.DynamicTableQuestion).mapPoguesMax(#this)")
    @DDI("T(fr.insee.eno.core.model.question.DynamicTableQuestion).mapDDISizeExpression(#this)")
    CalculatedExpression maxSizeExpression;

    @DDI("getOutParameterList().![#this.getParameterNameArray(0).getStringArray(0).getStringValue()]")
    List<String> variableNames = new ArrayList<>();

    @DDI("getBindingArray()")
    List<Binding> bindings = new ArrayList<>();

    @DDI("getStructuredMixedGridResponseDomain().getGridResponseDomainInMixedList()")
    @Pogues("T(fr.insee.eno.core.utils.PoguesUtils).getPoguesTableResponseCells(#this)")
    List<ResponseCell> responseCells = new ArrayList<>();

    /** No data cells */
    @DDI("getStructuredMixedGridResponseDomain().getNoDataByDefinitionList()")
    @Pogues("T(fr.insee.eno.core.utils.PoguesUtils).getPoguesTableNoDataCells(#this)")
    List<NoDataCell> noDataCells = new ArrayList<>();

    /** Labels for cells that contain no response data or that have a conditional label.
     * These labels are mapped here in DDI but are then moved within cell objects through a processing. */
    @DDI("getCellLabelList()")
    List<CellLabel> cellLabels = new ArrayList<>();

    public static CommandType mapDDISizeExpression(QuestionGridType ddiDynamicTableQuestion) {
        GridDimensionType rank1Dimension = checkRank1Dimension(ddiDynamicTableQuestion);
        CommandCodeType conditionForContinuation = rank1Dimension.getRoster().getConditionForContinuation();
        if (conditionForContinuation == null)
            return null;
        return conditionForContinuation.getCommandArray(0);
    }

    private static GridDimensionType checkRank1Dimension(QuestionGridType ddiDynamicTableQuestion) {
        return ddiDynamicTableQuestion.getGridDimensionList().stream()
                .filter(gridDimensionType -> BigInteger.ONE.equals(gridDimensionType.getRank()))
                .findAny()
                .orElseThrow(() -> new IllegalDDIElementException(
                        "DDI dynamic table question '" + ddiDynamicTableQuestion.getIDArray(0).getStringValue() +
                                "' has no rank 1 dimension."));
    }

    public static TypedValueType mapPoguesMin(QuestionType poguesTableQuestion) {
        DimensionType firstDimension = poguesTableQuestion.getResponseStructure().getDimension().getFirst();
        String dynamic = firstDimension.getDynamic();
        return switch (dynamic) {
            case "DYNAMIC_FIXED" -> firstDimension.getSize();
            case "DYNAMIC" -> firstDimension.getMinimum();
            case "DYNAMIC_LENGTH" -> convert(firstDimension.getMinLines()); // deprecated
            case "FIXED_LENGTH" -> convert(firstDimension.getFixedLength()); // deprecated
            default -> throw new IllegalStateException("Unexpected Pogues table dimension type: " + dynamic);
        };
    }
    public static TypedValueType mapPoguesMax(QuestionType poguesTableQuestion) {
        DimensionType firstDimension = poguesTableQuestion.getResponseStructure().getDimension().getFirst();
        String dynamic = firstDimension.getDynamic();
        return switch (dynamic) {
            case "DYNAMIC_FIXED" -> firstDimension.getSize();
            case "DYNAMIC" -> firstDimension.getMaximum();
            case "DYNAMIC_LENGTH" -> convert(firstDimension.getMaxLines()); // deprecated
            case "FIXED_LENGTH" -> convert(firstDimension.getFixedLength()); // deprecated
            default -> throw new IllegalStateException("Unexpected Pogues table dimension type: " + dynamic);
        };
    }
    private static TypedValueType convert(ExpressionType poguesExpression) {
        if (poguesExpression == null)
            return null;
        TypedValueType res = new TypedValueType();
        res.setValue(poguesExpression.getValue());
        res.setType(ValueTypeEnum.VTL);
        return res;
    }
    private static TypedValueType convert(BigInteger value) {
        if (value == null)
            return null;
        TypedValueType res = new TypedValueType();
        res.setValue(value.toString());
        res.setType(ValueTypeEnum.NUMBER);
        return res;
    }

}
