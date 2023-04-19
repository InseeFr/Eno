package fr.insee.eno.core.model.question;

import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.lunatic.model.flat.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class DynamicTableQuestion extends MultipleResponseQuestion {

    public DynamicTableQuestion() {
        log.warn("Dynamic tables mapping is not implemented!");
    }

    @Lunatic(contextType = Table.class,
            field = "setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    String lunaticComponentType = "TABLE";

}
