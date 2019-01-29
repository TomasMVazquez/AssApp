package com.example.toms.assapp.model;

import java.util.Date;

public class Profile {

    //Atributos
    private String idProfile;
    private String name;
    private String email;
    private String phone;
    private String document;
    private Date birth;
    private String nationality;
    private String cuit;
    private String address;
    private String activity;
    private String socialStatus;

    //Constructor
    public Profile(String idProfile, String name, String email, String phone, String document, Date birth, String nationality, String cuit, String address, String activity, String socialStatus) {
        this.idProfile = idProfile;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.document = document;
        this.birth = birth;
        this.nationality = nationality;
        this.cuit = cuit;
        this.address = address;
        this.activity = activity;
        this.socialStatus = socialStatus;
    }

    //toString
    @Override
    public String toString() {
        return "Profile{" +
                "idProfile='" + idProfile + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", document='" + document + '\'' +
                ", birth=" + birth +
                ", nationality='" + nationality + '\'' +
                ", cuit='" + cuit + '\'' +
                ", address='" + address + '\'' +
                ", activity='" + activity + '\'' +
                ", socialStatus='" + socialStatus + '\'' +
                '}';
    }

    //Getter
    public String getIdProfile() {
        return idProfile;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getDocument() {
        return document;
    }

    public Date getBirth() {
        return birth;
    }

    public String getNationality() {
        return nationality;
    }

    public String getCuit() {
        return cuit;
    }

    public String getAddress() {
        return address;
    }

    public String getActivity() {
        return activity;
    }

    public String getSocialStatus() {
        return socialStatus;
    }
}
