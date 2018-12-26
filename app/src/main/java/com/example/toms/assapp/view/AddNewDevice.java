package com.example.toms.assapp.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.toms.assapp.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.aprilapps.easyphotopicker.EasyImage;

public class AddNewDevice extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final int KEY_CAMERA=301;

    private Integer camera = 1;
    private ImageView imageOne;
    private ImageView imageTwo;
    private ImageView imageThree;
    private ImageView imageFour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_device);

        //Views
        Button fabAddDevice = findViewById(R.id.fabAddDevice);
        FloatingActionButton fabAddImage = findViewById(R.id.fabAddImage);
        Spinner spinnerSelectTypeDevice = findViewById(R.id.spinnerSelectTypeDevice);
        imageOne = findViewById(R.id.image_one);
        imageTwo = findViewById(R.id.image_two);
        imageThree = findViewById(R.id.image_three);
        imageFour = findViewById(R.id.image_four);
        TextInputLayout addTextInputName = findViewById(R.id.addTextInputName);
        TextInputLayout addTextInputSalesDate = findViewById(R.id.addTextInputSalesDate);
        TextInputLayout addTextInputMake = findViewById(R.id.addTextInputMake);
        TextInputLayout addTextInputModel = findViewById(R.id.addTextInputModel);
        EditText addName = findViewById(R.id.addName);
        EditText addSalesDate = findViewById(R.id.addSalesDate);
        EditText addMake = findViewById(R.id.addMake);
        EditText addModel = findViewById(R.id.addModel);

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
                EasyImage.openChooserWithGallery(AddNewDevice.this,"Elegir",KEY_CAMERA);
                camera = 1;
            }
        });
        imageTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyImage.openChooserWithGallery(AddNewDevice.this,"Elegir",KEY_CAMERA);
                camera = 2;
            }
        });
        imageThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyImage.openChooserWithGallery(AddNewDevice.this,"Elegir",KEY_CAMERA);
                camera = 3;
            }
        });
        imageFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyImage.openChooserWithGallery(AddNewDevice.this,"Elegir",KEY_CAMERA);
                camera = 4;
            }
        });

        //Camera Button
        fabAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyImage.openChooserWithGallery(AddNewDevice.this,"Elegir",KEY_CAMERA);
            }
        });

        //Insert Button
        fabAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

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
                    switch (i) {
                        case KEY_CAMERA:
                            File file = list.get(0);
                            Uri uri = Uri.fromFile(file);
                            switch (camera){
                                case 1:
                                    Glide.with(AddNewDevice.this).load(uri).into(imageOne);
                                    camera = 2;
                                    break;
                                case 2:
                                    Glide.with(AddNewDevice.this).load(uri).into(imageTwo);
                                    camera = 3;
                                    break;
                                case 3:
                                    Glide.with(AddNewDevice.this).load(uri).into(imageThree);
                                    camera = 4;
                                    break;
                                case 4:
                                    Glide.with(AddNewDevice.this).load(uri).into(imageFour);
                                    camera = 1;
                                    break;
                            }

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
}
