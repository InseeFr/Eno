package fr.insee.eno.core.model.navigation;

import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Eno model object for the articulation component.
 * The articulation is a sort of recap component to be displayed when the interviewer changes.
 */
@Getter
@Context(format = Format.POGUES, type = fr.insee.pogues.model.Articulation.class)
@Context(format = Format.LUNATIC, type = fr.insee.lunatic.model.flat.articulation.Articulation.class)
public class EnoArticulation extends EnoObject {

    @Getter
    @Setter
    @Context(format = Format.POGUES, type = fr.insee.pogues.model.Item.class)
    @Context(format = Format.LUNATIC, type = fr.insee.lunatic.model.flat.articulation.ArticulationItem.class)
    public static class Item extends EnoObject {

        /** Displayed label of the item. */
        @Pogues("getLabel()")
        @Lunatic("setLabel(#param)")
        private String label;

        /** Lunatic prop to indicate how should be interpreted the value.
         * Note: value defined in Pogues matches the enum value in Lunatic. */
        @Pogues("getType().value()")
        @Lunatic("setType(T(fr.insee.lunatic.model.flat.LabelTypeEnum).fromValue(#param))")
        private String type;

        /** VTL expression which returns the value of this item. */
        @Pogues("T(fr.insee.eno.core.model.calculated.CalculatedExpression).removeSurroundingDollarSigns(getValue())")
        @Lunatic("setValue(#param)")
        private String value;
    }

    /** Items to be shown in the articulation component. */
    @Pogues("getItems()")
    @Lunatic("getItems()")
    private final List<Item> items = new ArrayList<>();

}
