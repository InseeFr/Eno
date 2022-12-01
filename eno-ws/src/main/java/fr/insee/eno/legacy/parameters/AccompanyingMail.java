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
 * <p>Classe Java pour AccompanyingMail.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="AccompanyingMail"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;enumeration value=""/&gt;
 *     &lt;enumeration value="cnrCOL"/&gt;
 *     &lt;enumeration value="entreeCOL"/&gt;
 *     &lt;enumeration value="medCOL"/&gt;
 *     &lt;enumeration value="ouvertureCOL"/&gt;
 *     &lt;enumeration value="relanceCOL"/&gt;
 *     &lt;enumeration value="relanceCOLEM"/&gt;
 *     &lt;enumeration value="accompagnementCOL"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AccompanyingMail")
@XmlEnum
public enum AccompanyingMail {

    @XmlEnumValue("")
    NONE(""),
    @XmlEnumValue("cnrCOL")
    CNR_COL("cnrCOL"),
    @XmlEnumValue("entreeCOL")
    ENTREE_COL("entreeCOL"),
    @XmlEnumValue("medCOL")
    MED_COL("medCOL"),
    @XmlEnumValue("ouvertureCOL")
    OUVERTURE_COL("ouvertureCOL"),
    @XmlEnumValue("relanceCOL")
    RELANCE_COL("relanceCOL"),
    @XmlEnumValue("relanceCOLEM")
    RELANCE_COLEM("relanceCOLEM"),
    @XmlEnumValue("accompagnementCOL")
    ACCOMPAGNEMENT_COL("accompagnementCOL");
    private final String value;

    AccompanyingMail(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AccompanyingMail fromValue(String v) {
        for (AccompanyingMail c: AccompanyingMail.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
