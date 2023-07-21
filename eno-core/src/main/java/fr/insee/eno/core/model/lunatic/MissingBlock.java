package fr.insee.eno.core.model.lunatic;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MissingBlock {
    private String missingName;
    private List<String> names;
}
