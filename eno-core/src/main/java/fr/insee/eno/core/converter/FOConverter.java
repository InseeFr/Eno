package fr.insee.eno.core.converter;

import fr.insee.eno.core.exceptions.technical.ConversionException;
import fr.insee.eno.core.model.label.QuestionnaireLabel;
import fr.insee.eno.core.model.question.*;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.lunatic.model.flat.*;
import lombok.extern.slf4j.Slf4j;
import org.w3.x1999.xsl.format.TitleDocument;
import org.w3.x1999.xsl.format.impl.TitleDocumentImpl;
import org.w3c.dom.Document;

@Slf4j
public class FOConverter {

    private FOConverter() {}

    /**
     * Return a FO instance type that corresponds to the given Eno object.
     * @param enoObject An object from the Eno model.
     * @return An instance from Lunatic flat model.
     * @throws IllegalArgumentException if the given object is not in package 'fr.insee.eno.core.model'.
     */
    public static Object instantiateFromEnoObject(Object enoObject) {
        //
        if (! enoObject.getClass().getPackageName().startsWith("fr.insee.eno.core.model"))
            throw new IllegalArgumentException("Not an Eno object.");
        if (enoObject instanceof SingleResponseQuestion singleResponseQuestion)
            return instantiateFrom(singleResponseQuestion);
        if (enoObject instanceof MultipleResponseQuestion multipleResponseQuestion)
            return instantiateFrom(multipleResponseQuestion);
        if(enoObject instanceof QuestionnaireLabel) {
            return TitleDocument.Title.Factory.newInstance();
        }
        throw new ConversionException(unimplementedMessage(enoObject));
    }

    private static Object instantiateFrom(SingleResponseQuestion enoQuestion) {
        if (enoQuestion instanceof TextQuestion textQuestion) {
            return textComponentConversion(textQuestion);
        }
        if (enoQuestion instanceof UniqueChoiceQuestion uniqueChoiceQuestion) {
            return ucqComponentConversion(enoQuestion, uniqueChoiceQuestion);
        }
        throw new ConversionException(unimplementedMessage(enoQuestion));
    }

    private static ComponentType textComponentConversion(TextQuestion textQuestion) {
        throw new ConversionException(unimplementedMessage(textQuestion));
    }

    private static Object ucqComponentConversion(SingleResponseQuestion enoQuestion, UniqueChoiceQuestion uniqueChoiceQuestion) {
        throw new ConversionException(unimplementedMessage(uniqueChoiceQuestion));
    }

    private static Object instantiateFrom(Variable enoVariable) {
        throw new ConversionException(unimplementedMessage(enoVariable));
    }

    private static Object instantiateFrom(MultipleResponseQuestion enoQuestion) {
        throw new ConversionException(unimplementedMessage(enoQuestion));
    }

    private static String unimplementedMessage(Object enoObject) {
        return "Fo conversion for Eno type " + enoObject.getClass() + " not implemented.";
    }
}
