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
 * <p>Classe Java pour Sequence-title complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Sequence-title"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="background-color" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="color" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="font-weight" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="margin-bottom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="font-size" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="border-color" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="border-style" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="space-before" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="space-before.conditionality" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="text-align" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Sequence-title", propOrder = {
    "backgroundColor",
    "color",
    "fontWeight",
    "marginBottom",
    "fontSize",
    "borderColor",
    "borderStyle",
    "spaceBefore",
    "spaceBeforeConditionality",
    "textAlign"
})
public class SequenceTitle {

    @XmlElement(name = "background-color", defaultValue = "#666666")
    protected String backgroundColor;
    @XmlElement(defaultValue = "white")
    protected String color;
    @XmlElement(name = "font-weight", defaultValue = "bold")
    protected String fontWeight;
    @XmlElement(name = "margin-bottom", defaultValue = "9pt")
    protected String marginBottom;
    @XmlElement(name = "font-size", defaultValue = "14pt")
    protected String fontSize;
    @XmlElement(name = "border-color", defaultValue = "black")
    protected String borderColor;
    @XmlElement(name = "border-style", defaultValue = "solid")
    protected String borderStyle;
    @XmlElement(name = "space-before", defaultValue = "10mm")
    protected String spaceBefore;
    @XmlElement(name = "space-before.conditionality", defaultValue = "discard")
    protected String spaceBeforeConditionality;
    @XmlElement(name = "text-align", defaultValue = "left")
    protected String textAlign;

    /**
     * Obtient la valeur de la propriété backgroundColor.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Définit la valeur de la propriété backgroundColor.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBackgroundColor(String value) {
        this.backgroundColor = value;
    }

    /**
     * Obtient la valeur de la propriété color.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColor() {
        return color;
    }

    /**
     * Définit la valeur de la propriété color.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColor(String value) {
        this.color = value;
    }

    /**
     * Obtient la valeur de la propriété fontWeight.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFontWeight() {
        return fontWeight;
    }

    /**
     * Définit la valeur de la propriété fontWeight.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFontWeight(String value) {
        this.fontWeight = value;
    }

    /**
     * Obtient la valeur de la propriété marginBottom.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMarginBottom() {
        return marginBottom;
    }

    /**
     * Définit la valeur de la propriété marginBottom.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMarginBottom(String value) {
        this.marginBottom = value;
    }

    /**
     * Obtient la valeur de la propriété fontSize.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFontSize() {
        return fontSize;
    }

    /**
     * Définit la valeur de la propriété fontSize.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFontSize(String value) {
        this.fontSize = value;
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
     * Obtient la valeur de la propriété spaceBefore.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpaceBefore() {
        return spaceBefore;
    }

    /**
     * Définit la valeur de la propriété spaceBefore.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpaceBefore(String value) {
        this.spaceBefore = value;
    }

    /**
     * Obtient la valeur de la propriété spaceBeforeConditionality.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpaceBeforeConditionality() {
        return spaceBeforeConditionality;
    }

    /**
     * Définit la valeur de la propriété spaceBeforeConditionality.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpaceBeforeConditionality(String value) {
        this.spaceBeforeConditionality = value;
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

}
