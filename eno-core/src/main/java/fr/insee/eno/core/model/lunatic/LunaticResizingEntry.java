package fr.insee.eno.core.model.lunatic;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;

@Data
@AllArgsConstructor
public class LunaticResizingEntry implements LunaticResizingInterface {
    /** Resizing variable. */
    private String name;
    /** Resizing expression. */
    private String size;
    /** Resized variables. */
    private Collection<String> variables;

}