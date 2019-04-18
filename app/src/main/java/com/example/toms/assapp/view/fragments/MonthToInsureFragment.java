package com.example.toms.assapp.view.fragments;


import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.toms.assapp.R;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class MonthToInsureFragment extends Fragment {

    //Atributos
    private Integer month;
    private String id;
    private Double price;
    private String monthPrice;

    public MonthToInsureFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_month_to_insure, container, false);

        Bundle bundle = getArguments();
        id = bundle.getString(FragmentDialog.KEY_ID);
        price = bundle.getDouble(FragmentDialog.KEY_PRICE);
        Double monthlyPrice = (double) Math.round((price * 30) * 0.8);
        monthPrice = "$ " + String.valueOf(monthlyPrice) + "/mes";

        TextView insurancePriceMonth = view.findViewById(R.id.insurancePriceMonth);
        insurancePriceMonth.setText(monthPrice);

        final TextInputLayout textInputMonths = view.findViewById(R.id.textInputMonths);
        final EditText monthsToInsure = view.findViewById(R.id.monthsToInsure);
        Button btnMonth = view.findViewById(R.id.btnMonth);
        Button btnCancelMonth = view.findViewById(R.id.btnCancelMonth);

        final FragmentInterfaceMonth fragmentInterfaceMonth = (FragmentInterfaceMonth) getActivity();
        final InterfaceCloseMonth interfaceCloseMonth = (InterfaceCloseMonth) getActivity();

        monthsToInsure.setText("12");
        textInputMonths.setError("");

        monthsToInsure.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!String.valueOf(s).equals("")) {
                    if (Integer.valueOf(String.valueOf(s)) > 12) {
                        textInputMonths.setError("No puedes asegurar más de 12 meses");
                    } else {
                        textInputMonths.setError("");
                    }
                }
            }
        });

        btnMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (monthsToInsure.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Debes elegir una cantidad de meses para asegurar tu equipo", Toast.LENGTH_SHORT).show();
                }else if (Integer.valueOf(monthsToInsure.getText().toString()) > 12) {
                    Toast.makeText(getActivity(), "No puedes asegurar más de 12 meses", Toast.LENGTH_SHORT).show();
                } else {
                    fragmentInterfaceMonth.confirmDays(id, insureMonths(monthsToInsure.getText().toString()));
                    interfaceCloseMonth.closeDialog();
                }
            }
        });

        btnCancelMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentInterfaceMonth.confirmDays(id,0);
                interfaceCloseMonth.closeDialog();
            }
        });

        return view;
    }

    public Integer insureMonths(String meses){
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.MONTH, Integer.parseInt(meses));
        cal.add(Calendar.DAY_OF_MONTH,-1);
        Date modifiedDate = cal.getTime();

        int days = daysBetween(today,modifiedDate);

        return days;
    }

    public int daysBetween(Date d1, Date d2) {
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    public interface FragmentInterfaceMonth{
        public void confirmDays(String id,Integer days);
    }

    public interface InterfaceCloseMonth{
        void closeDialog();
    }

}
