package fr.insee.eno.core.model.question;

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

}
