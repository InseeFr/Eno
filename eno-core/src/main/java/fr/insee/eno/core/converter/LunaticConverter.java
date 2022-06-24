package fr.insee.eno.core.converter;

import fr.insee.eno.core.model.Question;
import fr.insee.eno.core.model.Variable;
import fr.insee.lunatic.model.flat.VariableType;

public class LunaticConverter {

    /**
     * Return the Lunatic class type that corresponds to the given Eno object.
     * @param enoObject An object from the Eno model.
     * @return A class from Lunatic flat model.
     * @throws IllegalArgumentException if the given object is not in package 'fr.insee.eno.core.model'.
     */
    public static Class<?> targetType(Object enoObject) {
        //
        if (! enoObject.getClass().getPackageName().startsWith("fr.insee.eno.core.model")) {
            throw new IllegalArgumentException("Not an Eno object.");
        }
        //
        if (enoObject instanceof Variable) {
            return VariableType.class;
        } else if (enoObject instanceof Question) {
            ((Question) enoObject).getType();
            return null; // TODO
        } else {
            throw new RuntimeException("Lunatic conversion for Eno type " + enoObject.getClass() + " not implemented.");
        }

    }

}
