package com.example.toms.assapp.dao;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.toms.assapp.R;
import com.example.toms.assapp.model.Device;
import com.example.toms.assapp.model.DeviceConteiner;
import com.example.toms.assapp.util.ResultListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DaoFirebaseDataBase {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private List<Device> deviceList = new ArrayList<>();
    private DeviceConteiner deviceConteiner;

    public void giveDevice(Context context, String idDataBase, final ResultListener<List<Device>> listenerController){

        mDatabase = FirebaseDatabase.getInstance();
        mReference  = mDatabase.getReference();

        mReference.child(idDataBase).child(context.getResources().getString(R.string.device_reference_child)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapShot : dataSnapshot.getChildren()){
                    Device addDevice = childSnapShot.getValue(Device.class);
                    deviceList.add(addDevice);
                }
                listenerController.finish(deviceList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        mReference.child(idDataBase).child(context.getResources().getString(R.string.device_reference_child)).addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                Device device = dataSnapshot.getValue(Device.class);
//                deviceList.add(device);
//                listenerController.finish(deviceList);
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
    }


}
