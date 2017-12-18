package com.calculator.entity;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.List;

public class TableHeader {
    private final static String openTag = "<thead>";
    private final static String closeTag = "</thead>";


    private List<TableRow> headerTableRows;
    private HashMap<String,String> attributes;

    public TableHeader() {
    }

    public TableHeader(Node node) {
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
        for(TableRow item : headerTableRows){
            result += item.toString();
        }
        result += closeTag;
        return result;
    }

    public List<TableRow> getHeaderTableRows() {
        return headerTableRows;
    }

    public void setHeaderTableRows(List<TableRow> headerTableRows) {
        this.headerTableRows = headerTableRows;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }
}
