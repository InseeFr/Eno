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
 * <p>Classe Java pour InFormat.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="InFormat"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;enumeration value="ddi"/&gt;
 *     &lt;enumeration value="pogues-xml"/&gt;
 *     &lt;enumeration value="xforms"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "InFormat")
@XmlEnum
public enum InFormat {

    @XmlEnumValue("ddi")
    DDI("ddi"),
    @XmlEnumValue("pogues-xml")
    POGUES_XML("pogues-xml"),
    @XmlEnumValue("xforms")
    XFORMS("xforms");
    private final String value;

    InFormat(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static InFormat fromValue(String v) {
        for (InFormat c: InFormat.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
