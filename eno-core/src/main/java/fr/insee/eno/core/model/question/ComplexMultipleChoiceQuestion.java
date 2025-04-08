package fr.insee.eno.core.model.question;

import fr.insee.ddi.lifecycle33.datacollection.QuestionGridType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.navigation.Binding;
import fr.insee.eno.core.model.question.table.ResponseCell;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.Table;
import fr.insee.pogues.model.QuestionType;
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
@Context(format = Format.POGUES, type = QuestionType.class)
@Context(format = Format.DDI, type = QuestionGridType.class)
@Context(format = Format.LUNATIC, type = Table.class)
public class ComplexMultipleChoiceQuestion extends MultipleResponseQuestion {

    /** Lunatic component type property.
     * This should be inserted by Lunatic-Model serializer later on. */
    @Lunatic("setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    String lunaticComponentType = "TABLE";

    /** Parameter that exists in Lunatic but that has a fixed value for now. */
    @Lunatic("setPositioning(#param)")
    private String positioning = "HORIZONTAL";

    @Pogues("getResponseStructure().getDimension().getFirst().getCodeListReference()")
    @DDI("#this.getGridDimensionList().?[#this.getRank().intValue() == 1].get(0)" +
            ".getCodeDomain().getCodeListReference().getIDArray(0).getStringValue()")
    String leftColumnCodeListReference;

    /** In Pogues and DDI, the code list object is inserted in a processing step.
     * In Lunatic the code list is used in a table processing step. */
    CodeList leftColumn;

    /** Considering that out parameters are sorted in the same order as GridResponseDomainInMixed objects in DDI. */
    @Pogues("getResponse().![#poguesIndex.get(#this.getCollectedVariableReference()).getName()]")
    @DDI("getOutParameterList().![#this.getParameterNameArray(0).getStringArray(0).getStringValue()]")
    List<String> variableNames = new ArrayList<>();

    /** Only used in DDI. */
    @DDI("getBindingArray()")
    List<Binding> bindings = new ArrayList<>();

    @Pogues("getResponse()")
    @DDI("getStructuredMixedGridResponseDomain().getGridResponseDomainInMixedList()")
    List<ResponseCell> responseCells = new ArrayList<>();

}
