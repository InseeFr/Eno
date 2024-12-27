package fr.insee.eno.core.converter;

import fr.insee.eno.core.exceptions.technical.ConversionException;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.pogues.model.VariableType;

public class PoguesConverter implements InConverter {

    @Override
    public EnoObject convertToEno(Object poguesObject, Class<?> enoType) {
        if (poguesObject instanceof VariableType poguesVariable)
            return PoguesVariableConversion.instantiateFrom(poguesVariable);
        throw new ConversionException("Eno conversion for Pogues type " + poguesObject.getClass() + " not implemented.");
    }

}
