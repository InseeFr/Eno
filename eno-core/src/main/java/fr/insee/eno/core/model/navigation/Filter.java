package fr.insee.eno.core.model.navigation;

import datacollection33.IfThenElseType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.EnoObjectWithExpression;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.sequence.ItemReference;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** Filter defined at the questionnaire level.
 * @see ComponentFilter for filter objects wihtin components. */
@Getter
@Setter
public class Filter extends EnoIdentifiableObject implements EnoObjectWithExpression {

    /** Lunatic filters doesn't have an identifier. */
    @DDI(contextType = IfThenElseType.class, field = "getIDArray(0).getStringValue()")
    private String id;

    /** Filter expression. */
    @DDI(contextType = IfThenElseType.class, field = "getIfCondition().getCommandArray(0)")
    private CalculatedExpression expression;

    /** Same principle as sequence items list in sequence objects. */
    @DDI(contextType = IfThenElseType.class,
            field = "#index.get(#this.getThenConstructReference().getIDArray(0).getStringValue())" +
                    ".getControlConstructReferenceList()")
    private List<ItemReference> filterItems = new ArrayList<>();

    /** References of sequences, subsequences or/and questions that are in the scope of the filter.
     * In DDI, this property is filled by a processing using the "filterItems" property. */
    private final List<StructureItemReference> filterScope = new ArrayList<>();

}
