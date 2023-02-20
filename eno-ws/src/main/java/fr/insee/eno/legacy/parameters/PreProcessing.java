//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.3.2 
// Voir <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2022.09.08 à 10:18:57 AM CEST 
//


package fr.insee.eno.legacy.parameters;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour PreProcessing.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="PreProcessing"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;enumeration value="ddi-32-to-ddi-33"/&gt;
 *     &lt;enumeration value="ddi-multimodal-selection"/&gt;
 *     &lt;enumeration value="ddi-markdownToXhtml"/&gt;
 *     &lt;enumeration value="ddi-mapping"/&gt;
 *     &lt;enumeration value="ddi-dereferencing"/&gt;
 *     &lt;enumeration value="ddi-cleaning"/&gt;
 *     &lt;enumeration value="ddi-titling"/&gt;
 *     &lt;enumeration value="pogues-xml-insert-filter-loop-into-question-tree"/&gt;
 *     &lt;enumeration value="pogues-xml-goto-2-ite"/&gt;
 *     &lt;enumeration value="pogues-xml-suppression-goto"/&gt;
 *     &lt;enumeration value="pogues-xml-tweak-to-merge-equivalent-ite"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "PreProcessing")
@XmlEnum
public enum PreProcessing {

    @XmlEnumValue("ddi-32-to-ddi-33")
    DDI_32_TO_DDI_33("ddi-32-to-ddi-33"),
    @XmlEnumValue("ddi-multimodal-selection")
    DDI_MULTIMODAL_SELECTION("ddi-multimodal-selection"),
    @XmlEnumValue("ddi-markdownToXhtml")
    DDI_MARKDOWN_TO_XHTML("ddi-markdownToXhtml"),
    @XmlEnumValue("ddi-mapping")
    DDI_MAPPING("ddi-mapping"),
    @XmlEnumValue("ddi-dereferencing")
    DDI_DEREFERENCING("ddi-dereferencing"),
    @XmlEnumValue("ddi-cleaning")
    DDI_CLEANING("ddi-cleaning"),
    @XmlEnumValue("ddi-titling")
    DDI_TITLING("ddi-titling"),
    @XmlEnumValue("pogues-xml-insert-filter-loop-into-question-tree")
    POGUES_XML_INSERT_FILTER_LOOP_INTO_QUESTION_TREE("pogues-xml-insert-filter-loop-into-question-tree"),
    @XmlEnumValue("pogues-xml-goto-2-ite")
    POGUES_XML_GOTO_2_ITE("pogues-xml-goto-2-ite"),
    @XmlEnumValue("pogues-xml-suppression-goto")
    POGUES_XML_SUPPRESSION_GOTO("pogues-xml-suppression-goto"),
    @XmlEnumValue("pogues-xml-tweak-to-merge-equivalent-ite")
    POGUES_XML_TWEAK_TO_MERGE_EQUIVALENT_ITE("pogues-xml-tweak-to-merge-equivalent-ite");
    private final String value;

    PreProcessing(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PreProcessing fromValue(String v) {
        for (PreProcessing c: PreProcessing.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
