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
 * <p>Classe Java pour filter-inline-container complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="filter-inline-container"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="width" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="vertical-align" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="padding-top" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="start-indent" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="end-indent" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "filter-inline-container", propOrder = {
    "width",
    "verticalAlign",
    "paddingTop",
    "startIndent",
    "endIndent"
})
public class FilterInlineContainer {

    @XmlElement(defaultValue = "85%")
    protected String width;
    @XmlElement(name = "vertical-align", defaultValue = "middle")
    protected String verticalAlign;
    @XmlElement(name = "padding-top", defaultValue = "0pt")
    protected String paddingTop;
    @XmlElement(name = "start-indent", defaultValue = "0%")
    protected String startIndent;
    @XmlElement(name = "end-indent", defaultValue = "0%")
    protected String endIndent;

    /**
     * Obtient la valeur de la propriété width.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWidth() {
        return width;
    }

    /**
     * Définit la valeur de la propriété width.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWidth(String value) {
        this.width = value;
    }

    /**
     * Obtient la valeur de la propriété verticalAlign.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVerticalAlign() {
        return verticalAlign;
    }

    /**
     * Définit la valeur de la propriété verticalAlign.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVerticalAlign(String value) {
        this.verticalAlign = value;
    }

    /**
     * Obtient la valeur de la propriété paddingTop.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaddingTop() {
        return paddingTop;
    }

    /**
     * Définit la valeur de la propriété paddingTop.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaddingTop(String value) {
        this.paddingTop = value;
    }

    /**
     * Obtient la valeur de la propriété startIndent.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStartIndent() {
        return startIndent;
    }

    /**
     * Définit la valeur de la propriété startIndent.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartIndent(String value) {
        this.startIndent = value;
    }

    /**
     * Obtient la valeur de la propriété endIndent.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndIndent() {
        return endIndent;
    }

    /**
     * Définit la valeur de la propriété endIndent.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndIndent(String value) {
        this.endIndent = value;
    }

}
