package com.example.toms.assapp.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.toms.assapp.R;
import com.example.toms.assapp.controller.ControllerPricing;
import com.example.toms.assapp.model.Device;
import com.example.toms.assapp.util.ResultListener;
import com.example.toms.assapp.view.fragments.MyInsuranceFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pl.aprilapps.easyphotopicker.EasyImage;

public class AddNewDevice extends AppCompatActivity implements AdapterView.OnItemSelectedListener, ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String KEY_ID_DB = "db";
    public static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 500;
    public static final int KEY_CAMERA_ONE = 301;
    public static final int KEY_CAMERA_TWO = 302;
    public static final int KEY_CAMERA_THREE = 303;
    public static final int KEY_CAMERA_FOUR = 304;

    private TelephonyManager telephonyManager;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private FirebaseStorage mStorage;
    private List<String> photoList = new ArrayList<>();
    private String idReferenceGuest;


    private Spinner spinnerSelectTypeDevice;
    private String deviceType;
    private static EditText addName;
    private EditText addMake;
    private EditText addModel;
    private TextView imeiCel;
    private TextView insurancePrice;
    private TextView insurancePriceMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_device);

        //firebase
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        //Gerente
        mStorage = FirebaseStorage.getInstance();
        //Raiz del Storage
        StorageReference raiz = mStorage.getReference();

//        //bundle
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle.getString(KEY_ID_DB) != null) {
            idReferenceGuest = bundle.getString(KEY_ID_DB);
        }

//        idReferenceGuest = MainActivity.showId();

        //Views
        Button fabAddDevice = findViewById(R.id.fabAddDevice);
        spinnerSelectTypeDevice = findViewById(R.id.spinnerSelectTypeDevice);
        TextInputLayout addTextInputName = findViewById(R.id.addTextInputName);
        TextInputLayout addTextInputMake = findViewById(R.id.addTextInputMake);
        TextInputLayout addTextInputModel = findViewById(R.id.addTextInputModel);
        addName = findViewById(R.id.addDeviceName);
        addMake = findViewById(R.id.addMake);
        addModel = findViewById(R.id.addModel);
        imeiCel = findViewById(R.id.imeiCel);
        insurancePrice = findViewById(R.id.insurancePrice);
        insurancePriceMonth = findViewById(R.id.insurancePriceMonth);

        //Seleccionar los items del spinner
        ArrayList<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("Elige el tipo de dispositivo");
        spinnerArray.add("Celular");
        spinnerArray.add("TV/LCD/SMART");
        spinnerArray.add("Consola");
        spinnerArray.add("Monitor PC");
        spinnerArray.add("Notebook");
        spinnerArray.add("Tablet");
        spinnerArray.add("Bicicleta");
        spinnerArray.add("Equipo de audio");

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSelectTypeDevice.setAdapter(adapterSpinner);
        spinnerSelectTypeDevice.setOnItemSelectedListener(this);

        //Insert Button
        fabAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInfo()) {
                    Device newDevice = new Device("", deviceType, addName.getText().toString(),
                            addMake.getText().toString(), addModel.getText().toString(), "",
                            photoList, false,false,"",imeiCel.getText().toString(),0);

                    if (newDevice.getTypeDevice().equals("Celular")) {
                        checkImei(newDevice);
                    }else{
                        addDeviceToDataBase(newDevice);
                        Intent data = MyInsuranceFragment.dataBaseId(idReferenceGuest);
                        setResult(Activity.RESULT_OK, data);
                        finish();
                    }
                }
            }
        });
    }

    //Button addDevice
    private boolean checkInfo() {
        if (spinnerSelectTypeDevice.getSelectedItemPosition() == 0) {
            Toast.makeText(this, getResources().getString(R.string.error_not_device_choosen), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (addName.getText().length() == 0) {
            Toast.makeText(this, getResources().getString(R.string.error_not_enough_info), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (addMake.getText().length() == 0) {
            Toast.makeText(this, getResources().getString(R.string.error_not_enough_info), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (addModel.getText().length() == 0) {
            Toast.makeText(this, getResources().getString(R.string.error_not_enough_info), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    //Spinner to select the type
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position != 0) {
            deviceType = parent.getItemAtPosition(position).toString();

            ControllerPricing controllerPricing = new ControllerPricing();
            controllerPricing.givePricing(deviceType, new ResultListener<Double>() {
                @Override
                public void finish(Double resultado) {
                    String price = "$ " + resultado + " /día";
                    Double monthlyPrice = (double) Math.round((resultado * 30) * 0.8);
                    String monthlyPrecio = "$ " + String.valueOf(monthlyPrice) + "/mes";
                    insurancePriceMonth.setText(monthlyPrecio);
                    insurancePrice.setText(price);
                }
            });

            //Toast.makeText(this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            if (position == 1) {
                addMake.setText(Build.MANUFACTURER);
                addModel.setText(Build.MODEL);
                telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                imeiCel.setVisibility(View.VISIBLE);
                int permissionCheck = ContextCompat.checkSelfPermission(AddNewDevice.this,
                        Manifest.permission.READ_PHONE_STATE);

                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_PHONE_STATE},
                            MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                } else {
                    imeiCel.setText(telephonyManager.getDeviceId());
                }

            }else {
                imeiCel.setText("0");
                addMake.setText("");
                addModel.setText("");
                imeiCel.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "Debes seleccionar el tipo de dispositivo", Toast.LENGTH_SHORT).show();
    }

    //Add to Database Firebase
    //check if IMEI already exists
    public void checkImei(final Device device){
        DatabaseReference devices;

        if (idReferenceGuest != null) {
            devices = mReference.child(idReferenceGuest).child(getResources().getString(R.string.device_reference_child));
            devices.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Boolean check = false;
                    for (DataSnapshot childSnapShot : dataSnapshot.getChildren()) {
                        Device checkDevice = childSnapShot.getValue(Device.class);
                        if (checkDevice.getImei().equals(device.getImei())) {
                            check = true;
                        }
                    }
                    if (check) {
                        Toast.makeText(AddNewDevice.this, "Este equipo ya esta en su lista", Toast.LENGTH_SHORT).show();
                        Intent data = MyInsuranceFragment.dataBaseId(idReferenceGuest);
                        setResult(Activity.RESULT_CANCELED, data);
                        finish();
                    } else {
                        addDeviceToDataBase(device);
                        Intent data = MyInsuranceFragment.dataBaseId(idReferenceGuest);
                        setResult(Activity.RESULT_OK, data);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
            addDeviceToDataBase(device);
            Intent data = MyInsuranceFragment.dataBaseId(idReferenceGuest);
            setResult(Activity.RESULT_OK, data);
            finish();
        }
    }

    public void addDeviceToDataBase(Device device){
        DatabaseReference idDevices;
        if (idReferenceGuest==null){
            idReferenceGuest = "guest" + (new Date()).toString();
            idDevices = mReference.child(idReferenceGuest).child(getResources().getString(R.string.device_reference_child)).push();
        }else {
            idDevices = mReference.child(idReferenceGuest).child(getResources().getString(R.string.device_reference_child)).push();
        }

        String idDataBase = idDevices.getKey();
        device.setId(idDataBase);

        idDevices.setValue(new Device(device.getId(),device.getTypeDevice(),device.getName(),device.getMake(),device.getModel()
                ,device.getSalesDate(),device.getPhotoList(),device.getInsured(),device.getFinalVerification(),device.getInsuranceDate(),device.getImei(),0));
    }

    //Permiso para sacar IMEI
    @SuppressLint({"MissingPermission", "NewApi"})
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults){

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    imeiCel.setText(telephonyManager.getImei());
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Necesitamos tu permiso para tomar el IMEI de tu equipo", Toast.LENGTH_SHORT).show();
                    spinnerSelectTypeDevice.setSelection(0);
                    addMake.setText("");
                    addModel.setText("");
                    imeiCel.setVisibility(View.GONE);
                }
                break;
        }

    }

}
