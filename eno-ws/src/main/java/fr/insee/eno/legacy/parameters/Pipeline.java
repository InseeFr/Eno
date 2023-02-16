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
 * <p>Classe Java pour Pipeline complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Pipeline"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="InFormat" type="{}InFormat"/&gt;
 *         &lt;element name="OutFormat" type="{}OutFormat"/&gt;
 *         &lt;sequence&gt;
 *           &lt;element name="PreProcessing" type="{}PreProcessing" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;/sequence&gt;
 *         &lt;sequence&gt;
 *           &lt;element name="PostProcessing" type="{}PostProcessing" maxOccurs="unbounded" minOccurs="0"/&gt;
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
@XmlType(name = "Pipeline", propOrder = {
    "inFormat",
    "outFormat",
    "preProcessing",
    "postProcessing"
})
public class Pipeline {

    @XmlElement(name = "InFormat", required = true)
    @XmlSchemaType(name = "token")
    protected InFormat inFormat;
    @XmlElement(name = "OutFormat", required = true)
    @XmlSchemaType(name = "token")
    protected OutFormat outFormat;
    @XmlElement(name = "PreProcessing")
    @XmlSchemaType(name = "token")
    protected List<PreProcessing> preProcessing;
    @XmlElement(name = "PostProcessing")
    @XmlSchemaType(name = "token")
    protected List<PostProcessing> postProcessing;

    /**
     * Obtient la valeur de la propriété inFormat.
     * 
     * @return
     *     possible object is
     *     {@link InFormat }
     *     
     */
    public InFormat getInFormat() {
        return inFormat;
    }

    /**
     * Définit la valeur de la propriété inFormat.
     * 
     * @param value
     *     allowed object is
     *     {@link InFormat }
     *     
     */
    public void setInFormat(InFormat value) {
        this.inFormat = value;
    }

    /**
     * Obtient la valeur de la propriété outFormat.
     * 
     * @return
     *     possible object is
     *     {@link OutFormat }
     *     
     */
    public OutFormat getOutFormat() {
        return outFormat;
    }

    /**
     * Définit la valeur de la propriété outFormat.
     * 
     * @param value
     *     allowed object is
     *     {@link OutFormat }
     *     
     */
    public void setOutFormat(OutFormat value) {
        this.outFormat = value;
    }

    /**
     * Gets the value of the preProcessing property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the preProcessing property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPreProcessing().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PreProcessing }
     * 
     * 
     */
    public List<PreProcessing> getPreProcessing() {
        if (preProcessing == null) {
            preProcessing = new ArrayList<PreProcessing>();
        }
        return this.preProcessing;
    }

    /**
     * Gets the value of the postProcessing property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the postProcessing property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPostProcessing().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PostProcessing }
     * 
     * 
     */
    public List<PostProcessing> getPostProcessing() {
        if (postProcessing == null) {
            postProcessing = new ArrayList<PostProcessing>();
        }
        return this.postProcessing;
    }

}
