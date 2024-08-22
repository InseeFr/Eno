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
        assertEquals(order.getField(), enoOrder.field());
        assertEquals(order.getType(), enoOrder.type());
    }

    @Test
    void whenConvertingToLunaticMappingIfNullParameterReturnNull() {
        assertNull(EnoSuggesterOrder.toLunaticModel(null));
    }

}
