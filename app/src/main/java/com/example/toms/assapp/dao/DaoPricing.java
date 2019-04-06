package com.example.toms.assapp.dao;

import com.example.toms.assapp.util.ResultListener;

public class DaoPricing {


    public void givePricin(String deviceType,ResultListener<Double> resultListener){

        switch (deviceType){

            case "Celular":
                resultListener.finish(12.5);
                break;
            case "TV/LCD/SMART":
                resultListener.finish(15.3);
                break;
            case "Consola":
                resultListener.finish(10.0);
                break;
            case "Monitor PC":
                resultListener.finish(5.0);
                break;
            case "Notebook":
                resultListener.finish(15.8);
                break;
            case "Tablet":
                resultListener.finish(2.5);
                break;
            case "Bicicleta":
                resultListener.finish(5.5);
                break;
            case "Equipo de audio":
                resultListener.finish(5.5);
                break;

        }

    }

}
