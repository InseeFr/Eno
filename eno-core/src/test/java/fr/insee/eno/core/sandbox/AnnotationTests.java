package fr.insee.eno.core.sandbox;

import fr.insee.eno.core.annotations.Contexts;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.model.EnoQuestionnaire;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AnnotationTests {

    @Test
    void getRepeatableAnnotation() {
        Contexts contexts = EnoQuestionnaire.class.getAnnotation(Contexts.class);
        assertNotNull(contexts);
        assertEquals(Format.LUNATIC, contexts.value()[2].format());
    }

    @Test
    void getRepeatableAnnotation_better() {
        Contexts.Context[] contextArray =  EnoQuestionnaire.class.getAnnotationsByType(Contexts.Context.class);
        assertNotNull(contextArray);
        assertEquals(Format.LUNATIC, contextArray[2].format());
    }

}
