package fr.insee.eno.core.mapping.in.pogues;

import fr.insee.eno.core.PoguesToEno;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.declaration.Declaration;
import fr.insee.eno.core.model.mode.Mode;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import fr.insee.pogues.model.Questionnaire;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeclarationTest {

   private EnoIndex index;

   @BeforeAll
    void init() throws ParsingException {
       InputStream poguesStream = this.getClass().getClassLoader().getResourceAsStream("integration/pogues/pogues-declarations.json");
      EnoQuestionnaire questionnaire = new PoguesToEno().transform(poguesStream,
                EnoParameters.of(EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.PROCESS));
       index = questionnaire.getIndex();
   }
   @Test
   void parseText() {
       Question question1 = (Question) index.get("lk6zkkfr");
       assertEquals(3, question1.getDeclarations().size());
       assertEquals(3, question1.getInstructions().size());
       Declaration declaration1 = (Declaration) index.get("lk706b3k");
       assertEquals("\"Static label 'Aide' before the question\"", declaration1.getLabel().getValue());
       assertEquals(List.of(Mode.CAPI,Mode.CATI, Mode.CAWI, Mode.PAPI),declaration1.getModes());
    }
}

//        @Test
//        void unitTest() throws PoguesDeserializationException {
//            // Given
//            InputStream poguesStream = this.getClass().getClassLoader().getResourceAsStream("integration/pogues/pogues-declarations.json");
//            if (poguesStream == null) {
//                throw new IllegalArgumentException("Le fichier JSON des déclarations est introuvable.");
//            }
//
//            // When
//            PoguesMapper poguesMapper = new PoguesMapper();
//            EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
//            Questionnaire poguesQuestionnaire = PoguesDeserializer.deserialize(poguesStream);
//            poguesMapper.mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);
//
//            // Then
//            EnoIndex index = enoQuestionnaire.getIndex();
//
//            //
////            EnoObject declarationObject = index.get("lk706b3k");
////            assertInstanceOf(Declaration.class, declarationObject);
////            Declaration declaration = (Declaration) declarationObject;
////            assertEquals("\"Static label 'Aide' before the question\"", declaration.getLabel().getValue());
//
//            //
//
//            Question question1 = (Question) index.get("lk6zkkfr");
//            assertEquals(3, question1.getDeclarations().size());
//            assertEquals(3, question1.getInstructions().size());
//        }
//    }


