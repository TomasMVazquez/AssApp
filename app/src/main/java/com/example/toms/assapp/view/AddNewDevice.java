package com.example.toms.assapp.view;

import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.toms.assapp.R;

public class AddNewDevice extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_device);

        //Views
//        FloatingActionButton fabAddDevice = findViewById(R.id.fabAddDevice);
        Button fabAddDevice = findViewById(R.id.fabAddDevice);
        FloatingActionButton fabAddImage = findViewById(R.id.fabAddImage);
        ImageView imageOne = findViewById(R.id.image_one);
        ImageView imageTwo = findViewById(R.id.image_two);
        ImageView imageThree = findViewById(R.id.image_three);
        ImageView imageFour = findViewById(R.id.image_four);
        TextInputLayout addTextInputName = findViewById(R.id.addTextInputName);
        TextInputLayout addTextInputSalesDate = findViewById(R.id.addTextInputSalesDate);
        TextInputLayout addTextInputMake = findViewById(R.id.addTextInputMake);
        TextInputLayout addTextInputModel = findViewById(R.id.addTextInputModel);
        EditText addName = findViewById(R.id.addName);
        EditText addSalesDate = findViewById(R.id.addSalesDate);
        EditText addMake = findViewById(R.id.addMake);
        EditText addModel = findViewById(R.id.addModel);



        fabAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

    }
}
