package fr.insee.eno.core.sandbox;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.reference.EnoIndex;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class MockitoTests {

    @Test
    public void helloMockito() {
        EnoIndex enoIndex = Mockito.mock(EnoIndex.class);
        Mockito.when(enoIndex.get("monId")).thenReturn(new EnoQuestionnaire());
        enoIndex.get("foo");
    }

}
