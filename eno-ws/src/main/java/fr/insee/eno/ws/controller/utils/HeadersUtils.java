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
}
