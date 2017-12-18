package com.calculator.entity;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableBody {
    private final static String openTag = "<tbody>";
    private final static String closeTag = "</tbody>";
    private List<TableRow> tableRows;
    private HashMap<String,String> attributes;

    public TableBody() {
    }


    public TableBody(Node node) {
        attributes = new HashMap<String,String>();
        NamedNodeMap namedNodeMap = node.getAttributes();
        int maxNumber = namedNodeMap.getLength();
        for(int i=0;i<maxNumber;i++){
            if(!namedNodeMap.item(i).getNodeName().equals("width"))
                attributes.put(namedNodeMap.item(i).getNodeName(),namedNodeMap.item(i).getNodeValue());
        }
    }

    private void filterAttributes(){
        attributes.remove("src");
        attributes.remove("width");
    }

    @Override
    public String toString() {
        String result = openTag;
        filterAttributes();
        String tempOpenTag = openTag.substring(0,openTag.indexOf('>'));
        if(attributes != null)
            for(Map.Entry<String, String> entry : attributes.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                tempOpenTag += " " + key + "='" + value + "'";
            }
        tempOpenTag+=">";
        result = tempOpenTag;
        for (TableRow item : tableRows){
            result += item.toString();
        }
        result+=closeTag;
        return result;
    }

    public List<TableRow> getTableRows() {
        return tableRows;
    }

    public void setTableRows(List<TableRow> tableRows) {
        this.tableRows = tableRows;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }
}
