//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.3.2 
// Voir <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2022.09.08 à 10:18:57 AM CEST 
//


package fr.insee.eno.legacy.parameters;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour CaptureEnum.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="CaptureEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;enumeration value="manual"/&gt;
 *     &lt;enumeration value="optical"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "CaptureEnum")
@XmlEnum
public enum CaptureEnum {

    @XmlEnumValue("manual")
    MANUAL("manual"),
    @XmlEnumValue("optical")
    OPTICAL("optical");
    private final String value;

    CaptureEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CaptureEnum fromValue(String v) {
        for (CaptureEnum c: CaptureEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
