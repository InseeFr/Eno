package fr.insee.eno.core.model.question;

import datacollection33.QuestionGridType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.navigation.Binding;
import fr.insee.eno.core.model.question.table.TableCell;
import fr.insee.lunatic.model.flat.RosterForLoop;
import fr.insee.lunatic.model.flat.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * "Complex" multiple choice question.
 * Each modality is itself a list of possibilities
 * (in this way the multiple choice question looks like a combination of unique choice questions).
 * In DDI, it corresponds to a QuestionGrid.
 * In Lunatic, it corresponds to a Table component.
 */
@Getter
@Setter
public  class ComplexMultipleChoiceQuestion extends MultipleResponseQuestion {

    /** Lunatic component type property.
     * This should be inserted by Lunatic-Model serializer later on. */
    @Lunatic(contextType = Table.class,
            field = "setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    String lunaticComponentType = "TABLE";

    /** Parameter that exists in Lunatic but that has a fixed value for now. */
    @Lunatic(contextType = Table.class, field = "setPositioning(#param)")
    private String positioning = "HORIZONTAL";

    @DDI(contextType = QuestionGridType.class,
            field = "getResponseDomain()?.getResponseCardinality()?.getMinimumResponses() != null ? " +
                    "getResponseDomain().getResponseCardinality().getMinimumResponses().intValue() > 0 : false")
    @Lunatic(contextType = {Table.class, RosterForLoop.class},
            field = "setMandatory(#param)")
    boolean mandatory;

    @DDI(contextType = QuestionGridType.class,
            field = "#index.get(#this.getGridDimensionList().?[#this.getRank().intValue() == 1].get(0)" +
                    ".getCodeDomain().getCodeListReference().getIDArray(0).getStringValue())")
    CodeList headers;

    /** Considering that out parameters are sorted in the same order as GridResponseDomainInMixed objects in DDI. */
    @DDI(contextType = QuestionGridType.class,
            field = "getOutParameterList().![#this.getParameterNameArray(0).getStringArray(0).getStringValue()]")
    List<String> variableNames = new ArrayList<>();

    @DDI(contextType = QuestionGridType.class,
            field = "getBindingArray()")
    List<Binding> bindings = new ArrayList<>();

    @DDI(contextType = QuestionGridType.class, field = "getStructuredMixedGridResponseDomain().getGridResponseDomainInMixedList()")
    List<TableCell> tableCells = new ArrayList<>();
}
