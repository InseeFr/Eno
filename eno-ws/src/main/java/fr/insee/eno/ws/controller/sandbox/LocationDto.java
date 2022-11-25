package fr.insee.eno.ws.controller.sandbox;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class LocationDto implements Serializable {

    public int id;
    public String slug;
    public String name;
    public String address;
    public double latitude;
    public double longitude;
    public String zipCode;
    public String city;
    public String country;
    public String furtherInfo;
    public String mapUrl;
    public String pictureFile;
    public boolean office;
    public boolean isPmi;

}
