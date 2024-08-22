package fr.insee.eno.treatments.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.insee.lunatic.model.flat.SuggesterOrder;
import lombok.Getter;

public record EnoSuggesterOrder(
        @JsonProperty(value = "field", required = true) String field,
        @JsonProperty(value = "type", required = true) String type) {

    /**
     * @param enoOrder EnoSuggesterOrder object to convert
     * @return the corresponding lunatic model object
     */
    public static SuggesterOrder toLunaticModel(EnoSuggesterOrder enoOrder) {
        if (enoOrder == null) {
            return null;
        }
        SuggesterOrder order = new SuggesterOrder();
        order.setField(enoOrder.field());
        order.setType(enoOrder.type());
        return order;
    }
}
