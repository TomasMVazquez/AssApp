package com.example.toms.assapp.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.toms.assapp.R;
import com.example.toms.assapp.model.Device;
import com.example.toms.assapp.view.fragments.DaysToInsureFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import pl.aprilapps.easyphotopicker.EasyImage;

public class FinalVerification extends AppCompatActivity implements DaysToInsureFragment.FragmentInterface{

    public static final String KEY_ID_DEVICE_DB = "db";

    public static final int KEY_CAMERA_ONE = 301;
    public static final int KEY_CAMERA_TWO = 302;
    public static final int KEY_CAMERA_THREE = 303;
    public static final int KEY_CAMERA_FOUR = 304;

    private String id;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private DatabaseReference deviceDb;
    private DatabaseReference idDeviceInsured;
    private DatabaseReference idinsuranceDate;
    private DatabaseReference idDaysToInsure;
    private DatabaseReference idVerif;

    private Boolean statusVerificationDB = false;
    private FirebaseStorage mStorage;
    private List<String> photoList = new ArrayList<>();

    private ImageView imageOne;
    private ImageView imageTwo;
    private ImageView imageThree;
    private ImageView imageFour;

    private static EditText addSalesDate;
    private static TextInputLayout addTextInputSalesDate;
    private Switch switchInvoice;
    private Switch switchDevicePhoto;
    private TextView textViewInvoiceTitle;
    private TextView tvTitlePhotos;
    private CardView cardViewImageOne;
    private LinearLayout imagenAgregada;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        id = bundle.getString(KEY_ID_DEVICE_DB);
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();

        verificationDone(id);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (statusVerificationDB){
                    continueActivity();
                }
            }
        }, 4000);

        setContentView(R.layout.activity_final_verification);

        //Gerente
        mStorage = FirebaseStorage.getInstance();

        //Views
        Button btnFinalVerif = findViewById(R.id.btnFinalVerif);

        switchInvoice = findViewById(R.id.switchInvoice);
        addTextInputSalesDate = findViewById(R.id.addTextInputSalesDate);
        addSalesDate = findViewById(R.id.addSalesDate);
        textViewInvoiceTitle = findViewById(R.id.textViewInvoiceTitle);
        cardViewImageOne = findViewById(R.id.cardViewImageOne);
        imageOne = findViewById(R.id.image_one);

        switchDevicePhoto = findViewById(R.id.switchDevicePhoto);
        tvTitlePhotos = findViewById(R.id.tvTitlePhotos);
        imagenAgregada = findViewById(R.id.imagenAgregada);
        imageTwo = findViewById(R.id.image_two);
        imageThree = findViewById(R.id.image_three);
        imageFour = findViewById(R.id.image_four);

        //Starting with invoice
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
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        showTruitonDatePickerDialog(v);
                        return true;
                }
                return false;
            }
        });

        //Continue with Device confirmation
        //checking confirmation
        switchDevicePhoto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                photosChecked();
            }
        });



        //Call camera on click imageview
        imageOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyImage.openChooserWithGallery(FinalVerification.this, "Elegir", KEY_CAMERA_ONE);
            }
        });
        imageTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EasyImage.openChooserWithGallery(FinalVerification.this, "Elegir", KEY_CAMERA_TWO);
            }
        });
        imageThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyImage.openChooserWithGallery(FinalVerification.this, "Elegir", KEY_CAMERA_THREE);
            }
        });
        imageFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyImage.openChooserWithGallery(FinalVerification.this, "Elegir", KEY_CAMERA_FOUR);
            }
        });

        //Al clickear boton Finalizar
        btnFinalVerif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInfo()){
                    final DatabaseReference device = mReference.child(MainActivity.showId()).child(getResources().getString(R.string.device_reference_child)).child(id);
                    device.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Device modifyDevice = dataSnapshot.getValue(Device.class);
                            modifyDevice.setSalesDate(addSalesDate.getText().toString());
                            modifyDevice.setPhotoList(photoList);
                            modifyDevice.setFinalVerification(true);
                            modifyDevice.setInsured(true);
                            SimpleDateFormat y = new SimpleDateFormat("yyyy");
                            SimpleDateFormat d = new SimpleDateFormat("dd");
                            SimpleDateFormat m = new SimpleDateFormat("MM");
                            Date today = new Date();
                            String year = String.valueOf(Integer.valueOf(y.format(today)));
                            String day = d.format(today);
                            String month = m.format(today);
                            String insuranceDate = (day + "/" + month + "/" + year);
                            modifyDevice.setInsuranceDate(insuranceDate);
                            device.setValue(modifyDevice);

                            confirmDays(id);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });

    }

    public void confirmDays(String id){
        Bundle bundle = new Bundle();
        bundle.putString(DaysToInsureFragment.KEY_ID,id);
        DaysToInsureFragment daysToInsureFragment = new DaysToInsureFragment();
        daysToInsureFragment.setArguments(bundle);
        daysToInsureFragment.setCancelable(false);
        FragmentManager fragmentManager = getSupportFragmentManager();
        daysToInsureFragment.show(fragmentManager,"days");
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        finish();
        super.onBackPressed();
    }

    public void verificationDone(String id){

        final DatabaseReference statusVerification = mReference.child(MainActivity.showId()).child(getResources().getString(R.string.device_reference_child)).child(id).child("finalVerification");
        statusVerification.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean check = (Boolean) dataSnapshot.getValue();
                if (check){
                    statusVerificationDB = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public boolean checkInfo() {
        if (!switchInvoice.isChecked()) {
            Toast.makeText(this, getResources().getString(R.string.error_not_invoice_checked), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (addSalesDate.getText().length() == 0) {
            Toast.makeText(this, getResources().getString(R.string.error_not_sales_date), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!switchDevicePhoto.isChecked()) {
            Toast.makeText(this, getResources().getString(R.string.error_not_photo), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (photoList.size() < 4) {
            Toast.makeText(this, getResources().getString(R.string.error_not_photo), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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

    public void photosChecked(){
        if (switchDevicePhoto.isChecked()){
            tvTitlePhotos.setVisibility(View.VISIBLE);
            imagenAgregada.setVisibility(View.VISIBLE);
        }else {
            tvTitlePhotos.setVisibility(View.GONE);
            imagenAgregada.setVisibility(View.GONE);
        }
    }

    //Activity for Result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Progess dialog
        final ProgressDialog prog= new ProgressDialog(FinalVerification.this);
        prog.setTitle("Por favor espere");
        prog.setMessage("Estamos cargando su imagen");
        prog.setCancelable(false);
        prog.setIndeterminate(true);
        prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        //Photos of devices
        EasyImage.handleActivityResult(requestCode, resultCode, data, FinalVerification.this, new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource imageSource, int i) {

            }

            @Override
            public void onImagesPicked(@NonNull List<File> list, EasyImage.ImageSource imageSource, int i) {
                StorageReference raiz = mStorage.getReference();

                if (list.size() > 0) {
                    File file = list.get(0);
                    final Uri uri = Uri.fromFile(file);
                    final Uri uriTemp = Uri.fromFile(new File(uri.getPath()));

                    switch (i) {
                        case KEY_CAMERA_ONE:
                            final StorageReference oneFoto = raiz.child(uriTemp.getLastPathSegment());
                            UploadTask uploadTask = oneFoto.putFile(uriTemp);
                            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    prog.show();
                                    //double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                    //System.out.println("Upload is " + progress + "% done");
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    photoList.add(uriTemp.getLastPathSegment());
                                    Glide.with(FinalVerification.this).load(uri).into(imageOne);
                                    prog.dismiss();
                                }
                            });
                            break;
                        case KEY_CAMERA_TWO:
                            final StorageReference twoFoto = raiz.child(uriTemp.getLastPathSegment());
                            uploadTask = twoFoto.putFile(uriTemp);
                            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    prog.show();
                                    //double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                    //System.out.println("Upload is " + progress + "% done");
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    photoList.add(uriTemp.getLastPathSegment());
                                    Glide.with(FinalVerification.this).load(uri).into(imageTwo);
                                    prog.dismiss();
                                }
                            });
                            break;
                        case KEY_CAMERA_THREE:
                            final StorageReference threeFoto = raiz.child(uriTemp.getLastPathSegment());
                            uploadTask = threeFoto.putFile(uriTemp);
                            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    prog.show();
                                    //double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                    //System.out.println("Upload is " + progress + "% done");
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    photoList.add(uriTemp.getLastPathSegment());
                                    Glide.with(FinalVerification.this).load(uri).into(imageThree);
                                    prog.dismiss();
                                }
                            });
                            break;
                        case KEY_CAMERA_FOUR:
                            final StorageReference fourFoto = raiz.child(uriTemp.getLastPathSegment());
                            uploadTask = fourFoto.putFile(uriTemp);
                            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    prog.show();
                                    //double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                    //System.out.println("Upload is " + progress + "% done");
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    photoList.add(uriTemp.getLastPathSegment());
                                    Glide.with(FinalVerification.this).load(uri).into(imageFour);
                                    prog.dismiss();
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
    public void esperarYCerrar(Integer milisegundos) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

            }
        }, milisegundos);
    }

    @Override
    public void confirmDays(String id, Integer days) {
        if (days>0){
            confirmInsurance(id,days);
            continueActivity();
        }else {
            cancelInsurance(id);
            Toast.makeText(getApplicationContext(), "Su seguro NO se activó", Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
        this.recreate();
    }

    public void cancelInsurance(String idDevice){
        deviceDb = mReference.child(MainActivity.showId()).child(this.getResources().getString(R.string.device_reference_child)).child(idDevice);
        idDeviceInsured = mReference.child(MainActivity.showId()).child(this.getResources().getString(R.string.device_reference_child)).child(idDevice).child("insured");
        idinsuranceDate = mReference.child(MainActivity.showId()).child(this.getResources().getString(R.string.device_reference_child)).child(idDevice).child("insuranceDate");
        idDaysToInsure = mReference.child(MainActivity.showId()).child(this.getResources().getString(R.string.device_reference_child)).child(idDevice).child("daysToInsure");
        idDeviceInsured.setValue(false);
        idinsuranceDate.setValue("");
        idDaysToInsure.setValue(0);
    }

    public void confirmInsurance(String idDevice,Integer days){
        deviceDb = mReference.child(MainActivity.showId()).child(this.getResources().getString(R.string.device_reference_child)).child(idDevice);
        idDeviceInsured = mReference.child(MainActivity.showId()).child(this.getResources().getString(R.string.device_reference_child)).child(idDevice).child("insured");
        idinsuranceDate = mReference.child(MainActivity.showId()).child(this.getResources().getString(R.string.device_reference_child)).child(idDevice).child("insuranceDate");
        idDaysToInsure = mReference.child(MainActivity.showId()).child(this.getResources().getString(R.string.device_reference_child)).child(idDevice).child("daysToInsure");

        idinsuranceDate.setValue(insureDay());
        idDaysToInsure.setValue(days);
        idDeviceInsured.setValue(true);
    }

    public String insureDay(){
        SimpleDateFormat y = new SimpleDateFormat("yyyy");
        SimpleDateFormat d = new SimpleDateFormat("dd");
        SimpleDateFormat m = new SimpleDateFormat("MM");
        Date today = new Date();
        String year = String.valueOf(Integer.valueOf(y.format(today)));
        String day = d.format(today);
        String month = m.format(today);
        String insuranceDate = (day + "/" + month + "/" + year);
        return insuranceDate;
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
                }else {
                    addTextInputSalesDate.setError("");
                    addSalesDate.setText(dateChoosen);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void showTruitonDatePickerDialog(View v) {
        DialogFragment newFragment = new FinalVerification.DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void continueActivity(){
        setResult(Activity.RESULT_OK);
        finish();
    }
}
