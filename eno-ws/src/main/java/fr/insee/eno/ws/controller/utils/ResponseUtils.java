package fr.insee.eno.ws.controller.utils;

import fr.insee.eno.ws.dto.FileDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

public class ResponseUtils {

    private ResponseUtils() {}

    public static ResponseEntity<byte[]> okFromFileDto(FileDto fileDto) {
        return ResponseEntity.ok()
                .headers(HeadersUtils.with(fileDto.getName()))
                .body(fileDto.getContent());
    }

}
