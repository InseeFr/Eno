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
 * <p>Classe Java pour Mode.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="Mode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;enumeration value="cawi"/&gt;
 *     &lt;enumeration value="capi"/&gt;
 *     &lt;enumeration value="cati"/&gt;
 *     &lt;enumeration value="all"/&gt;
 *     &lt;enumeration value="papi"/&gt;
 *     &lt;enumeration value="process"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "Mode")
@XmlEnum
public enum Mode {

    @XmlEnumValue("cawi")
    CAWI("cawi"),
    @XmlEnumValue("capi")
    CAPI("capi"),
    @XmlEnumValue("cati")
    CATI("cati"),
    @XmlEnumValue("all")
    ALL("all"),
    @XmlEnumValue("papi")
    PAPI("papi"),
    @XmlEnumValue("process")
    PROCESS("process");
    private final String value;

    Mode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Mode fromValue(String v) {
        for (Mode c: Mode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
