package fr.insee.eno.core.model;

import java.util.List;

public interface EnoObjectWithExpression {

    String getExpression();
    void setExpression(String expression);
    List<BindingReference> getBindingReferences();

}
