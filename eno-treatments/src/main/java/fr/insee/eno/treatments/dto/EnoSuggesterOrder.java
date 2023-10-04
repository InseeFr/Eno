package fr.insee.eno.treatments.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.insee.lunatic.model.flat.SuggesterOrder;
import lombok.*;

@Data
public class EnoSuggesterOrder {

    private String field;
    private String type;

    @JsonCreator
    public EnoSuggesterOrder(@JsonProperty(value = "field", required = true) String field,
                             @JsonProperty(value = "type", required = true) String type) {
        this.field = field;
        this.type = type;
    }

    /**
     *
     * @param enoOrder EnoSuggesterOrder object to convert
     * @return the corresponding lunatic model object
     */
    public static SuggesterOrder toLunaticModel(EnoSuggesterOrder enoOrder) {
        if(enoOrder == null) {
            return null;
        }
        SuggesterOrder order = new SuggesterOrder();
        order.setField(enoOrder.getField());
        order.setType(enoOrder.getType());
        return order;
    }
}
