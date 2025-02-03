package fr.insee.eno.factory;

import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlAnySimpleTypeImpl;
import org.modelmapper.internal.bytebuddy.dynamic.scaffold.MethodGraph;

// https://www.datypic.com/sc/fo11/
public class FOUtils {

    public static String camelToKebab(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }
        StringBuilder kebabCase = new StringBuilder();
        for (char c : camelCase.toCharArray()) {
            if (Character.isUpperCase(c)) {
                if (kebabCase.length() > 0) {
                    kebabCase.append('-');
                }
                kebabCase.append(Character.toLowerCase(c));
            } else {
                kebabCase.append(c);
            }
        }

        return kebabCase.toString();
    }

    public static XmlAnySimpleType valueOf(Object value){
        XmlAnySimpleType stringSimpleType = XmlAnySimpleType.Factory.newInstance();
        stringSimpleType.setStringValue(value.toString());
        return stringSimpleType;
    }
}
