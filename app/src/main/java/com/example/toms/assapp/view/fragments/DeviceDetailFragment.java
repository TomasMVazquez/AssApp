package com.example.toms.assapp.view.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.toms.assapp.R;
import com.example.toms.assapp.controller.ControllerFirebaseDataBase;
import com.example.toms.assapp.controller.ControllerPricing;
import com.example.toms.assapp.model.Device;
import com.example.toms.assapp.util.ResultListener;
import com.example.toms.assapp.view.FinalVerification;
import com.example.toms.assapp.view.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeviceDetailFragment extends Fragment {

    public static final String KEY_DEVICE = "device";
    public static final String KEY_POSITION = "position";

    private FirebaseStorage mStorage;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private Device device;
    private static Context context;

    public DeviceDetailFragment() {
        // Required empty public constructor
    }

    //constructor
    public static DeviceDetailFragment giveDevice(Context contextView, int fragmentNumber, Device device){
        context = contextView;
        DeviceDetailFragment firstDevice = new DeviceDetailFragment();
        Bundle args = new Bundle();
        args.putString(KEY_DEVICE,device.getId());
        firstDevice.setArguments(args);
        return firstDevice;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_device_detail, container, false);

        //firebase
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();

        final TextView titleDetail = view.findViewById(R.id.titleDetail);
        final ImageView imageDetail = view.findViewById(R.id.imageDetail);
        final TextView tvDetailType = view.findViewById(R.id.tvDetailType);
        final TextView tvDetailMake = view.findViewById(R.id.tvDetailMake);
        final TextView tvDetailModel = view.findViewById(R.id.tvDetailModel);
        final TextView tvDetailPrice = view.findViewById(R.id.tvDetailPrice);
        final TextView tvDetailTimeInsured = view.findViewById(R.id.tvDetailTimeInsured);

        TextView link =  view.findViewById(R.id.tvLink);
        link.setMovementMethod(LinkMovementMethod.getInstance());

        Bundle bundle = getArguments();
        final String idDevice = bundle.getString(KEY_DEVICE);

        ControllerFirebaseDataBase controllerFirebaseDataBase = new ControllerFirebaseDataBase();
        controllerFirebaseDataBase.giveDeviceList(getContext(), MainActivity.showId(), new ResultListener<List<Device>>() {
            @Override
            public void finish(List<Device> resultado) {
                for (Device newDevice:resultado) {
                    if (newDevice.getId().equals(idDevice)){

                        titleDetail.setText(newDevice.getName());

                        tvDetailType.setText(newDevice.getTypeDevice());
                        tvDetailMake.setText(newDevice.getMake());
                        tvDetailModel.setText(newDevice.getModel());
                        ControllerPricing controllerPricing = new ControllerPricing();
                        controllerPricing.givePricing(newDevice.getTypeDevice(), new ResultListener<Double>() {
                            @Override
                            public void finish(Double resultado) {
                                String pricing = "$ " + resultado.toString() + " /mes";
                                tvDetailPrice.setText(pricing);
                            }
                        });

                        //Gerente
                        mStorage = FirebaseStorage.getInstance();
                        //Raiz del Storage
                        StorageReference raiz = mStorage.getReference();

                        if(newDevice.getPhotoList()!=null) {
                            StorageReference imageReference = raiz.child(newDevice.getPhotoList().get(1));
                            imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(context).load(uri).into(imageDetail);
                                }
                            });
                        }

                        tvDetailTimeInsured.setText(daysCalculation(newDevice));
                    }
                }
            }
        });

//        DatabaseReference idDataBase = mReference.child(MainActivity.showId()).child(getResources().getString(R.string.device_reference_child)).child(idDevice);
//        idDataBase.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                device = dataSnapshot.getValue(Device.class);
//                titleDetail.setText(device.getName());
//
//                tvDetailType.setText(device.getTypeDevice());
//                tvDetailMake.setText(device.getMake());
//                tvDetailModel.setText(device.getModel());
//                ControllerPricing controllerPricing = new ControllerPricing();
//                controllerPricing.givePricing(device.getTypeDevice(), new ResultListener<Double>() {
//                    @Override
//                    public void finish(Double resultado) {
//                        String pricing = "$ " + resultado.toString() + " /mes";
//                        tvDetailPrice.setText(pricing);
//                    }
//                });
//
//                //Gerente
//                mStorage = FirebaseStorage.getInstance();
//                //Raiz del Storage
//                StorageReference raiz = mStorage.getReference();
//
//                if(device.getPhotoList()!=null) {
//                    StorageReference imageReference = raiz.child(device.getPhotoList().get(1));
//                    imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            Glide.with(getContext()).load(uri).into(imageDetail);
//                        }
//                    });
//                }else {
//                    Glide.with(getContext()).load(getContext().getDrawable(R.drawable.logo_solo)).into(imageDetail);
//                }
//
//                tvDetailTimeInsured.setText(daysCalculation(device));
//                prog.dismiss();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//

        return view;
    }

    public String daysCalculation(Device device){
        String insuranceDate = device.getInsuranceDate();
        if (!insuranceDate.isEmpty()){

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date myDate = null;
            try {
                myDate = dateFormat.parse(insuranceDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Date today = new Date();
            long timeDiff = today.getTime() - myDate.getTime();
            TimeUnit unit = TimeUnit.DAYS;
            long diference = unit.convert(timeDiff,TimeUnit.MILLISECONDS);
            long daysRestantes = 395 - diference;

            String time = daysRestantes + " días restantes";

//            timeInsured.setText(time);
            return time;
        }else {
//            timeInsured.setText("");
            return "";
        }
    }


}
