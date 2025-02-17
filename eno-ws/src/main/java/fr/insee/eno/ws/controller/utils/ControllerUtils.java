package fr.insee.eno.ws.controller.utils;

import fr.insee.eno.ws.dto.FileDto;
import fr.insee.eno.ws.exception.EnoControllerException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class ControllerUtils {

    private ControllerUtils() {}


    public static void addMultipartToBody(MultipartBodyBuilder multipartBodyBuilder, MultipartFile multipartFile,
                                          String partName) throws EnoControllerException {
        try {
            multipartBodyBuilder.part(partName, byteArrayResourceWithFileName(multipartFile.getBytes(), multipartFile.getOriginalFilename()));
        } catch (IOException e) {
            throw new EnoControllerException(
                    "Unable to access content of given file " + multipartFile.getOriginalFilename());
        }
    }

    public static void addMultipartToBody(
            MultipartBodyBuilder multipartBodyBuilder, FileDto fileDto, String partName) {
        multipartBodyBuilder.part(partName, byteArrayResourceWithFileName(fileDto.getContent(), fileDto.getName()));
    }

    public static void addStringToMultipartBody(
            MultipartBodyBuilder multipartBodyBuilder, String content, String fileName, String partName) {
        multipartBodyBuilder.part(partName, byteArrayResourceWithFileName(content.getBytes(), fileName));
    }

    private static ByteArrayResource byteArrayResourceWithFileName(byte[] bytes, String fileName) {
        // Ugly but I didn't find anything better
        return new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };
    }
}
