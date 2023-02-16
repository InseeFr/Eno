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
 * <p>Classe Java pour FOParameters complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="FOParameters"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;all&gt;
 *         &lt;element name="InitializeAllVariables" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Format" type="{}Format" minOccurs="0"/&gt;
 *         &lt;element name="Roster" type="{}Roster" minOccurs="0"/&gt;
 *         &lt;element name="TextArea" type="{}TextArea" minOccurs="0"/&gt;
 *         &lt;element name="Table" type="{}Table" minOccurs="0"/&gt;
 *         &lt;element name="Loop" type="{}Loop" minOccurs="0"/&gt;
 *         &lt;element name="Capture" type="{}Capture" minOccurs="0"/&gt;
 *         &lt;element name="PageBreakBetween" type="{}PageBreakBetween" minOccurs="0"/&gt;
 *         &lt;element name="AccompanyingMail" type="{}AccompanyingMail" minOccurs="0"/&gt;
 *         &lt;element name="Style" type="{}Style" minOccurs="0"/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FOParameters", propOrder = {

})
public class FOParameters {

    @XmlElement(name = "InitializeAllVariables", defaultValue = "false")
    protected Boolean initializeAllVariables;
    @XmlElement(name = "Format")
    protected Format format;
    @XmlElement(name = "Roster")
    protected Roster roster;
    @XmlElement(name = "TextArea")
    protected TextArea textArea;
    @XmlElement(name = "Table")
    protected Table table;
    @XmlElement(name = "Loop")
    protected Loop loop;
    @XmlElement(name = "Capture")
    protected Capture capture;
    @XmlElement(name = "PageBreakBetween")
    protected PageBreakBetween pageBreakBetween;
    @XmlElement(name = "AccompanyingMail", defaultValue = "")
    @XmlSchemaType(name = "token")
    protected AccompanyingMail accompanyingMail;
    @XmlElement(name = "Style")
    protected Style style;

    /**
     * Obtient la valeur de la propriété initializeAllVariables.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isInitializeAllVariables() {
        return initializeAllVariables;
    }

    /**
     * Définit la valeur de la propriété initializeAllVariables.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setInitializeAllVariables(Boolean value) {
        this.initializeAllVariables = value;
    }

    /**
     * Obtient la valeur de la propriété format.
     * 
     * @return
     *     possible object is
     *     {@link Format }
     *     
     */
    public Format getFormat() {
        return format;
    }

    /**
     * Définit la valeur de la propriété format.
     * 
     * @param value
     *     allowed object is
     *     {@link Format }
     *     
     */
    public void setFormat(Format value) {
        this.format = value;
    }

    /**
     * Obtient la valeur de la propriété roster.
     * 
     * @return
     *     possible object is
     *     {@link Roster }
     *     
     */
    public Roster getRoster() {
        return roster;
    }

    /**
     * Définit la valeur de la propriété roster.
     * 
     * @param value
     *     allowed object is
     *     {@link Roster }
     *     
     */
    public void setRoster(Roster value) {
        this.roster = value;
    }

    /**
     * Obtient la valeur de la propriété textArea.
     * 
     * @return
     *     possible object is
     *     {@link TextArea }
     *     
     */
    public TextArea getTextArea() {
        return textArea;
    }

    /**
     * Définit la valeur de la propriété textArea.
     * 
     * @param value
     *     allowed object is
     *     {@link TextArea }
     *     
     */
    public void setTextArea(TextArea value) {
        this.textArea = value;
    }

    /**
     * Obtient la valeur de la propriété table.
     * 
     * @return
     *     possible object is
     *     {@link Table }
     *     
     */
    public Table getTable() {
        return table;
    }

    /**
     * Définit la valeur de la propriété table.
     * 
     * @param value
     *     allowed object is
     *     {@link Table }
     *     
     */
    public void setTable(Table value) {
        this.table = value;
    }

    /**
     * Obtient la valeur de la propriété loop.
     * 
     * @return
     *     possible object is
     *     {@link Loop }
     *     
     */
    public Loop getLoop() {
        return loop;
    }

    /**
     * Définit la valeur de la propriété loop.
     * 
     * @param value
     *     allowed object is
     *     {@link Loop }
     *     
     */
    public void setLoop(Loop value) {
        this.loop = value;
    }

    /**
     * Obtient la valeur de la propriété capture.
     * 
     * @return
     *     possible object is
     *     {@link Capture }
     *     
     */
    public Capture getCapture() {
        return capture;
    }

    /**
     * Définit la valeur de la propriété capture.
     * 
     * @param value
     *     allowed object is
     *     {@link Capture }
     *     
     */
    public void setCapture(Capture value) {
        this.capture = value;
    }

    /**
     * Obtient la valeur de la propriété pageBreakBetween.
     * 
     * @return
     *     possible object is
     *     {@link PageBreakBetween }
     *     
     */
    public PageBreakBetween getPageBreakBetween() {
        return pageBreakBetween;
    }

    /**
     * Définit la valeur de la propriété pageBreakBetween.
     * 
     * @param value
     *     allowed object is
     *     {@link PageBreakBetween }
     *     
     */
    public void setPageBreakBetween(PageBreakBetween value) {
        this.pageBreakBetween = value;
    }

    /**
     * Obtient la valeur de la propriété accompanyingMail.
     * 
     * @return
     *     possible object is
     *     {@link AccompanyingMail }
     *     
     */
    public AccompanyingMail getAccompanyingMail() {
        return accompanyingMail;
    }

    /**
     * Définit la valeur de la propriété accompanyingMail.
     * 
     * @param value
     *     allowed object is
     *     {@link AccompanyingMail }
     *     
     */
    public void setAccompanyingMail(AccompanyingMail value) {
        this.accompanyingMail = value;
    }

    /**
     * Obtient la valeur de la propriété style.
     * 
     * @return
     *     possible object is
     *     {@link Style }
     *     
     */
    public Style getStyle() {
        return style;
    }

    /**
     * Définit la valeur de la propriété style.
     * 
     * @param value
     *     allowed object is
     *     {@link Style }
     *     
     */
    public void setStyle(Style value) {
        this.style = value;
    }

}
