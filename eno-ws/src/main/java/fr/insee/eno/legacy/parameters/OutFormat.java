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
 * <p>Classe Java pour OutFormat.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="OutFormat"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;enumeration value="ddi"/&gt;
 *     &lt;enumeration value="xforms"/&gt;
 *     &lt;enumeration value="fo"/&gt;
 *     &lt;enumeration value="fodt"/&gt;
 *     &lt;enumeration value="lunatic-xml"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "OutFormat")
@XmlEnum
public enum OutFormat {

    @XmlEnumValue("ddi")
    DDI("ddi"),
    @XmlEnumValue("xforms")
    XFORMS("xforms"),
    @XmlEnumValue("fo")
    FO("fo"),
    @XmlEnumValue("fodt")
    FODT("fodt"),
    @XmlEnumValue("lunatic-xml")
    LUNATIC_XML("lunatic-xml");
    private final String value;

    OutFormat(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static OutFormat fromValue(String v) {
        for (OutFormat c: OutFormat.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
