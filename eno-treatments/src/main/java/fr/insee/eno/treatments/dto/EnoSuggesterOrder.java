//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.3.2 
// Voir <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2023.05.11 à 10:17:07 AM CEST 
//


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
