package fr.insee.eno.core.sandbox;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.reference.EnoIndex;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MockitoTests {

    @Test
    void helloMockito() {
        String someId = "foo-id";
        EnoIndex enoIndex = Mockito.mock(EnoIndex.class);
        Mockito.when(enoIndex.get(someId)).thenReturn(new EnoQuestionnaire());
        assertTrue(enoIndex.get(someId) instanceof EnoQuestionnaire);
    }

}
