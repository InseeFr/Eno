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
 * <p>Classe Java pour PostProcessing.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="PostProcessing"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;enumeration value="ddi-specificTreatment"/&gt;
 *     &lt;enumeration value="fodt-specificTreatment"/&gt;
 *     &lt;enumeration value="fo-mailing"/&gt;
 *     &lt;enumeration value="fo-tableColumn"/&gt;
 *     &lt;enumeration value="fo-insertEndQuestion"/&gt;
 *     &lt;enumeration value="fo-editStructurePages"/&gt;
 *     &lt;enumeration value="fo-specificTreatment"/&gt;
 *     &lt;enumeration value="fo-insertCoverPage"/&gt;
 *     &lt;enumeration value="fo-insertAccompanyingMails"/&gt;
 *     &lt;enumeration value="lunatic-xml-sortComponents"/&gt;
 *     &lt;enumeration value="lunatic-xml-insert-generic-questions"/&gt;
 *     &lt;enumeration value="lunatic-xml-externalizeVariables"/&gt;
 *     &lt;enumeration value="lunatic-xml-insertCleaningBlock"/&gt;
 *     &lt;enumeration value="lunatic-xml-vtlParser"/&gt;
 *     &lt;enumeration value="lunatic-xml-pagination"/&gt;
 *     &lt;enumeration value="lunatic-xml-specificTreatment"/&gt;
 *     &lt;enumeration value="xforms-insert-generic-questions"/&gt;
 *     &lt;enumeration value="xforms-browsing"/&gt;
 *     &lt;enumeration value="xforms-insee-model"/&gt;
 *     &lt;enumeration value="xforms-insee-pattern"/&gt;
 *     &lt;enumeration value="xforms-identification"/&gt;
 *     &lt;enumeration value="xforms-insert-welcome"/&gt;
 *     &lt;enumeration value="xforms-insert-end"/&gt;
 *     &lt;enumeration value="xforms-specificTreatment"/&gt;
 *     &lt;enumeration value="xforms-fix-adherence"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "PostProcessing")
@XmlEnum
public enum PostProcessing {

    @XmlEnumValue("ddi-specificTreatment")
    DDI_SPECIFIC_TREATMENT("ddi-specificTreatment"),
    @XmlEnumValue("fodt-specificTreatment")
    FODT_SPECIFIC_TREATMENT("fodt-specificTreatment"),
    @XmlEnumValue("fo-mailing")
    FO_MAILING("fo-mailing"),
    @XmlEnumValue("fo-tableColumn")
    FO_TABLE_COLUMN("fo-tableColumn"),
    @XmlEnumValue("fo-insertEndQuestion")
    FO_INSERT_END_QUESTION("fo-insertEndQuestion"),
    @XmlEnumValue("fo-editStructurePages")
    FO_EDIT_STRUCTURE_PAGES("fo-editStructurePages"),
    @XmlEnumValue("fo-specificTreatment")
    FO_SPECIFIC_TREATMENT("fo-specificTreatment"),
    @XmlEnumValue("fo-insertCoverPage")
    FO_INSERT_COVER_PAGE("fo-insertCoverPage"),
    @XmlEnumValue("fo-insertAccompanyingMails")
    FO_INSERT_ACCOMPANYING_MAILS("fo-insertAccompanyingMails"),
    @XmlEnumValue("lunatic-xml-sortComponents")
    LUNATIC_XML_SORT_COMPONENTS("lunatic-xml-sortComponents"),
    @XmlEnumValue("lunatic-xml-insert-generic-questions")
    LUNATIC_XML_INSERT_GENERIC_QUESTIONS("lunatic-xml-insert-generic-questions"),
    @XmlEnumValue("lunatic-xml-externalizeVariables")
    LUNATIC_XML_EXTERNALIZE_VARIABLES("lunatic-xml-externalizeVariables"),
    @XmlEnumValue("lunatic-xml-insertCleaningBlock")
    LUNATIC_XML_INSERT_CLEANING_BLOCK("lunatic-xml-insertCleaningBlock"),
    @XmlEnumValue("lunatic-xml-vtlParser")
    LUNATIC_XML_VTL_PARSER("lunatic-xml-vtlParser"),
    @XmlEnumValue("lunatic-xml-pagination")
    LUNATIC_XML_PAGINATION("lunatic-xml-pagination"),
    @XmlEnumValue("lunatic-xml-specificTreatment")
    LUNATIC_XML_SPECIFIC_TREATMENT("lunatic-xml-specificTreatment"),
    @XmlEnumValue("xforms-insert-generic-questions")
    XFORMS_INSERT_GENERIC_QUESTIONS("xforms-insert-generic-questions"),
    @XmlEnumValue("xforms-browsing")
    XFORMS_BROWSING("xforms-browsing"),
    @XmlEnumValue("xforms-insee-model")
    XFORMS_INSEE_MODEL("xforms-insee-model"),
    @XmlEnumValue("xforms-insee-pattern")
    XFORMS_INSEE_PATTERN("xforms-insee-pattern"),
    @XmlEnumValue("xforms-identification")
    XFORMS_IDENTIFICATION("xforms-identification"),
    @XmlEnumValue("xforms-insert-welcome")
    XFORMS_INSERT_WELCOME("xforms-insert-welcome"),
    @XmlEnumValue("xforms-insert-end")
    XFORMS_INSERT_END("xforms-insert-end"),
    @XmlEnumValue("xforms-specificTreatment")
    XFORMS_SPECIFIC_TREATMENT("xforms-specificTreatment"),
    @XmlEnumValue("xforms-fix-adherence")
    XFORMS_FIX_ADHERENCE("xforms-fix-adherence");
    private final String value;

    PostProcessing(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PostProcessing fromValue(String v) {
        for (PostProcessing c: PostProcessing.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
