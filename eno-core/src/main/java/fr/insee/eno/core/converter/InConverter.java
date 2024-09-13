package fr.insee.eno.core.converter;

import fr.insee.eno.core.model.EnoObject;

/** Interface for conversion from input objects to Eno model objects. */
public interface InConverter {

    EnoObject convertToEno(Object inputObject, Class<?> enoType);

}
