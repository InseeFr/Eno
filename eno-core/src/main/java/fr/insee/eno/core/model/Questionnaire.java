package fr.insee.eno.core.model;

import fr.insee.eno.core.annotations.DDI;
import instance33.DDIInstanceDocument;

import java.util.ArrayList;
import java.util.List;

/**
 *  = instance33.DDIInstanceDocument ?
 */
public class Questionnaire {


    private final List<Variable> variables=new ArrayList<>();

    /**
     *  = AbstractIdentifiableType.getType()
     *
     *  Questionnaire.id correspond à :
     *  - `getDDIInstance().getIDArray()[0].getStringValue()`
     *  - dans DDIInstanceDocument
     *
     */
    @DDI(contextType= DDIInstanceDocument.class, field="getDDIInstance().getIDArray()[0].getStringValue()")
    private String id;

}
