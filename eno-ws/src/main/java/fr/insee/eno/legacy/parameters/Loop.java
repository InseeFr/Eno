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
 * <p>Classe Java pour Loop complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Loop"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DefaultOccurrence" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="MinimumEmptyOccurrence" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Loop", propOrder = {
    "defaultOccurrence",
    "minimumEmptyOccurrence"
})
public class Loop {

    @XmlElement(name = "DefaultOccurrence", defaultValue = "5")
    protected int defaultOccurrence;
    @XmlElement(name = "MinimumEmptyOccurrence", defaultValue = "1")
    protected int minimumEmptyOccurrence;

    /**
     * Obtient la valeur de la propriété defaultOccurrence.
     * 
     */
    public int getDefaultOccurrence() {
        return defaultOccurrence;
    }

    /**
     * Définit la valeur de la propriété defaultOccurrence.
     * 
     */
    public void setDefaultOccurrence(int value) {
        this.defaultOccurrence = value;
    }

    /**
     * Obtient la valeur de la propriété minimumEmptyOccurrence.
     * 
     */
    public int getMinimumEmptyOccurrence() {
        return minimumEmptyOccurrence;
    }

    /**
     * Définit la valeur de la propriété minimumEmptyOccurrence.
     * 
     */
    public void setMinimumEmptyOccurrence(int value) {
        this.minimumEmptyOccurrence = value;
    }

}
