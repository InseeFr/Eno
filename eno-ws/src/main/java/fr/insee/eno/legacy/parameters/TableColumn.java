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
 * <p>Classe Java pour table-column complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="table-column"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="border-color" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="border-style" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="text-align" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="padding-left" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="padding-right" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "table-column", propOrder = {
    "borderColor",
    "borderStyle",
    "textAlign",
    "paddingLeft",
    "paddingRight"
})
public class TableColumn {

    @XmlElement(name = "border-color", defaultValue = "black")
    protected String borderColor;
    @XmlElement(name = "border-style", defaultValue = "solid")
    protected String borderStyle;
    @XmlElement(name = "text-align", defaultValue = "left")
    protected String textAlign;
    @XmlElement(name = "padding-left", defaultValue = "1mm")
    protected String paddingLeft;
    @XmlElement(name = "padding-right", defaultValue = "1mm")
    protected String paddingRight;

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
     * Obtient la valeur de la propriété paddingLeft.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaddingLeft() {
        return paddingLeft;
    }

    /**
     * Définit la valeur de la propriété paddingLeft.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaddingLeft(String value) {
        this.paddingLeft = value;
    }

    /**
     * Obtient la valeur de la propriété paddingRight.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaddingRight() {
        return paddingRight;
    }

    /**
     * Définit la valeur de la propriété paddingRight.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaddingRight(String value) {
        this.paddingRight = value;
    }

}
