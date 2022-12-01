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
 * <p>Classe Java pour filter-block complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="filter-block"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="space-before" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="space-after" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="start-indent" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="end-indent" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="background-color" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "filter-block", propOrder = {
    "spaceBefore",
    "spaceAfter",
    "startIndent",
    "endIndent",
    "backgroundColor"
})
public class FilterBlock {

    @XmlElement(name = "space-before", defaultValue = "2pt")
    protected String spaceBefore;
    @XmlElement(name = "space-after", defaultValue = "2pt")
    protected String spaceAfter;
    @XmlElement(name = "start-indent", defaultValue = "5%")
    protected String startIndent;
    @XmlElement(name = "end-indent", defaultValue = "0%")
    protected String endIndent;
    @XmlElement(name = "background-color", defaultValue = "#f0f0f0")
    protected String backgroundColor;

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
     * Obtient la valeur de la propriété spaceAfter.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpaceAfter() {
        return spaceAfter;
    }

    /**
     * Définit la valeur de la propriété spaceAfter.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpaceAfter(String value) {
        this.spaceAfter = value;
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

}
