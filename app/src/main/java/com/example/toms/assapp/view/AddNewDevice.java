package com.example.toms.assapp.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pl.aprilapps.easyphotopicker.EasyImage;

public class AddNewDevice extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final int KEY_CAMERA_ONE=301;
    public static final int KEY_CAMERA_TWO=302;
    public static final int KEY_CAMERA_THREE=303;
    public static final int KEY_CAMERA_FOUR=304;

    private ImageView imageOne;
    private ImageView imageTwo;
    private ImageView imageThree;
    private ImageView imageFour;

    private Spinner spinnerSelectTypeDevice;
    private static EditText addSalesDate;
    private static EditText addName;
    private static TextInputLayout addTextInputSalesDate;
    private Switch switchInvoice;
    private TextView textViewInvoiceTitle;
    private CardView cardViewImageOne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_device);

        //Views
        Button fabAddDevice = findViewById(R.id.fabAddDevice);
//        FloatingActionButton fabAddImage = findViewById(R.id.fabAddImage);
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
        final EditText addMake = findViewById(R.id.addMake);
        EditText addModel = findViewById(R.id.addModel);
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
        spinnerArray.add("Grande Electrodomestico");
        spinnerArray.add("Pequeño Electrodomestico");
        spinnerArray.add("TV/LCD/SMART");
        spinnerArray.add("Consola");
        spinnerArray.add("Tablet");
        spinnerArray.add("Celular");
        spinnerArray.add("Bicicleta");
        spinnerArray.add("Electrónicos");

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
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            }
        });

    }

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

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, AddNewDevice.this, new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource imageSource, int i) {

            }

            @Override
            public void onImagesPicked(@NonNull List<File> list, EasyImage.ImageSource imageSource, int i) {
                if (list.size()>0) {
                    File file = list.get(0);
                    Uri uri = Uri.fromFile(file);
                    switch (i) {
                        case KEY_CAMERA_ONE:
                            Glide.with(AddNewDevice.this).load(uri).into(imageOne);
                            break;
                        case KEY_CAMERA_TWO:
                            Glide.with(AddNewDevice.this).load(uri).into(imageTwo);
                            break;
                        case KEY_CAMERA_THREE:
                            Glide.with(AddNewDevice.this).load(uri).into(imageThree);
                            break;
                        case KEY_CAMERA_FOUR:
                            Glide.with(AddNewDevice.this).load(uri).into(imageFour);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position!=0){
            Toast.makeText(this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "Debes seleccionar el tipo de dispositivo", Toast.LENGTH_SHORT).show();
    }


    //Date Picker Comands
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
}
