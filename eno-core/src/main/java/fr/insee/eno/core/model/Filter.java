package fr.insee.eno.core.model;

import datacollection33.IfThenElseTextType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.lunatic.model.flat.ComponentType;

public class Filter {


    private String componentReference;

    @DDI(contextType = IfThenElseTextType.class, field = "")
    private String expression;
}
