package fr.insee.eno.core.tmp;

import fr.insee.lunatic.model.flat.ComponentType;
import fr.insee.lunatic.model.flat.LabelType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class PairwiseLinksTmp extends ComponentType {

    @Getter @Setter
    LabelType xAxisIterations;

    @Getter @Setter
    LabelType yAxisIterations;

    // TODO: why is it a list since it is supposed to contain only 1 Dropdown component?
    List<ComponentType> components;

    public List<ComponentType> getComponents() {
        if (components == null)
            components = new ArrayList<>();
        return components;
    }

}