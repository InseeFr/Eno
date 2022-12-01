//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.3.2 
// Voir <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2022.09.08 à 10:18:57 AM CEST 
//


package fr.insee.eno.legacy.parameters;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour XFORMSParameters complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="XFORMSParameters"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="NumericExample" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Deblocage" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Satisfaction" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="LengthOfLongTable" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="DecimalSeparator" type="{}DecimalSeparator" minOccurs="0"/&gt;
 *         &lt;sequence&gt;
 *           &lt;element name="Css" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;/sequence&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "XFORMSParameters", propOrder = {
    "numericExample",
    "deblocage",
    "satisfaction",
    "lengthOfLongTable",
    "decimalSeparator",
    "css"
})
public class XFORMSParameters {

    @XmlElement(name = "NumericExample", defaultValue = "false")
    protected Boolean numericExample;
    @XmlElement(name = "Deblocage", defaultValue = "false")
    protected Boolean deblocage;
    @XmlElement(name = "Satisfaction", defaultValue = "false")
    protected Boolean satisfaction;
    @XmlElement(name = "LengthOfLongTable", defaultValue = "7")
    protected Integer lengthOfLongTable;
    @XmlElement(name = "DecimalSeparator", defaultValue = ",")
    @XmlSchemaType(name = "token")
    protected DecimalSeparator decimalSeparator;
    @XmlElement(name = "Css")
    protected List<String> css;

    /**
     * Obtient la valeur de la propriété numericExample.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNumericExample() {
        return numericExample;
    }

    /**
     * Définit la valeur de la propriété numericExample.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNumericExample(Boolean value) {
        this.numericExample = value;
    }

    /**
     * Obtient la valeur de la propriété deblocage.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDeblocage() {
        return deblocage;
    }

    /**
     * Définit la valeur de la propriété deblocage.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDeblocage(Boolean value) {
        this.deblocage = value;
    }

    /**
     * Obtient la valeur de la propriété satisfaction.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSatisfaction() {
        return satisfaction;
    }

    /**
     * Définit la valeur de la propriété satisfaction.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSatisfaction(Boolean value) {
        this.satisfaction = value;
    }

    /**
     * Obtient la valeur de la propriété lengthOfLongTable.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLengthOfLongTable() {
        return lengthOfLongTable;
    }

    /**
     * Définit la valeur de la propriété lengthOfLongTable.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLengthOfLongTable(Integer value) {
        this.lengthOfLongTable = value;
    }

    /**
     * Obtient la valeur de la propriété decimalSeparator.
     * 
     * @return
     *     possible object is
     *     {@link DecimalSeparator }
     *     
     */
    public DecimalSeparator getDecimalSeparator() {
        return decimalSeparator;
    }

    /**
     * Définit la valeur de la propriété decimalSeparator.
     * 
     * @param value
     *     allowed object is
     *     {@link DecimalSeparator }
     *     
     */
    public void setDecimalSeparator(DecimalSeparator value) {
        this.decimalSeparator = value;
    }

    /**
     * Gets the value of the css property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the css property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCss().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getCss() {
        if (css == null) {
            css = new ArrayList<String>();
        }
        return this.css;
    }

}
