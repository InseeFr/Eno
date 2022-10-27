package fr.insee.eno.core.model;

import fr.insee.eno.core.model.calculated.CalculatedExpression;

public interface EnoObjectWithExpression {

    CalculatedExpression getExpression();

}
