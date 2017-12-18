package com.calculator.entity;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {
    private final static String openTag = "<table>";
    private final static String closeTag = "</table>";

    private String tableName;
    private HashMap<String,String> attributes;
    private List<TableHeader> tableHeaders;
    private List<TableBody> tableBodies;

    public Table() {
    }

    public Table(Node node) {
        attributes = new HashMap<String,String>();
        attributes.put("max-width",190+"mm");
        NamedNodeMap namedNodeMap = node.getAttributes();
        int maxNumber = namedNodeMap.getLength();
        for(int i=0;i<maxNumber;i++){
            if(!namedNodeMap.item(i).getNodeName().equals("width"))
                attributes.put(namedNodeMap.item(i).getNodeName(),namedNodeMap.item(i).getNodeValue());
        }
    }

    private void filterAttributes(){
        attributes.remove("src");
//        attributes.remove("width");
    }

    @Override
    public String toString() {
        String result = "";
        filterAttributes();
        String tempOpenTag = openTag.substring(0,openTag.indexOf('>'));
        tempOpenTag+=" style=\"";
        if(attributes != null)
            for(Map.Entry<String, String> entry : attributes.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                tempOpenTag += " " + key + ":" + value + ";";
            }
        tempOpenTag+="\">";
        result = tempOpenTag;
        if(tableHeaders != null)
            for (TableHeader item : tableHeaders)
                result += item.toString();
        if(tableBodies != null)
            for (TableBody item : tableBodies)
                result += item.toString();
        result += closeTag;
        return result;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<TableHeader> getTableHeaders() {
        return tableHeaders;
    }

    public void setTableHeaders(List<TableHeader> tableHeaders) {
        this.tableHeaders = tableHeaders;
    }

    public List<TableBody> getTableBodies() {
        return tableBodies;
    }

    public void setTableBodies(List<TableBody> tableBodies) {
        this.tableBodies = tableBodies;
    }
}
