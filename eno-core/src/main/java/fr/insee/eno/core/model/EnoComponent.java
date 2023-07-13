package fr.insee.eno.core.model;

import fr.insee.eno.core.model.declaration.Declaration;
import fr.insee.eno.core.model.declaration.Instruction;
import fr.insee.eno.core.model.navigation.Control;

import java.util.List;

/** Interface to use common methods of sequences and questions objects. */
public interface EnoComponent {

    String getId();
    List<Declaration> getDeclarations();
    List<Instruction> getInstructions();
    List<Control> getControls();

}
