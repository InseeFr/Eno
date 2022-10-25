package fr.insee.eno.core.model.question;

import datacollection33.QuestionGridType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.CodeList;
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
 * - a list of BodyType, one BodyType is one row, and a BodyType contains a list of BodyLine which are the cells of the line
 * </p><p>
 * This class is designed to be close to DDI modeling.
 * Header and left column are code lists. The left hand corner cell is in neither of these two (so it is implicitly an empty cell).
 * The conversion of tables to Lunatic is not done via annotations,
 * but mostly in a dedicated class that carries the logic of this conversion.
 * </p>
 */
@Getter
@Setter
public class TableQuestion extends MultipleResponseQuestion {

    /** Parameter that exists in Lunatic but that has a fixed value for now. */
    @Lunatic(contextType = Table.class, field = "setPositioning(#param)")
    private final String positioning = "HORIZONTAL";

    /** Code list that contain header info. */
    @DDI(contextType = QuestionGridType.class,
            field = "#index.get(#this.getGridDimensionList().?[#this.getRank().intValue() == 2].get(0)" +
                    ".getCodeDomain().getCodeListReference().getIDArray(0).getStringValue())")
    CodeList header;

    @DDI(contextType = QuestionGridType.class,
            field = "#index.get(#this.getGridDimensionList().?[#this.getRank().intValue() == 1].get(0)" +
                    ".getCodeDomain().getCodeListReference().getIDArray(0).getStringValue())")
    CodeList leftColumn;

    /** Considering that out parameters are sorted in the same order as GridResponseDomainInMixed objects in DDI. */
    @DDI(contextType = QuestionGridType.class,
            field = "getOutParameterList().![#this.getParameterNameArray(0).getStringArray(0).getStringValue()]")
    List<String> variableNames = new ArrayList<>();

    /** Table cells. */
    @DDI(contextType = QuestionGridType.class, field = "getStructuredMixedGridResponseDomain().getGridResponseDomainInMixedList()")
    List<TableCell> tableCells = new ArrayList<>();

}
