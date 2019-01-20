package com.example.toms.assapp.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
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
import com.example.toms.assapp.model.Device;
import com.example.toms.assapp.view.fragments.MyInsuranceFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class AddNewDevice extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String KEY_ID_DB = "db";
    public static final int KEY_CAMERA_ONE=301;
    public static final int KEY_CAMERA_TWO=302;
    public static final int KEY_CAMERA_THREE=303;
    public static final int KEY_CAMERA_FOUR=304;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private FirebaseStorage mStorage;
    private List<String> photoList = new ArrayList<>();
    private String idReferenceGuest;

    private ImageView imageOne;
    private ImageView imageTwo;
    private ImageView imageThree;
    private ImageView imageFour;

    private Spinner spinnerSelectTypeDevice;
    private String deviceType;
    private static EditText addSalesDate;
    private static EditText addName;
    private static TextInputLayout addTextInputSalesDate;
    private EditText addMake;
    private EditText addModel;
    private Switch switchInvoice;
    private TextView textViewInvoiceTitle;
    private CardView cardViewImageOne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_device);

        //firebase
        mDatabase = FirebaseDatabase.getInstance();
        mReference  = mDatabase.getReference();
        //Gerente
        mStorage = FirebaseStorage.getInstance();
        //Raiz del Storage
        StorageReference raiz = mStorage.getReference();

//        //bundle
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle.getString(KEY_ID_DB)!=null) {
            idReferenceGuest = bundle.getString(KEY_ID_DB);
        }

//        idReferenceGuest = MainActivity.showId();

        //Views
        Button fabAddDevice = findViewById(R.id.fabAddDevice);
        spinnerSelectTypeDevice = findViewById(R.id.spinnerSelectTypeDevice);
        imageOne = findViewById(R.id.image_one);
        imageTwo = findViewById(R.id.image_two);
        imageThree = findViewById(R.id.image_three);
        imageFour = findViewById(R.id.image_four);
        TextInputLayout addTextInputName = findViewById(R.id.addTextInputName);
        addTextInputSalesDate = findViewById(R.id.addTextInputSalesDate);
        TextInputLayout addTextInputMake = findViewById(R.id.addTextInputMake);
        TextInputLayout addTextInputModel = findViewById(R.id.addTextInputModel);
        addName = findViewById(R.id.addDeviceName);
        addSalesDate = findViewById(R.id.addSalesDate);
        addMake = findViewById(R.id.addMake);
        addModel = findViewById(R.id.addModel);
        switchInvoice = findViewById(R.id.switchInvoice);
        textViewInvoiceTitle = findViewById(R.id.textViewInvoiceTitle);
        cardViewImageOne = findViewById(R.id.cardViewImageOne);

        //checking invoice
        switchInvoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               invoiceChecked();
            }
        });

        //Adding date piccker for sales date
            //Stting this on Touch will not allow blind people to use the app (https://www.youtube.com/watch?v=1by5J7c5Vz4&feature=youtu.be)
        addSalesDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        showTruitonDatePickerDialog(v);
                        return true;
                }
                return false;
            }
        });

        //Seleccionar los items del spinner
        ArrayList<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("Elige el tipo de dispositivo");
        spinnerArray.add("TV/LCD/SMART");
        spinnerArray.add("Consola");
        spinnerArray.add("Monitor PC");
        spinnerArray.add("Notebook");
        spinnerArray.add("Tablet");
        spinnerArray.add("Celular");
        spinnerArray.add("Bicicleta");
        spinnerArray.add("Equipo de audio");

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,spinnerArray);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSelectTypeDevice.setAdapter(adapterSpinner);
        spinnerSelectTypeDevice.setOnItemSelectedListener(this);

        //Call camera on click imageview
        imageOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyImage.openChooserWithGallery(AddNewDevice.this,"Elegir",KEY_CAMERA_ONE);
            }
        });
        imageTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyImage.openChooserWithGallery(AddNewDevice.this,"Elegir",KEY_CAMERA_TWO);
            }
        });
        imageThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyImage.openChooserWithGallery(AddNewDevice.this,"Elegir",KEY_CAMERA_THREE);
            }
        });
        imageFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyImage.openChooserWithGallery(AddNewDevice.this,"Elegir",KEY_CAMERA_FOUR);
            }
        });

        //Insert Button
        fabAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInfo()) {
                    Device newDevice = new Device("",deviceType,addName.getText().toString(),
                            addMake.getText().toString(),addModel.getText().toString(),addSalesDate.getText().toString(),
                            photoList,false);
                    addDeviceToDataBase(newDevice);

                    Intent data = MyInsuranceFragment.dataBaseId(idReferenceGuest);
                    setResult(Activity.RESULT_OK,data);
                    finish();
                }
            }
        });
    }

    //Button addDevice
    public boolean checkInfo(){
        if (spinnerSelectTypeDevice.getSelectedItemPosition()==0){
            Toast.makeText(this, getResources().getString(R.string.error_not_device_choosen), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!switchInvoice.isChecked()){
            Toast.makeText(this, getResources().getString(R.string.error_not_invoice_checked), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (addSalesDate.getText().length()==0){
            Toast.makeText(this, getResources().getString(R.string.error_not_sales_date), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (addName.getText().length()==0){
            Toast.makeText(this, getResources().getString(R.string.error_not_enough_info), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (addMake.getText().length()==0){
            Toast.makeText(this, getResources().getString(R.string.error_not_enough_info), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (addModel.getText().length()==0){
            Toast.makeText(this, getResources().getString(R.string.error_not_enough_info), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (photoList.size()<4){
            Toast.makeText(this, getResources().getString(R.string.error_not_photo), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }



    //Activity for Result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Photos of devices
        EasyImage.handleActivityResult(requestCode, resultCode, data, AddNewDevice.this, new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource imageSource, int i) {

            }

            @Override
            public void onImagesPicked(@NonNull List<File> list, EasyImage.ImageSource imageSource, int i) {
                StorageReference raiz = mStorage.getReference();

                if (list.size()>0) {
                    File file = list.get(0);
                    final Uri uri = Uri.fromFile(file);
                    final Uri uriTemp = Uri.fromFile(new File(uri.getPath()));

                    switch (i) {
                        case KEY_CAMERA_ONE:
                            final StorageReference oneFoto = raiz.child(uriTemp.getLastPathSegment());
                            UploadTask uploadTask = oneFoto.putFile(uriTemp);
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    photoList.add(uriTemp.getLastPathSegment());
                                    Glide.with(AddNewDevice.this).load(uri).into(imageOne);
                                }
                            });
                            break;
                        case KEY_CAMERA_TWO:
                            final StorageReference twoFoto = raiz.child(uriTemp.getLastPathSegment());
                            uploadTask = twoFoto.putFile(uriTemp);
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    photoList.add(uriTemp.getLastPathSegment());
                                    Glide.with(AddNewDevice.this).load(uri).into(imageTwo);
                                }
                            });
                            break;
                        case KEY_CAMERA_THREE:
                            final StorageReference threeFoto = raiz.child(uriTemp.getLastPathSegment());
                            uploadTask = threeFoto.putFile(uriTemp);
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    photoList.add(uriTemp.getLastPathSegment());
                                    Glide.with(AddNewDevice.this).load(uri).into(imageThree);
                                }
                            });
                            break;
                        case KEY_CAMERA_FOUR:
                            final StorageReference fourFoto = raiz.child(uriTemp.getLastPathSegment());
                            uploadTask = fourFoto.putFile(uriTemp);
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    photoList.add(uriTemp.getLastPathSegment());
                                    Glide.with(AddNewDevice.this).load(uri).into(imageFour);
                                }
                            });
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onCanceled(EasyImage.ImageSource imageSource, int i) {

            }
        });


    }

    //Spinner to select the type
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position!=0){
            deviceType = parent.getItemAtPosition(position).toString();
            //Toast.makeText(this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "Debes seleccionar el tipo de dispositivo", Toast.LENGTH_SHORT).show();
    }


    //Date Picker Comands - for purchase date
    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            // Do something with the date chosen by the user
            String dateChoosen=(dayOfMonth + "/" + (month + 1) + "/" + year);
            SimpleDateFormat simpledateformat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date salesDate = simpledateformat.parse(dateChoosen);
                SimpleDateFormat y = new SimpleDateFormat("yyyy");
                SimpleDateFormat d = new SimpleDateFormat("dd");
                SimpleDateFormat m = new SimpleDateFormat("MM");
                Date today = new Date();
                String limitYear = String.valueOf(Integer.valueOf(y.format(today))-1);
                String limitDay = d.format(today);
                String limitMonth = m.format(today);
                Date limitDate = simpledateformat.parse((limitDay + "/" + limitMonth + "/" + limitYear));

                if (salesDate.before(limitDate)){
                    addTextInputSalesDate.setError(getResources().getString(R.string.sales_date_error));
                    addSalesDate.setText("");
                    addName.requestFocus();
                }else {
                    addTextInputSalesDate.setError("");
                    addSalesDate.setText(dateChoosen);
                    addName.requestFocus();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void showTruitonDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    //Switch
    public void invoiceChecked(){
        if (switchInvoice.isChecked()){
            textViewInvoiceTitle.setVisibility(View.VISIBLE);
            addSalesDate.setVisibility(View.VISIBLE);
            addTextInputSalesDate.setVisibility(View.VISIBLE);
            cardViewImageOne.setVisibility(View.VISIBLE);
            imageOne.setVisibility(View.VISIBLE);
        }else {
            textViewInvoiceTitle.setVisibility(View.GONE);
            addSalesDate.setVisibility(View.GONE);
            addTextInputSalesDate.setVisibility(View.GONE);
            cardViewImageOne.setVisibility(View.GONE);
            imageOne.setVisibility(View.GONE);
        }
    }

    //Add to Database Firebase
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
                ,device.getSalesDate(),device.getPhotoList(),device.getInsured()));
    }

}
