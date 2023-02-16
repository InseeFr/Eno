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
 * <p>Classe Java pour LunaticXMLParameters complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="LunaticXMLParameters"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;all&gt;
 *         &lt;element name="FilterDescription" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="AddFilterResult" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="MissingVar" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Tooltip" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Control" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Pagination" type="{}Pagination" minOccurs="0"/&gt;
 *         &lt;element name="UnusedVars" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LunaticXMLParameters", propOrder = {

})
public class LunaticXMLParameters {

    @XmlElement(name = "FilterDescription", defaultValue = "false")
    protected Boolean filterDescription;
    @XmlElement(name = "AddFilterResult", defaultValue = "false")
    protected Boolean addFilterResult;
    @XmlElement(name = "MissingVar", defaultValue = "false")
    protected Boolean missingVar;
    @XmlElement(name = "Tooltip", defaultValue = "false")
    protected Boolean tooltip;
    @XmlElement(name = "Control", defaultValue = "false")
    protected Boolean control;
    @XmlElement(name = "Pagination")
    @XmlSchemaType(name = "token")
    protected Pagination pagination;
    @XmlElement(name = "UnusedVars")
    protected Boolean unusedVars;

    /**
     * Obtient la valeur de la propriété filterDescription.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isFilterDescription() {
        return filterDescription;
    }

    /**
     * Définit la valeur de la propriété filterDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setFilterDescription(Boolean value) {
        this.filterDescription = value;
    }

    /**
     * Obtient la valeur de la propriété addFilterResult.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAddFilterResult() {
        return addFilterResult;
    }

    /**
     * Définit la valeur de la propriété addFilterResult.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAddFilterResult(Boolean value) {
        this.addFilterResult = value;
    }

    /**
     * Obtient la valeur de la propriété missingVar.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMissingVar() {
        return missingVar;
    }

    /**
     * Définit la valeur de la propriété missingVar.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMissingVar(Boolean value) {
        this.missingVar = value;
    }

    /**
     * Obtient la valeur de la propriété tooltip.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTooltip() {
        return tooltip;
    }

    /**
     * Définit la valeur de la propriété tooltip.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTooltip(Boolean value) {
        this.tooltip = value;
    }

    /**
     * Obtient la valeur de la propriété control.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isControl() {
        return control;
    }

    /**
     * Définit la valeur de la propriété control.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setControl(Boolean value) {
        this.control = value;
    }

    /**
     * Obtient la valeur de la propriété pagination.
     * 
     * @return
     *     possible object is
     *     {@link Pagination }
     *     
     */
    public Pagination getPagination() {
        return pagination;
    }

    /**
     * Définit la valeur de la propriété pagination.
     * 
     * @param value
     *     allowed object is
     *     {@link Pagination }
     *     
     */
    public void setPagination(Pagination value) {
        this.pagination = value;
    }

    /**
     * Obtient la valeur de la propriété unusedVars.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUnusedVars() {
        return unusedVars;
    }

    /**
     * Définit la valeur de la propriété unusedVars.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUnusedVars(Boolean value) {
        this.unusedVars = value;
    }

}
