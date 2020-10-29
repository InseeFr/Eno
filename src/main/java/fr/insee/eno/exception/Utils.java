package fr.insee.eno.exception;

import net.sf.saxon.trans.XPathException;

public class Utils {

    public static String getErrorLocation(String styleSheet, Exception e){
        String errorMessage="";
        try{
            int line = ((XPathException) e).getLocator().getLineNumber();
            int column = ((XPathException) e).getLocator().getColumnNumber();
            errorMessage += String.format("Error in :%s [line :%d - column:%d]", styleSheet,line,column);
        } catch (Exception exception){
        }
        return errorMessage;
    }
}
