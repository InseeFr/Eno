package fr.insee.eno.exception;

import net.sf.saxon.trans.XPathException;

public class Utils {

    public static String getErrorLocation(String styleSheet, Exception e){
        String errorMessage="";
        try{
            String location = ((XPathException) e).getLocationAsString();
            errorMessage += String.format("Error in :%s %s", styleSheet, location);
        } catch (Exception exception){
        }
        return errorMessage;
    }
}
