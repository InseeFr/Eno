package fr.insee.eno.core.utils;

import lombok.NonNull;
import reusable33.AbstractIdentifiableType;

// TODO: to be refactored in tje ddi java library
public class DDIUtils {

    public static String ddiToString(@NonNull Object ddiObject) {
        return ddiObject.getClass().getSimpleName()
                +((ddiObject instanceof AbstractIdentifiableType ddiIdentifiableObject) ?
                "[id="+ddiIdentifiableObject.getIDArray(0).getStringValue()+"]" :
                "");
    }

}
