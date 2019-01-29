package com.example.toms.assapp.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.toms.assapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class UifDataActivity extends AppCompatActivity {

    private Spinner spinnerDocumentType;
    private static EditText addBirth;
    private EditText addCuit;
    private EditText addDocument;
    private EditText addAddress;
    private EditText addPhone;
    private EditText addEmail;
    private EditText addActivity;
    private EditText addMaritial;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uif_data);

        spinnerDocumentType = findViewById(R.id.spinnerDocumentType);
        addBirth = findViewById(R.id.addBirth);
        Button btnContinue = findViewById(R.id.btnContinue);
        addCuit = findViewById(R.id.addCuit);
        addDocument = findViewById(R.id.addDocument);
        addAddress = findViewById(R.id.addAddress);
        addPhone = findViewById(R.id.addPhone);
        addEmail = findViewById(R.id.addEmail);
        addActivity = findViewById(R.id.addActivity);
        addMaritial = findViewById(R.id.addMaritial);

        final TextInputLayout addTextInputCuit = findViewById(R.id.addTextInputCuit);

//        if (user.getEmail()!=null){
//            addEmail.setText(user.getEmail());
//        }
//
//        if (user.getPhoneNumber()!=null){
//            addPhone.setText(user.getPhoneNumber());
//        }

        //validaciones de los campos
        addCuit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()==2){
                    if (s != "20" || s!="23" || s!="24" || s!="27"){
                        addTextInputCuit.setError("Debe completar un CUIT valido");
                    }else {
                        addTextInputCuit.setError("");
                        addCuit.setText(s + String.valueOf(addDocument.getText()));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //Adding date piccker for birth
        //Stting this on Touch will not allow blind people to use the app (https://www.youtube.com/watch?v=1by5J7c5Vz4&feature=youtu.be)
        addBirth.setOnTouchListener(new View.OnTouchListener() {
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

        //Seleccionar los items del spinner
        ArrayList<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("Tipo");
        spinnerArray.add("DNI");
        spinnerArray.add("Cedula");
        spinnerArray.add("----");

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDocumentType.setAdapter(adapterSpinner);
        spinnerDocumentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Toast.makeText(UifDataActivity.this, "Debe seleccionar un tipo de documento", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInfo()) {
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            }
        });

    }

    public Boolean checkInfo(){
        if (spinnerDocumentType.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Missing info", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (addBirth.getText().length() == 0) {
            Toast.makeText(this, "Missing info", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (addCuit.getText().length() == 0) {
            Toast.makeText(this, "Missing info", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (addDocument.getText().length() == 0) {
            Toast.makeText(this, "Missing info", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (addAddress.getText().length() == 0) {
            Toast.makeText(this, "Missing info", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (addPhone.getText().length() == 0) {
            Toast.makeText(this, "Missing info", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (addEmail.getText().length() == 0) {
            Toast.makeText(this, "Missing info", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (addActivity.getText().length() == 0) {
            Toast.makeText(this, "Missing info", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (addMaritial.getText().length() == 0) {
            Toast.makeText(this, "Missing info", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        finish();
        super.onBackPressed();
    }

    public void showTruitonDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
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
            addBirth.setText(dateChoosen);
        }
    }

}
