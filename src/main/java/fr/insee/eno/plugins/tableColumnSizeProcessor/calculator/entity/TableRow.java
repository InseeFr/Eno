package fr.insee.eno.plugins.tableColumnSizeProcessor.calculator.entity;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class TableRow {
    private final static String openTag = "<tr>";
    private final static String closeTag = "</tr>";

    private List<TableCell> tableCells;
    private HashMap<String,String> attributes;

    public TableRow() {
    }

    public TableRow(Node node) {
        attributes = new HashMap<String,String>();
        NamedNodeMap namedNodeMap = node.getAttributes();
        int maxNumber = namedNodeMap.getLength();
        Pattern p = Pattern.compile("\\b(font|margin|padding|)\\b");
        for(int i=0;i<maxNumber;i++){
            if(namedNodeMap.item(i).getNodeName().contains("col")||namedNodeMap.item(i).getNodeName().contains("row"))
                if(namedNodeMap.item(i).getNodeName().contains("number-rows-spanned"))
                    attributes.put("rowspan",namedNodeMap.item(i).getNodeValue());
                else if(namedNodeMap.item(i).getNodeName().contains("number-columns-spanned")){
                    attributes.put("colspan",namedNodeMap.item(i).getNodeValue());
                }else if(p.matcher(namedNodeMap.item(i).getNodeName()).find()){
                    attributes.put(namedNodeMap.item(i).getNodeName(),namedNodeMap.item(i).getNodeValue());
                }
        }
    }

    @Override
    public String toString() {
        String result = openTag;
        String tempOpenTag = openTag.substring(0,openTag.indexOf('>'));
        if(attributes != null)
            for(Map.Entry<String, String> entry : attributes.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                tempOpenTag += " " + key + "='" + value + "'";
            }
        tempOpenTag+=">";
        result = tempOpenTag;
        result = tempOpenTag;
        for(TableCell item : tableCells){
            result += item.toString();
        }
        result += closeTag;
        return result;
    }

    public List<TableCell> getTableCells() {
        return tableCells;
    }

    public void setTableCells(List<TableCell> tableCells) {
        this.tableCells = tableCells;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }
}
