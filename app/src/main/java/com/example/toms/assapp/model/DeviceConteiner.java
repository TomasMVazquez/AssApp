package com.example.toms.assapp.model;

import java.util.List;

public class DeviceConteiner {

    //atributos
    private List<Device> deviceList;

    //constructor
    public DeviceConteiner(List<Device> deviceList) {
        this.deviceList = deviceList;
    }

    //getter
    public List<Device> getDeviceList() {
        return deviceList;
    }

    //tostring

    @Override
    public String toString() {
        return "DeviceConteiner{" +
                "deviceList=" + deviceList +
                '}';
    }
}
