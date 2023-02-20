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
 * <p>Classe Java pour Style complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Style"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;all&gt;
 *         &lt;element name="Sequence-title" type="{}Sequence-title" minOccurs="0"/&gt;
 *         &lt;element name="Paragraph-title" type="{}Paragraph-title" minOccurs="0"/&gt;
 *         &lt;element name="general-style" type="{}general-style" minOccurs="0"/&gt;
 *         &lt;element name="label-cell" type="{}label-cell" minOccurs="0"/&gt;
 *         &lt;element name="label-question" type="{}label-question" minOccurs="0"/&gt;
 *         &lt;element name="row-header-line" type="{}row-header-line" minOccurs="0"/&gt;
 *         &lt;element name="table-column" type="{}table-column" minOccurs="0"/&gt;
 *         &lt;element name="data-cell" type="{}data-cell" minOccurs="0"/&gt;
 *         &lt;element name="Line-drawing" type="{}Line-drawing" minOccurs="0"/&gt;
 *         &lt;element name="Line-drawing-Garde" type="{}Line-drawing-Garde" minOccurs="0"/&gt;
 *         &lt;element name="filter-block" type="{}filter-block" minOccurs="0"/&gt;
 *         &lt;element name="filter-inline-container" type="{}filter-inline-container" minOccurs="0"/&gt;
 *         &lt;element name="filter-alternative" type="{}filter-alternative" minOccurs="0"/&gt;
 *         &lt;element name="footnote" type="{}footnote" minOccurs="0"/&gt;
 *         &lt;element name="instruction" type="{}instruction" minOccurs="0"/&gt;
 *         &lt;element name="statement" type="{}statement" minOccurs="0"/&gt;
 *         &lt;element name="answer-item" type="{}answer-item" minOccurs="0"/&gt;
 *         &lt;element name="details" type="{}details" minOccurs="0"/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Style", propOrder = {

})
public class Style {

    @XmlElement(name = "Sequence-title")
    protected SequenceTitle sequenceTitle;
    @XmlElement(name = "Paragraph-title")
    protected ParagraphTitle paragraphTitle;
    @XmlElement(name = "general-style")
    protected GeneralStyle generalStyle;
    @XmlElement(name = "label-cell")
    protected LabelCell labelCell;
    @XmlElement(name = "label-question")
    protected LabelQuestion labelQuestion;
    @XmlElement(name = "row-header-line")
    protected RowHeaderLine rowHeaderLine;
    @XmlElement(name = "table-column")
    protected TableColumn tableColumn;
    @XmlElement(name = "data-cell")
    protected DataCell dataCell;
    @XmlElement(name = "Line-drawing")
    protected LineDrawing lineDrawing;
    @XmlElement(name = "Line-drawing-Garde")
    protected LineDrawingGarde lineDrawingGarde;
    @XmlElement(name = "filter-block")
    protected FilterBlock filterBlock;
    @XmlElement(name = "filter-inline-container")
    protected FilterInlineContainer filterInlineContainer;
    @XmlElement(name = "filter-alternative")
    protected FilterAlternative filterAlternative;
    protected Footnote footnote;
    protected Instruction instruction;
    protected Statement statement;
    @XmlElement(name = "answer-item")
    protected AnswerItem answerItem;
    protected Details details;

    /**
     * Obtient la valeur de la propriété sequenceTitle.
     * 
     * @return
     *     possible object is
     *     {@link SequenceTitle }
     *     
     */
    public SequenceTitle getSequenceTitle() {
        return sequenceTitle;
    }

    /**
     * Définit la valeur de la propriété sequenceTitle.
     * 
     * @param value
     *     allowed object is
     *     {@link SequenceTitle }
     *     
     */
    public void setSequenceTitle(SequenceTitle value) {
        this.sequenceTitle = value;
    }

    /**
     * Obtient la valeur de la propriété paragraphTitle.
     * 
     * @return
     *     possible object is
     *     {@link ParagraphTitle }
     *     
     */
    public ParagraphTitle getParagraphTitle() {
        return paragraphTitle;
    }

    /**
     * Définit la valeur de la propriété paragraphTitle.
     * 
     * @param value
     *     allowed object is
     *     {@link ParagraphTitle }
     *     
     */
    public void setParagraphTitle(ParagraphTitle value) {
        this.paragraphTitle = value;
    }

    /**
     * Obtient la valeur de la propriété generalStyle.
     * 
     * @return
     *     possible object is
     *     {@link GeneralStyle }
     *     
     */
    public GeneralStyle getGeneralStyle() {
        return generalStyle;
    }

    /**
     * Définit la valeur de la propriété generalStyle.
     * 
     * @param value
     *     allowed object is
     *     {@link GeneralStyle }
     *     
     */
    public void setGeneralStyle(GeneralStyle value) {
        this.generalStyle = value;
    }

    /**
     * Obtient la valeur de la propriété labelCell.
     * 
     * @return
     *     possible object is
     *     {@link LabelCell }
     *     
     */
    public LabelCell getLabelCell() {
        return labelCell;
    }

    /**
     * Définit la valeur de la propriété labelCell.
     * 
     * @param value
     *     allowed object is
     *     {@link LabelCell }
     *     
     */
    public void setLabelCell(LabelCell value) {
        this.labelCell = value;
    }

    /**
     * Obtient la valeur de la propriété labelQuestion.
     * 
     * @return
     *     possible object is
     *     {@link LabelQuestion }
     *     
     */
    public LabelQuestion getLabelQuestion() {
        return labelQuestion;
    }

    /**
     * Définit la valeur de la propriété labelQuestion.
     * 
     * @param value
     *     allowed object is
     *     {@link LabelQuestion }
     *     
     */
    public void setLabelQuestion(LabelQuestion value) {
        this.labelQuestion = value;
    }

    /**
     * Obtient la valeur de la propriété rowHeaderLine.
     * 
     * @return
     *     possible object is
     *     {@link RowHeaderLine }
     *     
     */
    public RowHeaderLine getRowHeaderLine() {
        return rowHeaderLine;
    }

    /**
     * Définit la valeur de la propriété rowHeaderLine.
     * 
     * @param value
     *     allowed object is
     *     {@link RowHeaderLine }
     *     
     */
    public void setRowHeaderLine(RowHeaderLine value) {
        this.rowHeaderLine = value;
    }

    /**
     * Obtient la valeur de la propriété tableColumn.
     * 
     * @return
     *     possible object is
     *     {@link TableColumn }
     *     
     */
    public TableColumn getTableColumn() {
        return tableColumn;
    }

    /**
     * Définit la valeur de la propriété tableColumn.
     * 
     * @param value
     *     allowed object is
     *     {@link TableColumn }
     *     
     */
    public void setTableColumn(TableColumn value) {
        this.tableColumn = value;
    }

    /**
     * Obtient la valeur de la propriété dataCell.
     * 
     * @return
     *     possible object is
     *     {@link DataCell }
     *     
     */
    public DataCell getDataCell() {
        return dataCell;
    }

    /**
     * Définit la valeur de la propriété dataCell.
     * 
     * @param value
     *     allowed object is
     *     {@link DataCell }
     *     
     */
    public void setDataCell(DataCell value) {
        this.dataCell = value;
    }

    /**
     * Obtient la valeur de la propriété lineDrawing.
     * 
     * @return
     *     possible object is
     *     {@link LineDrawing }
     *     
     */
    public LineDrawing getLineDrawing() {
        return lineDrawing;
    }

    /**
     * Définit la valeur de la propriété lineDrawing.
     * 
     * @param value
     *     allowed object is
     *     {@link LineDrawing }
     *     
     */
    public void setLineDrawing(LineDrawing value) {
        this.lineDrawing = value;
    }

    /**
     * Obtient la valeur de la propriété lineDrawingGarde.
     * 
     * @return
     *     possible object is
     *     {@link LineDrawingGarde }
     *     
     */
    public LineDrawingGarde getLineDrawingGarde() {
        return lineDrawingGarde;
    }

    /**
     * Définit la valeur de la propriété lineDrawingGarde.
     * 
     * @param value
     *     allowed object is
     *     {@link LineDrawingGarde }
     *     
     */
    public void setLineDrawingGarde(LineDrawingGarde value) {
        this.lineDrawingGarde = value;
    }

    /**
     * Obtient la valeur de la propriété filterBlock.
     * 
     * @return
     *     possible object is
     *     {@link FilterBlock }
     *     
     */
    public FilterBlock getFilterBlock() {
        return filterBlock;
    }

    /**
     * Définit la valeur de la propriété filterBlock.
     * 
     * @param value
     *     allowed object is
     *     {@link FilterBlock }
     *     
     */
    public void setFilterBlock(FilterBlock value) {
        this.filterBlock = value;
    }

    /**
     * Obtient la valeur de la propriété filterInlineContainer.
     * 
     * @return
     *     possible object is
     *     {@link FilterInlineContainer }
     *     
     */
    public FilterInlineContainer getFilterInlineContainer() {
        return filterInlineContainer;
    }

    /**
     * Définit la valeur de la propriété filterInlineContainer.
     * 
     * @param value
     *     allowed object is
     *     {@link FilterInlineContainer }
     *     
     */
    public void setFilterInlineContainer(FilterInlineContainer value) {
        this.filterInlineContainer = value;
    }

    /**
     * Obtient la valeur de la propriété filterAlternative.
     * 
     * @return
     *     possible object is
     *     {@link FilterAlternative }
     *     
     */
    public FilterAlternative getFilterAlternative() {
        return filterAlternative;
    }

    /**
     * Définit la valeur de la propriété filterAlternative.
     * 
     * @param value
     *     allowed object is
     *     {@link FilterAlternative }
     *     
     */
    public void setFilterAlternative(FilterAlternative value) {
        this.filterAlternative = value;
    }

    /**
     * Obtient la valeur de la propriété footnote.
     * 
     * @return
     *     possible object is
     *     {@link Footnote }
     *     
     */
    public Footnote getFootnote() {
        return footnote;
    }

    /**
     * Définit la valeur de la propriété footnote.
     * 
     * @param value
     *     allowed object is
     *     {@link Footnote }
     *     
     */
    public void setFootnote(Footnote value) {
        this.footnote = value;
    }

    /**
     * Obtient la valeur de la propriété instruction.
     * 
     * @return
     *     possible object is
     *     {@link Instruction }
     *     
     */
    public Instruction getInstruction() {
        return instruction;
    }

    /**
     * Définit la valeur de la propriété instruction.
     * 
     * @param value
     *     allowed object is
     *     {@link Instruction }
     *     
     */
    public void setInstruction(Instruction value) {
        this.instruction = value;
    }

    /**
     * Obtient la valeur de la propriété statement.
     * 
     * @return
     *     possible object is
     *     {@link Statement }
     *     
     */
    public Statement getStatement() {
        return statement;
    }

    /**
     * Définit la valeur de la propriété statement.
     * 
     * @param value
     *     allowed object is
     *     {@link Statement }
     *     
     */
    public void setStatement(Statement value) {
        this.statement = value;
    }

    /**
     * Obtient la valeur de la propriété answerItem.
     * 
     * @return
     *     possible object is
     *     {@link AnswerItem }
     *     
     */
    public AnswerItem getAnswerItem() {
        return answerItem;
    }

    /**
     * Définit la valeur de la propriété answerItem.
     * 
     * @param value
     *     allowed object is
     *     {@link AnswerItem }
     *     
     */
    public void setAnswerItem(AnswerItem value) {
        this.answerItem = value;
    }

    /**
     * Obtient la valeur de la propriété details.
     * 
     * @return
     *     possible object is
     *     {@link Details }
     *     
     */
    public Details getDetails() {
        return details;
    }

    /**
     * Définit la valeur de la propriété details.
     * 
     * @param value
     *     allowed object is
     *     {@link Details }
     *     
     */
    public void setDetails(Details value) {
        this.details = value;
    }

}
