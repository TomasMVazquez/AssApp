package com.example.toms.assapp.model;

import java.util.List;

public class Device {

    //Atributos
    private Integer id;
    private String typeDevice;
    private String name;
    private String make;
    private String model;
    private String salesDate;
    private List<String> photoList;
    private Boolean insured;

    //consturctores

    public Device() {
    }

    public Device(Integer id, String typeDevice, String name, String make, String model, String salesDate, List<String> photoList, Boolean insured) {
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

    public Integer getId() {
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
}
