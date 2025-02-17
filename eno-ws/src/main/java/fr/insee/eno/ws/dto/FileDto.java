package fr.insee.eno.ws.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Data transfer object to represent a file from service to controller.
 * */
@Builder
@Getter
@Setter
public class FileDto {

    private String name;
    private byte[] content;

}
