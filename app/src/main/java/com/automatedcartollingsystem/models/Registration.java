package com.automatedcartollingsystem.models;

/**
 * Matome Modiba @13/12/2020
 * Registration class.
 */

public class Registration {

    private String province;
    private final String prov_code;
    private final String registration;

    public Registration(String province, String prov_code, String registration) {
        if(province.equals("")||prov_code.equals("")||registration.equals(""))
            throw new IllegalArgumentException("Some thing is wrong with your car registration");
        this.province = province;
        this.prov_code = prov_code;
        this.registration = registration;
    }

    public Registration(String registration) {
        String[] arr = registration.split(" ");
        if(arr.length==3 && !(arr[0].equals("CA")||arr[0].equals("CAA"))){
            prov_code = arr[2];
        } else if (arr.length==4){
            prov_code = arr[3];
        } else {
            prov_code = arr[2];
        }
        this.registration=registration;
    }

    public String getProv_code() {
        return prov_code;
    }

    public String getRegistration() {
        return registration;
    }
    public String province(){
        switch (prov_code){
            case "L":
                province = "Limpopo";
                break;
            case "GP":
                province = "Gauteng";
                break;
            case "CA":
            case "CAA":
            case "WP":
                province = "Western Cape";
                break;
            case "NW":
                province = "North West";
                break;
            case "EC":
                province = "Eastern Cape";
                break;
            case "NC":
                province = "Northern Cape";
                break;
            case "MP":
                province = "Mpumalanga";
                break;
            case "N":
                province = "Kwa-Zulu Natal";
                break;
            case "FS":
                province = "Free State";
                break;
        }
        return province;
    }
}
