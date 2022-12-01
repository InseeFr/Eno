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
 * <p>Classe Java pour Parameters complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Parameters"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;all&gt;
 *         &lt;element name="Context" type="{}Context"/&gt;
 *         &lt;element name="Campagne" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Languages" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Language" type="{}Language" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="BeginQuestion" type="{}BeginQuestion" minOccurs="0"/&gt;
 *         &lt;element name="EndQuestion" type="{}EndQuestion" minOccurs="0"/&gt;
 *         &lt;element name="xforms-parameters" type="{}XFORMSParameters" minOccurs="0"/&gt;
 *         &lt;element name="fo-parameters" type="{}FOParameters" minOccurs="0"/&gt;
 *         &lt;element name="lunatic-xml-parameters" type="{}LunaticXMLParameters" minOccurs="0"/&gt;
 *         &lt;element name="fodt-parameters" type="{}FODTParameters" minOccurs="0"/&gt;
 *         &lt;element name="Numerotation" type="{}GlobalNumbering" minOccurs="0"/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Parameters", propOrder = {

})
public class Parameters {

    @XmlElement(name = "Context", required = true)
    @XmlSchemaType(name = "token")
    protected Context context;
    @XmlElement(name = "Campagne", required = true)
    protected String campagne;
    @XmlElement(name = "Languages")
    protected Parameters.Languages languages;
    @XmlElement(name = "BeginQuestion")
    protected BeginQuestion beginQuestion;
    @XmlElement(name = "EndQuestion")
    protected EndQuestion endQuestion;
    @XmlElement(name = "xforms-parameters")
    protected XFORMSParameters xformsParameters;
    @XmlElement(name = "fo-parameters")
    protected FOParameters foParameters;
    @XmlElement(name = "lunatic-xml-parameters")
    protected LunaticXMLParameters lunaticXmlParameters;
    @XmlElement(name = "fodt-parameters")
    protected FODTParameters fodtParameters;
    @XmlElement(name = "Numerotation")
    protected GlobalNumbering numerotation;

    /**
     * Obtient la valeur de la propriété context.
     * 
     * @return
     *     possible object is
     *     {@link Context }
     *     
     */
    public Context getContext() {
        return context;
    }

    /**
     * Définit la valeur de la propriété context.
     * 
     * @param value
     *     allowed object is
     *     {@link Context }
     *     
     */
    public void setContext(Context value) {
        this.context = value;
    }

    /**
     * Obtient la valeur de la propriété campagne.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCampagne() {
        return campagne;
    }

    /**
     * Définit la valeur de la propriété campagne.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCampagne(String value) {
        this.campagne = value;
    }

    /**
     * Obtient la valeur de la propriété languages.
     * 
     * @return
     *     possible object is
     *     {@link Parameters.Languages }
     *     
     */
    public Parameters.Languages getLanguages() {
        return languages;
    }

    /**
     * Définit la valeur de la propriété languages.
     * 
     * @param value
     *     allowed object is
     *     {@link Parameters.Languages }
     *     
     */
    public void setLanguages(Parameters.Languages value) {
        this.languages = value;
    }

    /**
     * Obtient la valeur de la propriété beginQuestion.
     * 
     * @return
     *     possible object is
     *     {@link BeginQuestion }
     *     
     */
    public BeginQuestion getBeginQuestion() {
        return beginQuestion;
    }

    /**
     * Définit la valeur de la propriété beginQuestion.
     * 
     * @param value
     *     allowed object is
     *     {@link BeginQuestion }
     *     
     */
    public void setBeginQuestion(BeginQuestion value) {
        this.beginQuestion = value;
    }

    /**
     * Obtient la valeur de la propriété endQuestion.
     * 
     * @return
     *     possible object is
     *     {@link EndQuestion }
     *     
     */
    public EndQuestion getEndQuestion() {
        return endQuestion;
    }

    /**
     * Définit la valeur de la propriété endQuestion.
     * 
     * @param value
     *     allowed object is
     *     {@link EndQuestion }
     *     
     */
    public void setEndQuestion(EndQuestion value) {
        this.endQuestion = value;
    }

    /**
     * Obtient la valeur de la propriété xformsParameters.
     * 
     * @return
     *     possible object is
     *     {@link XFORMSParameters }
     *     
     */
    public XFORMSParameters getXformsParameters() {
        return xformsParameters;
    }

    /**
     * Définit la valeur de la propriété xformsParameters.
     * 
     * @param value
     *     allowed object is
     *     {@link XFORMSParameters }
     *     
     */
    public void setXformsParameters(XFORMSParameters value) {
        this.xformsParameters = value;
    }

    /**
     * Obtient la valeur de la propriété foParameters.
     * 
     * @return
     *     possible object is
     *     {@link FOParameters }
     *     
     */
    public FOParameters getFoParameters() {
        return foParameters;
    }

    /**
     * Définit la valeur de la propriété foParameters.
     * 
     * @param value
     *     allowed object is
     *     {@link FOParameters }
     *     
     */
    public void setFoParameters(FOParameters value) {
        this.foParameters = value;
    }

    /**
     * Obtient la valeur de la propriété lunaticXmlParameters.
     * 
     * @return
     *     possible object is
     *     {@link LunaticXMLParameters }
     *     
     */
    public LunaticXMLParameters getLunaticXmlParameters() {
        return lunaticXmlParameters;
    }

    /**
     * Définit la valeur de la propriété lunaticXmlParameters.
     * 
     * @param value
     *     allowed object is
     *     {@link LunaticXMLParameters }
     *     
     */
    public void setLunaticXmlParameters(LunaticXMLParameters value) {
        this.lunaticXmlParameters = value;
    }

    /**
     * Obtient la valeur de la propriété fodtParameters.
     * 
     * @return
     *     possible object is
     *     {@link FODTParameters }
     *     
     */
    public FODTParameters getFodtParameters() {
        return fodtParameters;
    }

    /**
     * Définit la valeur de la propriété fodtParameters.
     * 
     * @param value
     *     allowed object is
     *     {@link FODTParameters }
     *     
     */
    public void setFodtParameters(FODTParameters value) {
        this.fodtParameters = value;
    }

    /**
     * Obtient la valeur de la propriété numerotation.
     * 
     * @return
     *     possible object is
     *     {@link GlobalNumbering }
     *     
     */
    public GlobalNumbering getNumerotation() {
        return numerotation;
    }

    /**
     * Définit la valeur de la propriété numerotation.
     * 
     * @param value
     *     allowed object is
     *     {@link GlobalNumbering }
     *     
     */
    public void setNumerotation(GlobalNumbering value) {
        this.numerotation = value;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="Language" type="{}Language" maxOccurs="unbounded" minOccurs="0"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "language"
    })
    public static class Languages {

        @XmlElement(name = "Language", defaultValue = "fr")
        @XmlSchemaType(name = "token")
        protected List<Language> language;

        /**
         * Gets the value of the language property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the language property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getLanguage().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Language }
         * 
         * 
         */
        public List<Language> getLanguage() {
            if (language == null) {
                language = new ArrayList<Language>();
            }
            return this.language;
        }

    }

}
