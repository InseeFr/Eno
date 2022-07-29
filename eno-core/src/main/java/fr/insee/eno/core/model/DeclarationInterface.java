package fr.insee.eno.core.model;

import java.util.List;

/** Interface to use common properties of Declaration and Instruction e.g. in during processing. */
public interface DeclarationInterface {

    String getId();
    String getDeclarationType(); //TODO: maybe an enum instead of string would be appropriated here (see comments in implementations)
    String getLabel();
    String getPosition();
    List<Mode> getModes();

}
