package com.example.toms.assapp.controller;

import com.example.toms.assapp.dao.DaoPricing;
import com.example.toms.assapp.util.ResultListener;

public class ControllerPricing {

    public void givePricing(String deviceType, final ResultListener<Double> resultListener){

        DaoPricing daoPricing = new DaoPricing();
        daoPricing.givePricin(deviceType, new ResultListener<Double>() {
            @Override
            public void finish(Double resultado) {
                resultListener.finish(resultado);
            }
        });

    }

}
