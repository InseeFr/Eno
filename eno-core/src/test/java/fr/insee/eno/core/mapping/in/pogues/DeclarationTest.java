package fr.insee.eno.core.mapping.in.pogues;

import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.declaration.Declaration;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import fr.insee.pogues.model.Questionnaire;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class DeclarationTest {

        @Test
        void unitTest() throws PoguesDeserializationException {
            // Given
            InputStream poguesStream = this.getClass().getClassLoader().getResourceAsStream("integration/pogues/pogues-declarations.json");
            if (poguesStream == null) {
                throw new IllegalArgumentException("Le fichier JSON des déclarations est introuvable.");
            }

            // When
            PoguesMapper poguesMapper = new PoguesMapper();
            EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
            Questionnaire poguesQuestionnaire = PoguesDeserializer.deserialize(poguesStream);
            poguesMapper.mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);

            // Then
            EnoIndex index = enoQuestionnaire.getIndex();

            //
//            EnoObject declarationObject = index.get("lk706b3k");
//            assertInstanceOf(Declaration.class, declarationObject);
//            Declaration declaration = (Declaration) declarationObject;
//            assertEquals("\"Static label 'Aide' before the question\"", declaration.getLabel().getValue());

            //

            Question question1 = (Question) index.get("lk6zkkfr");
            assertEquals(3, question1.getDeclarations().size());
            assertEquals(3, question1.getInstructions().size());
        }
    }


