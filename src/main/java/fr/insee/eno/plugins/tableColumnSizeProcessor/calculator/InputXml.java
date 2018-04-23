package fr.insee.eno.plugins.tableColumnSizeProcessor.calculator;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.insee.eno.plugins.tableColumnSizeProcessor.calculator.entity.Table;
import fr.insee.eno.plugins.tableColumnSizeProcessor.calculator.entity.TableBody;
import fr.insee.eno.plugins.tableColumnSizeProcessor.calculator.entity.TableCell;
import fr.insee.eno.plugins.tableColumnSizeProcessor.calculator.entity.TableHeader;
import fr.insee.eno.plugins.tableColumnSizeProcessor.calculator.entity.TableRow;

/**
 * @author Karim
 * @version 1.0.0
 * <p>
 * <p>
 * This class allow you to read the fo file
 * and extract all table and build a structural
 * list of objects,
 * This class allow you to generate a temp HTML
 * File that is the mirror of the fo input file
 */
public class InputXml implements Serializable {
    private String filePath;
    private String htmlFilePath;
    private String imageDirectoryPath;
    private Document doc;
    private Integer[][] sizeArray;
    private List<Table> tableList;

    /**
     * Constructor build doc attribute from filePath
     *
     * @param filePath
     */
    public InputXml(String filePath,String htmlFilePath, String imageDirectoryPath) {
        this.filePath = filePath;
        this.htmlFilePath = htmlFilePath;
        this.imageDirectoryPath = imageDirectoryPath;
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            doc = builder.parse(this.filePath);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException p) {
            p.printStackTrace();
        }
    }

    /**
     * Allow you to manipulate the DOM of the fo file
     * In This function we are focused on tables
     */
    public Document calculateNewTableDimensions() {
        tableList = new ArrayList<Table>();
        int count = 0;
        NodeList tableNodes = doc.getElementsByTagName("fo:table");
        TABLE_LOOP: for (int i = 0; i < tableNodes.getLength(); i++) { //Loop Into Each Table in the current document
            NodeList tableChildsNodes = tableNodes.item(i).getChildNodes();
            Table table = new Table(tableNodes.item(i));
            TABLE_HEADER_BODY_LOOP: for (int j = 0; j < tableChildsNodes.getLength(); j++) { //Loop into each tag inside table to get rows
                if (tableChildsNodes.item(j).getNodeName().equals("fo:table-body") || tableChildsNodes.item(j).getNodeName().equals("fo:table-header")) {//Check if this table body
                    NodeList tableBodyNodes = tableChildsNodes.item(j).getChildNodes();
                    TableBody tableBody = new TableBody(tableChildsNodes.item(j));
                    TABLE_ROW_LOOP: for (int k = 0; k < tableBodyNodes.getLength(); k++) { //Loop to get all table-row tags
                        if (tableBodyNodes.item(k).getNodeName().equals("fo:table-row")) { //Check if this tag is fo:table-row
                            NodeList tableRowNodes = tableBodyNodes.item(k).getChildNodes();
                            TableRow tableRow = new TableRow(tableBodyNodes.item(k));
                            TABLE_COLUMN_LOOP: for (int l = 0; l < tableRowNodes.getLength(); l++) { //Loop into rows to get cell
                                if (tableRowNodes.item(l).getNodeName().equals("fo:table-cell")) {
                                    NodeList cellNodes = tableRowNodes.item(l).getChildNodes();
                                    String id = "" + i + j + k + l + count;
                                    TableCell tableCell = new TableCell(tableRowNodes.item(l), id);
                                    ((Element) tableRowNodes.item(l)).setAttribute("id", id);
                                    CELL_CONTENT_LOOP: for (int c = 0; c < cellNodes.getLength(); c++) {
                                        List<String> results = new ArrayList<String>();
                                        String result = "";
                                        if (cellNodes.item(c).getNodeName().equals("fo:external-graphic")) {
                                            String[] tempImageName = cellNodes.item(c).getAttributes().getNamedItem("src").getNodeValue().split("/");
                                            String imageName = tempImageName[tempImageName.length-1];
                                            results.add("<img style='margin-left:1mm;max-width:20px;' src='"+ imageDirectoryPath + imageName + "'/>");
                                        } else {
                                            browseChildContent(cellNodes.item(c), results, tableCell);
                                            for (String res : results)
                                                result += res;
                                        }
                                        if (tableCell.getContent() != null)
                                            result = tableCell.getContent() + result;
                                        tableCell.setContent(result);
                                        count++;
                                    }
                                    if (tableRow.getTableCells() == null)
                                        tableRow.setTableCells(new ArrayList<TableCell>());
                                    tableRow.getTableCells().add(tableCell);
                                }
                            }
                            if (tableBody.getTableRows() == null)
                                tableBody.setTableRows(new ArrayList<TableRow>());
                            tableBody.getTableRows().add(tableRow);
                        }
                    }
                    TableHeader tableHeader = new TableHeader(tableChildsNodes.item(j));
                    if (tableChildsNodes.item(j).getNodeName().equals("fo:table-header")) {
                        tableHeader.setHeaderTableRows(tableBody.getTableRows());
                        if (table.getTableHeaders() == null)
                            table.setTableHeaders(new ArrayList<TableHeader>());
                        table.getTableHeaders().add(tableHeader);
                    } else {
                        if (table.getTableBodies() == null)
                            table.setTableBodies(new ArrayList<TableBody>());
                        table.getTableBodies().add(tableBody);
                    }
                }
            }
            tableList.add(table);
        }
        String result = "";
        for (Table table : tableList) {
            result += table.toString();
        }
        result = wrapHtmlResult(result);
        try {
            FileOutputStream out = new FileOutputStream(this.htmlFilePath);
            out.write(result.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return doc;
    }

    /**
     * Allow to wrap with html basic tags
     *
     * @param htmlContent
     * @return HTML valid page
     */
    private String wrapHtmlResult(String htmlContent) {
        String result =
                "<html>" +
                        "<head>" +
                        "<meta charset=\"UTF-8\">" +
                        "</head>" +
                        "<body>";
        result += htmlContent;
        result += "</body>" +
                "</html>";
        return result;
    }

    /**
     * browse child node param
     * if is the last node get insert content on names
     * else call this function recursively
     *
     * @param node      : current Object Node
     * @param names     : List of concatenated content
     * @param tableCell
     */
    private void browseChildContent(Node node, List<String> names, TableCell tableCell) {
        NodeList nodeChild;
        String str = "";
        if (node.hasChildNodes()) {
            nodeChild = node.getChildNodes();
            for (int i = 0; i < nodeChild.getLength(); i++) {
                getStyleAttributes(nodeChild.item(i), tableCell);
                if (nodeChild.item(i).getNodeName().equals("fo:external-graphic")) {
                    String maxWidth = nodeChild.item(i).getAttributes().getNamedItem("src").getNodeValue().contains("mask_number")?"max-width:20px;":"";
                    String[] tempImageName = nodeChild.item(i).getAttributes().getNamedItem("src").getNodeValue().split("/");
                    String imageName = tempImageName[tempImageName.length-1];
                    names.add("<img style='margin-left:1mm;"+maxWidth+"' src='"  + imageDirectoryPath + imageName + "'/>");
                } else{
                    browseChildContent(nodeChild.item(i), names, tableCell);
                }
            }
        }
        if (node.getNodeName().equals("fo:block")) {
            if (node.getAttributes() != null) {
                if (node.getAttributes().getNamedItem("width") != null) {
                    if (node.getAttributes().getNamedItem("width").getNodeValue() != null) {
                        str = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
                    }
                }
            }
        } else if (node.getNodeValue() != null) {
            if (!node.getNodeValue().trim().isEmpty()){
                    str += node.getNodeValue();
            }

        }
        if (!str.trim().isEmpty() && str != null && !str.equals("null")) {
            names.add(str);
            getStyleAttributes(node, tableCell);
        }
    }

    /**
     * Allow to get styles that will be needed on the HTML file
     *
     * @param node      : from Node
     * @param tableCell : To Object
     */
    public void getStyleAttributes(Node node, TableCell tableCell) {
        Pattern p = Pattern.compile("\\b(font|margin|padding|)\\b");
        NamedNodeMap namedNodeMap = node.getAttributes();
        if (namedNodeMap != null) {
            int maxNumber = namedNodeMap.getLength();
            for (int j = 0; j < maxNumber; j++) {
                if (p.matcher(namedNodeMap.item(j).getNodeName()).find()) {
                    tableCell.getStyleAttributes().put(namedNodeMap.item(j).getNodeName(), namedNodeMap.item(j).getNodeValue());
                }
            }
        }
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer[][] getSizeArray() {
        return sizeArray;
    }

    public void setSizeArray(Integer[][] sizeArray) {
        this.sizeArray = sizeArray;
    }
}
