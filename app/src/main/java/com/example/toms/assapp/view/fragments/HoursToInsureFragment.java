package com.example.toms.assapp.view.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.toms.assapp.R;
import com.sdsmdg.harjot.crollerTest.Croller;

/**
 * A simple {@link Fragment} subclass.
 */
public class HoursToInsureFragment extends Fragment {

    //Atributos
    private Integer hours;
    private String id;
    private Double price;
    private String hourPrice;

    public HoursToInsureFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hours_to_insure, container, false);

        TextView insurancePriceHours = view.findViewById(R.id.insurancePriceHours);
        final TextView crollerHoursChooser = view.findViewById(R.id.crollerHoursChooser);
        Croller crollerHours = view.findViewById(R.id.crollerHours);
        Button btnHour = view.findViewById(R.id.btnHour);
        Button btnCancelHour = view.findViewById(R.id.btnCancelHour);

        Bundle bundle = getArguments();
        id = bundle.getString(FragmentDialog.KEY_ID);
        price = bundle.getDouble(FragmentDialog.KEY_PRICE);
        Double hourlyPrice = (double) Math.round(price * 1.3);
        hourPrice = "$ " + String.valueOf(hourlyPrice) + "/hour";

        insurancePriceHours.setText(hourPrice);

        crollerHours.setOnProgressChangedListener(new Croller.onProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress) {
                crollerHoursChooser.setText(String.valueOf(progress));
            }
        });

        final FragmentInterfaceHour fragmentInterfaceHour = (FragmentInterfaceHour) getActivity();
        final InterfaceCloseHour interfaceCloseHour = (InterfaceCloseHour) getActivity();

        btnHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (crollerHoursChooser.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Debes elegir una cantidad de horas para asegurar tu equipo", Toast.LENGTH_SHORT).show();
                }else if (Integer.valueOf(crollerHoursChooser.getText().toString()) > 24){
                    Toast.makeText(getActivity(), "No puedes asegurar más de 24 horas, favor de contratar días", Toast.LENGTH_SHORT).show();
                } else{
                    hours = Integer.valueOf(String.valueOf(crollerHoursChooser.getText()));
                    fragmentInterfaceHour.confirmHours(id,hours);
                    interfaceCloseHour.closeDialog();
                }
            }
        });

        btnCancelHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentInterfaceHour.confirmHours(id,0);
                interfaceCloseHour.closeDialog();
            }
        });

        return view;
    }

    public interface FragmentInterfaceHour{
        public void confirmHours(String id,Integer Hours);
    }

    public interface InterfaceCloseHour{
        void closeDialog();
    }


}
