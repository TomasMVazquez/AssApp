package com.example.toms.assapp.view.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.toms.assapp.R;
import com.example.toms.assapp.view.AddNewDevice;
import com.example.toms.assapp.view.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyInsuranceFragment extends Fragment {

    public static final int KEY_ADD_DEVICE = 201;
    public static final String KEY_ID_GUEST = "guest";

    private String idGuest;

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

        idGuest = MainActivity.showId();

        //actions
        fabAddInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(view.getContext(),AddNewDevice.class);
                Bundle bundle = new Bundle();
                bundle.putString(AddNewDevice.KEY_ID_GUEST, idGuest);
                i.putExtras(bundle);
                startActivityForResult(i,KEY_ADD_DEVICE);
            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == KEY_ADD_DEVICE){
            Bundle bundle = data.getExtras();
            idGuest = bundle.getString(KEY_ID_GUEST);

            if (idGuest != null){
                OnFragmentFormNotify onFragmentFormNotify= (OnFragmentFormNotify) getContext();
                onFragmentFormNotify.showIdGuest(idGuest);
            }
        }
    }


    //the guest id of the database
    public static Intent dataBaseId(String id){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ID_GUEST, id);
        intent.putExtras(bundle);
        return intent;
    }

    public interface OnFragmentFormNotify{
        public void showIdGuest(String id);
    }

}
