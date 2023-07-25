package fr.insee.eno.core.model.question;

import datacollection33.QuestionGridType;
import fr.insee.eno.core.annotations.Contexts;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.RosterForLoop;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

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

}
