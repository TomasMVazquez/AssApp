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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class DaysToInsureFragment extends DialogFragment {

    public static final String KEY_ID="id";

    //Atributos
    private Integer days;
    private String id;

    public DaysToInsureFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_days_to_insure, container, false);

        Bundle bundle = getArguments();
        id = bundle.getString(KEY_ID);

        TextInputLayout textInputDays = view.findViewById(R.id.textInputDays);
        final EditText daysToInsure = view.findViewById(R.id.daysToInsure);
        ImageButton btnDays = view.findViewById(R.id.btnDays);
        ImageButton btnCancel = view.findViewById(R.id.btnCancel);

        daysToInsure.setText("30");
        daysToInsure.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //Integer days = Integer.valueOf(String.valueOf(daysToInsure.getText()));
//                if (days>30){
                    //TODO ver si se puede o no asegurar mas de 30 dias
//                }else {
//
//                }
            }
        });

        final FragmentInterface fragmentInterface = (FragmentInterface) getActivity();

        btnDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (daysToInsure.getText().equals("")){
                    Toast.makeText(getContext(), "Debes elegir una cantidad de días para asegurar tu equipo", Toast.LENGTH_SHORT).show();
                }else {
                    days = Integer.valueOf(String.valueOf(daysToInsure.getText()));
//                    insure(days);
                    fragmentInterface.confirmDays(id,days);
                    Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().remove(DaysToInsureFragment.this).commit();

                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                confirmDays(id,0);
                fragmentInterface.confirmDays(id,0);
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().remove(DaysToInsureFragment.this).commit();
            }
        });

        return view;
    }


    public interface FragmentInterface{
        public void confirmDays(String id,Integer days);
    }


}
