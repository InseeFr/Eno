package fr.insee.eno.core.model.question;

import datacollection33.QuestionGridType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * In DDI a table question is modeled as such:
 * - one code list for the header content
 * - one code list for the left column content
 * - a list of response domain element (one for each cell of the table)
 * </p><p>
 * In Lunatic, the modeling is significantly different:
 * - a list of HeaderType which is the header (and is optional)
 * - a list of BodyLine, one BodyLine is one row, and a BodyLine contains a list of BodyCell which are the cells of the line
 * </p><p>
 * This class is designed to be close to DDI modeling.
 * Header and left column are code lists. The left hand corner cell is in neither of these two
 * (so it is implicitly an empty cell).
 * Code lists can have hierarchical codes, so header / left column do not necessarily represent a single line / column.
 * Lunatic cells have 'colspan' / 'rowspan' attributes to handle this.
 * The conversion of tables to Lunatic is not done via annotations,
 * but mostly in a dedicated class that carries the logic of this conversion.
 * </p>
 */
@Getter
@Setter
@Context(format = Format.DDI, type = QuestionGridType.class)
@Context(format = Format.LUNATIC, type = Table.class)
public class TableQuestion extends MultipleResponseQuestion {

    /** Parameter that exists in Lunatic but that has a fixed value for now. */
    @Lunatic("setPositioning(#param)")
    private final String positioning = "HORIZONTAL";

    /** Lunatic component type property.
     * This should be inserted by Lunatic-Model serializer later on. */
    @Lunatic("setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    String lunaticComponentType = "TABLE";

    /** Reference of the code list that contains header info. */
    @DDI("getGridDimensionList().?[#this.getRank().intValue() == 2].get(0)" +
            ".getCodeDomain().getCodeListReference().getIDArray(0).getStringValue()")
    String headerCodeListReference;

    /** Code list that contains header info.
     * In DDI, inserted here through a processing. */
    CodeList header;

    /** Code list that contains header info. */
    @DDI("getGridDimensionList().?[#this.getRank().intValue() == 1].get(0)" +
            ".getCodeDomain().getCodeListReference().getIDArray(0).getStringValue()")
    String leftColumnCodeListReference;

    /** Code list that contains left column info.
     * In DDI, inserted here through a processing. */
    CodeList leftColumn;

    /** Considering that out parameters are sorted in the same order as GridResponseDomainInMixed objects in DDI. */
    @DDI("getOutParameterList().![#this.getParameterNameArray(0).getStringArray(0).getStringValue()]")
    List<String> variableNames = new ArrayList<>();

    /** Table cells. */
    @DDI("getStructuredMixedGridResponseDomain().getGridResponseDomainInMixedList()")
    List<TableCell> tableCells = new ArrayList<>();

}
