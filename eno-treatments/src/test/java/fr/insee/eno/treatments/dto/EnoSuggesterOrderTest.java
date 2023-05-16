package fr.insee.eno.treatments.dto;

import fr.insee.lunatic.model.flat.SuggesterOrder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EnoSuggesterOrderTest {

    @Test
    void whenConvertingToLunaticMappingIsCorrect() {
        EnoSuggesterOrder enoOrder = new EnoSuggesterOrder("field", "type");
        SuggesterOrder order = EnoSuggesterOrder.toLunaticModel(enoOrder);
        assertEquals(order.getField(), enoOrder.getField());
        assertEquals(order.getType(), enoOrder.getType());
    }

    @Test
    void whenConvertingToLunaticMappingIfNullParameterReturnNull() {
        assertNull(EnoSuggesterOrder.toLunaticModel(null));
    }

}
