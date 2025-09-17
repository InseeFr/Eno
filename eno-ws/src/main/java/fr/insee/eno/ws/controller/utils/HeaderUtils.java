package fr.insee.eno.ws.controller.utils;

public class HeaderUtils {

    private HeaderUtils() {}

    public static String headersAttachment(String outputFileName) {
        return "attachment;filename=\"" + outputFileName + "\"";
    }

}
