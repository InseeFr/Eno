package fr.insee.eno.core;

import fr.insee.eno.core.annotations.DDI;
import lombok.Getter;
import lombok.Setter;
import reusable33.AbstractIdentifiableType;

@Getter
@Setter
public abstract class FooAbstract {

    @DDI(contextType = AbstractIdentifiableType.class, field = "hello")
    int a;

}
