package fr.insee.eno.core.model.question;

import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.lunatic.model.flat.CellsType;

public class TableCell extends EnoObject {

    @Lunatic(contextType = CellsType.class, field = "setId(#param)")
    private String id;

}
