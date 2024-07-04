package fr.insee.eno.core.utils;

import lombok.NonNull;
import reusable33.AbstractIdentifiableType;

// TODO: to be refactored in the ddi java library

/**
 * Utility class that provide some methods for DDI objects.
 */
public class DDIUtils {

    private DDIUtils() {}

    /**
     * Returns a better representation than the "toString" method for a DDI object.
     * @param ddiObject A DDI object.
     * @return String representation of the object.
     */
    public static String ddiToString(@NonNull Object ddiObject) {
        return ddiObject.getClass().getSimpleName()
                +((ddiObject instanceof AbstractIdentifiableType ddiIdentifiableObject) ?
                "[id="+ddiIdentifiableObject.getIDArray(0).getStringValue()+"]" :
                "");
    }

}
