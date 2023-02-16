//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.3.2 
// Voir <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2022.09.08 à 10:18:57 AM CEST 
//


package fr.insee.eno.legacy.parameters;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour Capture complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Capture"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Numeric" type="{}CaptureEnum" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Capture", propOrder = {
    "numeric"
})
public class Capture {

    @XmlElement(name = "Numeric", defaultValue = "optical")
    @XmlSchemaType(name = "token")
    protected CaptureEnum numeric;

    /**
     * Obtient la valeur de la propriété numeric.
     * 
     * @return
     *     possible object is
     *     {@link CaptureEnum }
     *     
     */
    public CaptureEnum getNumeric() {
        return numeric;
    }

    /**
     * Définit la valeur de la propriété numeric.
     * 
     * @param value
     *     allowed object is
     *     {@link CaptureEnum }
     *     
     */
    public void setNumeric(CaptureEnum value) {
        this.numeric = value;
    }

}
