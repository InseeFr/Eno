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
 * <p>Classe Java pour Roster complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Roster"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Row" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="DefaultSize" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *                   &lt;element name="MinimumEmpty" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Roster", propOrder = {
    "row"
})
public class Roster {

    @XmlElement(name = "Row")
    protected Roster.Row row;

    /**
     * Obtient la valeur de la propriété row.
     * 
     * @return
     *     possible object is
     *     {@link Roster.Row }
     *     
     */
    public Roster.Row getRow() {
        return row;
    }

    /**
     * Définit la valeur de la propriété row.
     * 
     * @param value
     *     allowed object is
     *     {@link Roster.Row }
     *     
     */
    public void setRow(Roster.Row value) {
        this.row = value;
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
     *         &lt;element name="DefaultSize" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
     *         &lt;element name="MinimumEmpty" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
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
        "defaultSize",
        "minimumEmpty"
    })
    public static class Row {

        @XmlElement(name = "DefaultSize", defaultValue = "10")
        protected int defaultSize;
        @XmlElement(name = "MinimumEmpty", defaultValue = "1")
        protected int minimumEmpty;

        /**
         * Obtient la valeur de la propriété defaultSize.
         * 
         */
        public int getDefaultSize() {
            return defaultSize;
        }

        /**
         * Définit la valeur de la propriété defaultSize.
         * 
         */
        public void setDefaultSize(int value) {
            this.defaultSize = value;
        }

        /**
         * Obtient la valeur de la propriété minimumEmpty.
         * 
         */
        public int getMinimumEmpty() {
            return minimumEmpty;
        }

        /**
         * Définit la valeur de la propriété minimumEmpty.
         * 
         */
        public void setMinimumEmpty(int value) {
            this.minimumEmpty = value;
        }

    }

}
