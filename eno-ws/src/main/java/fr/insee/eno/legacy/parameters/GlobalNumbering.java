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
 * <p>Classe Java pour GlobalNumbering complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="GlobalNumbering"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;all&gt;
 *         &lt;element name="QuestNum" type="{}BrowsingEnum" minOccurs="0"/&gt;
 *         &lt;element name="SeqNum" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="PreQuestSymbol" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalNumbering", propOrder = {

})
public class GlobalNumbering {

    @XmlElement(name = "QuestNum", defaultValue = "module")
    @XmlSchemaType(name = "token")
    protected BrowsingEnum questNum;
    @XmlElement(name = "SeqNum", defaultValue = "true")
    protected Boolean seqNum;
    @XmlElement(name = "PreQuestSymbol", defaultValue = "true")
    protected Boolean preQuestSymbol;

    /**
     * Obtient la valeur de la propriété questNum.
     * 
     * @return
     *     possible object is
     *     {@link BrowsingEnum }
     *     
     */
    public BrowsingEnum getQuestNum() {
        return questNum;
    }

    /**
     * Définit la valeur de la propriété questNum.
     * 
     * @param value
     *     allowed object is
     *     {@link BrowsingEnum }
     *     
     */
    public void setQuestNum(BrowsingEnum value) {
        this.questNum = value;
    }

    /**
     * Obtient la valeur de la propriété seqNum.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSeqNum() {
        return seqNum;
    }

    /**
     * Définit la valeur de la propriété seqNum.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSeqNum(Boolean value) {
        this.seqNum = value;
    }

    /**
     * Obtient la valeur de la propriété preQuestSymbol.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPreQuestSymbol() {
        return preQuestSymbol;
    }

    /**
     * Définit la valeur de la propriété preQuestSymbol.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPreQuestSymbol(Boolean value) {
        this.preQuestSymbol = value;
    }

}
