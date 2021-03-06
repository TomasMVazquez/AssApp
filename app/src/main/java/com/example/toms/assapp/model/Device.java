package com.example.toms.assapp.model;

import java.util.Date;
import java.util.List;

public class Device {

    //Atributos
    private String id;
    private String typeDevice;
    private String salesDate;
    private String name;
    private String make;
    private String model;
    private String imei;
    private List<String> photoList;
    private Boolean insured;
    private Boolean finalVerification;
    private String insuranceDate;
    private Integer daysToInsure;
    private Integer hoursToInsure;

    //consturctores

    public Device() {
    }

    public Device(String id, String typeDevice, String name, String make, String model,
                  String salesDate, List<String> photoList, Boolean insured, Boolean finalVerification,
                  String insuranceDate,String imei, Integer daysToInsure,Integer hoursToInsure) {
        this.id = id;
        this.typeDevice = typeDevice;
        this.name = name;
        this.make = make;
        this.model = model;
        this.salesDate = salesDate;
        this.photoList = photoList;
        this.insured = insured;
        this.finalVerification = finalVerification;
        this.insuranceDate = insuranceDate;
        this.imei = imei;
        this.daysToInsure = daysToInsure;
        this.hoursToInsure = hoursToInsure;
    }

    //getter

    public Integer getDaysToInsure() {
        return daysToInsure;
    }

    public String getImei() {
        return imei;
    }

    public String getInsuranceDate() {
        return insuranceDate;
    }

    public Boolean getFinalVerification() {
        return finalVerification;
    }

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

    public Integer getHoursToInsure() {
        return hoursToInsure;
    }


    //setter

    public void setDaysToInsure(Integer daysToInsure) {
        this.daysToInsure = daysToInsure;
    }

    public void setHoursToInsure(Integer hoursToInsure) {
        this.hoursToInsure = hoursToInsure;
    }

    public void setInsuranceDate(String insuranceDate) {
        this.insuranceDate = insuranceDate;
    }

    public void setInsured(Boolean insured) {
        this.insured = insured;
    }

    public void setFinalVerification(Boolean finalVerification) {
        this.finalVerification = finalVerification;
    }

    public void setSalesDate(String salesDate) {
        this.salesDate = salesDate;
    }

    public void setPhotoList(List<String> photoList) {
        this.photoList = photoList;
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
