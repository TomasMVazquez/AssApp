package com.example.toms.assapp.view.fragments;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.example.toms.assapp.R;
import com.example.toms.assapp.controller.ControllerPricing;
import com.example.toms.assapp.util.ResultListener;
import com.example.toms.assapp.view.FinalVerification;
import com.example.toms.assapp.view.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDialog extends DialogFragment implements DaysToInsureFragment.InterfaceClose {

    public static final String KEY_ID="id";
    public static final String KEY_PRICE="price";

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private ProgressDialog prog;

    private String id;
    private String deviceType;
    private Double price;

    public FragmentDialog() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT; //ViewGroup.LayoutParams.MATCH_PARENT //700
        params.height = ViewGroup.LayoutParams.MATCH_PARENT; //ViewGroup.LayoutParams.MATCH_PARENT //680
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState){
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //Progess dialog
        prog= new ProgressDialog(getActivity());
        prog.setTitle("Por favor espere");
        prog.setMessage("Estamos cargando su imagen");
        prog.setCancelable(false);
        prog.setIndeterminate(true);
        prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        prog.show();
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_dialog, container, false);

        Bundle bundle = getArguments();
        id = bundle.getString(KEY_ID);

        mDatabase = FirebaseDatabase.getInstance();
        mReference  = mDatabase.getReference();
        DatabaseReference type = mReference.child(MainActivity.showId()).child(this.getResources().getString(R.string.device_reference_child)).child(id).child("typeDevice");

        final ControllerPricing controllerPricing = new ControllerPricing();

        type.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                deviceType = dataSnapshot.getValue(String.class);
                controllerPricing.givePricing(deviceType, new ResultListener<Double>() {
                    @Override
                    public void finish(Double resultado) {
                        price = resultado;
                        prog.dismiss();
                        // tab slider
                        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
                        // Set up the ViewPager with the sections adapter.
                        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);

                        viewPager.setAdapter(sectionsPagerAdapter);
                        viewPager.setCurrentItem(1);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Bundle bundle = new Bundle();
            bundle.putString(KEY_ID,id);
            bundle.putDouble(KEY_PRICE,price);

            if (position == 0){
                // find first fragment...
                HoursToInsureFragment hoursToInsureFragment = new HoursToInsureFragment();
                hoursToInsureFragment.setArguments(bundle);
                return hoursToInsureFragment;
            }
            if (position == 1){
                // find first fragment...
                DaysToInsureFragment daysToInsureFragment = new DaysToInsureFragment();
                daysToInsureFragment.setArguments(bundle);
                return daysToInsureFragment;
            }
            if (position == 2){
                // find first fragment...
                MonthToInsureFragment monthToInsureFragment = new MonthToInsureFragment();
                monthToInsureFragment.setArguments(bundle);
                return monthToInsureFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "On Demand Horas";
                case 1:
                    return "On Demand Días";
                case 2:
                    return "Mensual";
            }
            return null;
        }
    }

    public void closeDialog(){
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().remove(FragmentDialog.this).commit();
    }
}
