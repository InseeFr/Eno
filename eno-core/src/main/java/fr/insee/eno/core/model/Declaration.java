package fr.insee.eno.core.model;

import datacollection33.StatementItemType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.lunatic.model.flat.DeclarationType;
import lombok.Getter;
import lombok.Setter;

/** Text displayed before a question or sequence. */
@Getter
@Setter
public class Declaration extends EnoObject {

    //@DDI(contextType = StatementItemType.class, field = "") //TODO
    String id;

    String label;

    @Lunatic(contextType = DeclarationType.class,
            field = "setDeclarationType(T(fr.insee.lunatic.model.flat.DeclarationPositionEnum).valueOf(#param))")
    String position = "BEFORE_QUESTION_TEXT";

}
