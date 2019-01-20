package com.example.toms.assapp.controller;

import android.content.Context;

import com.example.toms.assapp.dao.DaoFirebaseDataBase;
import com.example.toms.assapp.model.Device;
import com.example.toms.assapp.util.ResultListener;

import java.util.ArrayList;
import java.util.List;

public class ControllerFirebaseDataBase {

    private List<Device> deviceList = new ArrayList<>();

    public void giveDeviceList(Context context, String idDataBase, final ResultListener<List<Device>> listResultListener){

        DaoFirebaseDataBase daoFirebaseDataBase = new DaoFirebaseDataBase();

        daoFirebaseDataBase.giveDevice(context, idDataBase, new ResultListener<List<Device>>() {
            @Override
            public void finish(List<Device> resultado) {
                listResultListener.finish(resultado);
            }
        });

    }

}
