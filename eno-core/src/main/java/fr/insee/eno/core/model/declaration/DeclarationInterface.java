package fr.insee.eno.core.model.declaration;

import fr.insee.eno.core.model.mode.Mode;
import fr.insee.eno.core.model.label.DynamicLabel;

import java.util.List;

/** Interface to use common properties of Declaration and Instruction e.g. in during processing. */
public interface DeclarationInterface {

    String getId();
    String getDeclarationType(); //TODO: maybe an enum instead of string would be appropriated here (see comments in implementations)
    DynamicLabel getLabel();
    String getPosition();
    List<Mode> getModes();

}
