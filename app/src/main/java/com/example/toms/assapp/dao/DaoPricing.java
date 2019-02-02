package com.example.toms.assapp.dao;

import com.example.toms.assapp.util.ResultListener;

public class DaoPricing {


    public void givePricin(String deviceType,ResultListener<Double> resultListener){

        switch (deviceType){

            case "Celular":
                resultListener.finish(25.5);
                break;
            case "TV/LCD/SMART":
                resultListener.finish(35.5);
                break;
            case "Consola":
                resultListener.finish(45.5);
                break;
            case "Monitor PC":
                resultListener.finish(55.5);
                break;
            case "Notebook":
                resultListener.finish(65.5);
                break;
            case "Tablet":
                resultListener.finish(75.5);
                break;
            case "Bicicleta":
                resultListener.finish(85.5);
                break;
            case "Equipo de audio":
                resultListener.finish(95.5);
                break;

        }

    }

}
