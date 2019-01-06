package com.example.toms.assapp.model;

import java.util.List;

public class Device {

    //Atributos
    private String id;
    private String typeDevice;
    private String salesDate;
    private String name;
    private String make;
    private String model;
    private List<String> photoList;
    private Boolean insured;

    //consturctores

    public Device() {
    }

    public Device(String id, String typeDevice, String name, String make, String model, String salesDate, List<String> photoList, Boolean insured) {
        this.id = id;
        this.typeDevice = typeDevice;
        this.name = name;
        this.make = make;
        this.model = model;
        this.salesDate = salesDate;
        this.photoList = photoList;
        this.insured = insured;
    }

    //getter

    public String getTypeDevice() {
        return typeDevice;
    }

    public Boolean getInsured() {
        return insured;
    }

    public String getName() {
        return name;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getId() {
        return id;
    }

    public String getSalesDate() {
        return salesDate;
    }

    public List<String> getPhotoList() {
        return photoList;
    }

    //tostring

    @Override
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", salesDate='" + salesDate + '\'' +
                ", photoList=" + photoList +
                ", insured=" + insured +
                '}';
    }

    //Setter ID
    public void setId(String id) {
        this.id = id;
    }
}
