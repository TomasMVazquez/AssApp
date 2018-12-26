package com.example.toms.assapp.view.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.toms.assapp.R;
import com.example.toms.assapp.view.AddNewDevice;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyInsuranceFragment extends Fragment {

    public static final int KEY_ADD_DEVICE = 201;

    public MyInsuranceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_my_insurance, container, false);

        //Views
        FloatingActionButton fabAddInsurance = view.findViewById(R.id.fabAddInsurance);



        //actions
        fabAddInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(view.getContext(),AddNewDevice.class);
                startActivityForResult(i,KEY_ADD_DEVICE);
            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == KEY_ADD_DEVICE){
            Toast.makeText(getActivity(), "Funciona" + requestCode, Toast.LENGTH_SHORT).show();
        }

    }

}
