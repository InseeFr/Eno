package fr.insee.eno.core.model.question;

import datacollection33.QuestionGridType;
import fr.insee.eno.core.annotations.Contexts;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.navigation.Binding;
import fr.insee.eno.core.model.question.table.TableCell;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.RosterForLoop;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Eno model class to represent dynamic table questions.
 * A dynamic table question is a table question where lines can be dynamically added/removed during data collection.
 * In DDI, it corresponds to a QuestionGrid similar to table questions (to be verified).
 * In Lunatic, it corresponds to the RosterForLoop component (to be verified).
 */
@Getter
@Setter
@Slf4j
@Contexts.Context(format = Format.DDI, type = QuestionGridType.class)
@Contexts.Context(format = Format.LUNATIC, type = RosterForLoop.class)
public class DynamicTableQuestion extends MultipleResponseQuestion {

    public DynamicTableQuestion() {
        log.warn("Dynamic tables mapping is not implemented!");
    }

    /** Lunatic component type property.
     * This should be inserted by Lunatic-Model serializer later on. */
    @Lunatic("setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    String lunaticComponentType = "ROSTER_FOR_LOOP";

    @DDI("#index.get(#this.getGridDimensionList().?[#this.getRank().intValue() == 2].get(0)" +
            ".getCodeDomain().getCodeListReference().getIDArray(0).getStringValue())")
    CodeList header;

    /** Parameter that exists in Lunatic but that has a fixed value for now. */
    @Lunatic("setPositioning(#param)")
    private String positioning = "HORIZONTAL";

    @DDI("getResponseDomain()?.getResponseCardinality()?.getMinimumResponses() != null ? " +
            "getResponseDomain().getResponseCardinality().getMinimumResponses().intValue() > 0 : false")
    @Lunatic("setMandatory(#param)")
    boolean mandatory;

    @DDI("getGridDimensionList().?[#this.getRank().intValue() == 1].get(0)" +
            ".getRoster().getMinimumRequired()")
    BigInteger minLines;

    @DDI("getGridDimensionList().?[#this.getRank().intValue() == 1].get(0)" +
            ".getRoster().getMaximumAllowed()")
    BigInteger maxLines;

    @DDI("getOutParameterList().![#this.getParameterNameArray(0).getStringArray(0).getStringValue()]")
    List<String> variableNames = new ArrayList<>();

    @DDI("getBindingArray()")
    List<Binding> bindings = new ArrayList<>();

    @DDI("getStructuredMixedGridResponseDomain().getGridResponseDomainInMixedList()")
    List<TableCell> tableCells = new ArrayList<>();
}
