package fr.insee.eno.ws.controller.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class HeadersUtils {

    private HeadersUtils() {}

    public static HttpHeaders with(String fileName) {
        HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return headers;
    }

    public static String extractFileName(String contentDisposition) {
        if (contentDisposition != null && contentDisposition.contains("filename=")) {
            return contentDisposition.substring(contentDisposition.indexOf("filename=") + 9)
                    .replace("\"", "");
        }
        return null;
    }

}
