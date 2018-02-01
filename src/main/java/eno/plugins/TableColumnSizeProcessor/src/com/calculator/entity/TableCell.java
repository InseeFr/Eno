package com.calculator.entity;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class TableCell {
    private final static String openTag = "<td >";
    private final static String closeTag = "</td>";

    private String content;
    private HashMap<String, String> styleAttributes;
    private HashMap<String, String> attributes;

    public TableCell() {
    }

    private void filterAttributes() {
        attributes.remove("src");
        attributes.remove("width");

        styleAttributes.remove("src");
        styleAttributes.remove("width");
    }

    public TableCell(Node node, String id) {
        attributes = new HashMap<String, String>();
        attributes.put("id", id);
        styleAttributes = new HashMap<String, String>();
        styleAttributes.put("border", "1px");
        styleAttributes.put("font-family", "arial");
//        styleAttributes.put("min-width", "90px");
        NamedNodeMap namedNodeMap = node.getAttributes();
        int maxNumber = namedNodeMap.getLength();
        Pattern p = Pattern.compile("\\b(font|margin|padding|)\\b");
        for (int i = 0; i < maxNumber; i++) {
            if (namedNodeMap.item(i).getNodeName().contains("number-rows-spanned"))
                attributes.put("rowspan", namedNodeMap.item(i).getNodeValue());
            else if (namedNodeMap.item(i).getNodeName().contains("number-columns-spanned")) {
                attributes.put("colspan", namedNodeMap.item(i).getNodeValue());
            } else if (p.matcher(namedNodeMap.item(i).getNodeName()).find()) {
                styleAttributes.put(namedNodeMap.item(i).getNodeName(), namedNodeMap.item(i).getNodeValue());
            }
        }
    }

    @Override
    public String toString() {
        String result = openTag;
        filterAttributes();
        String tempOpenTag = openTag.substring(0, openTag.indexOf('>'));
        if (attributes != null)
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                tempOpenTag += " " + key + "='" + value + "'";
            }
        tempOpenTag += " style=\"";
        if (styleAttributes != null)
            for (Map.Entry<String, String> entry : styleAttributes.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                tempOpenTag += " " + key + ":" + value + ";";
            }
        tempOpenTag += "\">";
        String customStyle = "";
        if(content.contains("<img") || content.contains("\t&#10063;"))
            customStyle = "white-space: nowrap;";
        tempOpenTag += "<div style=\"margin-left:2mm;"+customStyle+"\">";
        result = tempOpenTag;
        result += content + "</div>" +closeTag;
        return result;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public HashMap<String, String> getStyleAttributes() {
        return styleAttributes;
    }

    public void setStyleAttributes(HashMap<String, String> styleAttributes) {
        this.styleAttributes = styleAttributes;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }
}
