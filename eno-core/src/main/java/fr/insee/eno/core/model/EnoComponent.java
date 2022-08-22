package fr.insee.eno.core.model;

import java.util.List;

/** Interface to use common methods of sequences and questions objects. */
public interface EnoComponent {

    String getId();
    List<Declaration> getDeclarations();
    List<Instruction> getInstructions();
    List<Control> getControls();
    void setFilter(Filter filter);

}
