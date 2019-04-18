package com.example.toms.assapp.view.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.toms.assapp.R;
import com.example.toms.assapp.model.Device;
import com.example.toms.assapp.view.MainActivity;
import com.example.toms.assapp.view.adpater.AdapterDeviceRecycler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.harjot.crollerTest.Croller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class DaysToInsureFragment extends Fragment {

//    public static final String KEY_ID="id";

    //Atributos
    private Integer days;
    private String id;
    private Double price;
    private String dayPrice;

    public DaysToInsureFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_days_to_insure, container, false);

        final TextView crollerDaysChooser = view.findViewById(R.id.crollerDaysChooser);
        Croller crollerDays  = view.findViewById(R.id.crollerDays);

        Bundle bundle = getArguments();
        id = bundle.getString(FragmentDialog.KEY_ID);
        price = bundle.getDouble(FragmentDialog.KEY_PRICE);
        dayPrice = "$ " + String.valueOf(price) + "/día";

        TextView insurancePriceDays = view.findViewById(R.id.insurancePriceDays);
        insurancePriceDays.setText(dayPrice);

        crollerDays.setOnProgressChangedListener(new Croller.onProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress) {
                crollerDaysChooser.setText(String.valueOf(progress));
            }
        });

//        final TextInputLayout textInputDays = view.findViewById(R.id.textInputDays);
//        final EditText daysToInsure = view.findViewById(R.id.daysToInsure);
        Button btnDays = view.findViewById(R.id.btnDays);
        Button btnCancel = view.findViewById(R.id.btnCancel);

//        daysToInsure.setText("30");
//        textInputDays.setError("");
//        daysToInsure.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (!String.valueOf(s).equals("")) {
//                    if (Integer.valueOf(String.valueOf(s)) > 30) {
//                        textInputDays.setError("No puedes asegurar más de 30 días, favor de contratar mensualisado");
//                    } else {
//                        textInputDays.setError("");
//                    }
//                }
//            }
//        });

        final FragmentInterface fragmentInterface = (FragmentInterface) getActivity();
        final InterfaceClose interfaceClose = (InterfaceClose) getActivity();

        btnDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (crollerDaysChooser.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Debes elegir una cantidad de días para asegurar tu equipo", Toast.LENGTH_SHORT).show();
                }else if (Integer.valueOf(crollerDaysChooser.getText().toString()) > 30){
                    Toast.makeText(getActivity(), "No puedes asegurar más de 30 días, favor de contratar mensualisado", Toast.LENGTH_SHORT).show();
                } else{
                    days = Integer.valueOf(String.valueOf(crollerDaysChooser.getText()));
                    fragmentInterface.confirmDays(id, days);
                    interfaceClose.closeDialog();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentInterface.confirmDays(id,0);
                interfaceClose.closeDialog();

            }
        });

        return view;
    }


    public interface FragmentInterface{
        public void confirmDays(String id,Integer days);
    }

    public interface InterfaceClose{
        void closeDialog();
    }

}
