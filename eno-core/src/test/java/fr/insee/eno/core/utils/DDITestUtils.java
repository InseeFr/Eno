package fr.insee.eno.core.utils;

import fr.insee.ddi.lifecycle33.reusable.AbstractIdentifiableType;
import fr.insee.ddi.lifecycle33.reusable.IDType;
import fr.insee.ddi.lifecycle33.reusable.ReferenceType;

public class DDITestUtils {

    public static void setId(AbstractIdentifiableType ddiObject, String id) {
        ddiObject.getIDList().add(IDType.Factory.newInstance());
        ddiObject.getIDArray(0).setStringValue(id);
    }

    public static void setId(ReferenceType ddiReferenceObject, String id) {
        ddiReferenceObject.getIDList().add(IDType.Factory.newInstance());
        ddiReferenceObject.getIDArray(0).setStringValue(id);
    }

}
