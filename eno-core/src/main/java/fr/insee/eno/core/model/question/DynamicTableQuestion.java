package fr.insee.eno.core.model.question;

import datacollection33.QuestionGridType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.lunatic.model.flat.RosterForLoop;
import fr.insee.lunatic.model.flat.Table;
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
public class DynamicTableQuestion extends MultipleResponseQuestion {

    public DynamicTableQuestion() {
        log.warn("Dynamic tables mapping is not implemented!");
    }

    /** Lunatic component type property.
     * This should be inserted by Lunatic-Model serializer later on. */
    @Lunatic(contextType = RosterForLoop.class,
            field = "setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    String lunaticComponentType = "ROSTER_FOR_LOOP";

    @DDI(contextType = QuestionGridType.class,
            field = "#index.get(#this.getGridDimensionList().?[#this.getRank().intValue() == 2].get(0)" +
                    ".getCodeDomain().getCodeListReference().getIDArray(0).getStringValue())")
    CodeList header;

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
            field = "getGridDimensionList().?[#this.getRank().intValue() == 1].get(0)" +
                    ".getRoster().getMinimumRequired()")
    BigInteger minLines;

    @DDI(contextType = QuestionGridType.class,
            field = "getGridDimensionList().?[#this.getRank().intValue() == 1].get(0)" +
                    ".getRoster().getMaximumAllowed()")
    BigInteger maxLines;

    @DDI(contextType = QuestionGridType.class,
            field = "getOutParameterList().![#this.getParameterNameArray(0).getStringArray(0).getStringValue()]")
    List<String> variableNames = new ArrayList<>();

    @DDI(contextType = QuestionGridType.class, field = "getStructuredMixedGridResponseDomain().getGridResponseDomainInMixedList()")
    List<TableCell> tableCells = new ArrayList<>();
}
