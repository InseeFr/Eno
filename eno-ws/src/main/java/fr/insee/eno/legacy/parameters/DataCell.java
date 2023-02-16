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
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour data-cell complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="data-cell"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="text-align" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="border-color" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="border-style" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="padding" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "data-cell", propOrder = {
    "textAlign",
    "borderColor",
    "borderStyle",
    "padding"
})
public class DataCell {

    @XmlElement(name = "text-align", defaultValue = "left")
    protected String textAlign;
    @XmlElement(name = "border-color", defaultValue = "black")
    protected String borderColor;
    @XmlElement(name = "border-style", defaultValue = "solid")
    protected String borderStyle;
    @XmlElement(defaultValue = "1mm")
    protected String padding;

    /**
     * Obtient la valeur de la propriété textAlign.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTextAlign() {
        return textAlign;
    }

    /**
     * Définit la valeur de la propriété textAlign.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTextAlign(String value) {
        this.textAlign = value;
    }

    /**
     * Obtient la valeur de la propriété borderColor.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBorderColor() {
        return borderColor;
    }

    /**
     * Définit la valeur de la propriété borderColor.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBorderColor(String value) {
        this.borderColor = value;
    }

    /**
     * Obtient la valeur de la propriété borderStyle.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBorderStyle() {
        return borderStyle;
    }

    /**
     * Définit la valeur de la propriété borderStyle.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBorderStyle(String value) {
        this.borderStyle = value;
    }

    /**
     * Obtient la valeur de la propriété padding.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPadding() {
        return padding;
    }

    /**
     * Définit la valeur de la propriété padding.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPadding(String value) {
        this.padding = value;
    }

}
