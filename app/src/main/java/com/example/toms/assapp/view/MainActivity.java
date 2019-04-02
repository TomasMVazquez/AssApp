package com.example.toms.assapp.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.toms.assapp.R;
import com.example.toms.assapp.model.Device;
import com.example.toms.assapp.util.Util;
import com.example.toms.assapp.view.fragments.DaysToInsureFragment;
import com.example.toms.assapp.view.fragments.MyInsuranceFragment;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rohitss.uceh.UCEHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MyInsuranceFragment.OnFragmentNotify, DaysToInsureFragment.FragmentInterface {

    public static final int KEY_LOGIN=101;
    public static final String KEY_NAME = "name";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private FirebaseAuth mAuth;
    private static FirebaseUser currentUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private DatabaseReference deviceDb;
    private DatabaseReference idDeviceInsured;
    private DatabaseReference idinsuranceDate;
    private DatabaseReference idDaysToInsure;
    private DatabaseReference idVerif;

    private static String idDataBase;

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new UCEHandler.Builder(this).build();

        mAuth = FirebaseAuth.getInstance();
        //firebase
        mDatabase = FirebaseDatabase.getInstance();
        mReference  = mDatabase.getReference();

        Util.printHash(this);

        //Toolbar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.company_name));

        //NavigationView
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigation);

        //Btn Hamburguesa
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, myToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()){
                    case R.id.login:
                        goLogIn();
                        return true;
                    case R.id.misSeguros:
                        Toast.makeText(MainActivity.this, "En construccion", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.aboutUs:
                        Toast.makeText(MainActivity.this, "En construccion", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });

    }

    //Inflar Menu para ver el boton de ir al Login
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //On item Click del Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.login_toolbar:
                Toast.makeText(this, "Aca hay que poner Activity de my profile", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Boton para Atras
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)){
            drawerLayout.closeDrawers();
        }else{
            super.onBackPressed();
        }
    }

    //Cargar Fragments
    public void cargarFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    //Confirmar si esta Logeado
    public static Boolean isLogon(Context context){
        if (currentUser!=null){
            return true;
        }else {
            return false;
        }
    }

    //Ir al Login
    public void goLogIn(){
        if (currentUser!=null){
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            currentUser = null;
            updateUI(currentUser);
            Toast.makeText(this, "Has salido de tu sesion", Toast.LENGTH_SHORT).show();
            idDataBase = null;
        }else {
            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
            startActivityForResult(intent, KEY_LOGIN);
        }
    }

    //Lo que envia el login en el activity for result
    public static Intent respuestaLogin(String name){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_NAME, name);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case KEY_LOGIN:
                    Bundle bundle = data.getExtras();
                    String name = bundle.getString(KEY_NAME);
                    break;
            }
            updateUI(currentUser);
        }else {
            //Toast.makeText(this, "Fallo LogIn", Toast.LENGTH_SHORT).show();
        }

    }

    //Actualizar experiencia de usuario
    public void updateUI(FirebaseUser user){
        //Call fragment to add new devices
        final MyInsuranceFragment myInsuranceFragment = new MyInsuranceFragment();
        cargarFragment(myInsuranceFragment);

        if (user != null) {
            navigationView.getMenu().findItem(R.id.login).setTitle(getResources().getString(R.string.logout));
            String name = user.getDisplayName();
            String email = user.getEmail();
            String phone = user.getPhoneNumber();
            Uri uri = user.getPhotoUrl();
            String dataBaseName;

            if (email != null) {
                String mail = email.substring(0, email.indexOf("."));
                dataBaseName = mail;
            }else {
                dataBaseName = phone;
            }

            if (idDataBase != null && !idDataBase.equals(dataBaseName)){
                updateGuestDataBase(dataBaseName);

            } else {
                idDataBase = dataBaseName;
            }
        }else {
            navigationView.getMenu().findItem(R.id.login).setTitle(getResources().getString(R.string.login));
        }
    }

    //Actualizar nombre de base de datos del cliente guest al iniciar
    public void updateGuestDataBase(final String name){

        final Map newUserData = new HashMap();

        mReference.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    mReference.child(idDataBase).child(getResources().getString(R.string.device_reference_child)).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Map newDevicesAdded = new HashMap();
                            newDevicesAdded.put(dataSnapshot.getKey(),dataSnapshot.getValue());
                            mReference.child(name).child(getResources().getString(R.string.device_reference_child)).updateChildren(newDevicesAdded);
                            mReference.child(idDataBase).removeValue();
                            idDataBase = name;
                            //return;
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else {

                    mReference.child(idDataBase).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            newUserData.put(name,dataSnapshot.getValue());
                            mReference.updateChildren(newUserData);
                            mReference.child(idDataBase).removeValue();
                            idDataBase = name;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //Al salir de la app destruir la base de datos del guest
    @Override
    protected void onDestroy() {

        if (idDataBase !=null && currentUser==null){
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
            final DatabaseReference mReference = mDatabase.getReference();
            FirebaseStorage mStorage = FirebaseStorage.getInstance();
            final StorageReference raiz = mStorage.getReference();
            //TODO ver como eliminar las fotos del guest
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
                    mReference.child(idDataBase).removeValue();
//                }
//            },3000);
        }

        super.onDestroy();
    }

    @Override
    public void showIdGuest(String id) {
        idDataBase = id;
    }

    @Override
    public void cargarDias(String id) {
        Bundle bundle = new Bundle();
        bundle.putString(DaysToInsureFragment.KEY_ID,id);
        DaysToInsureFragment daysToInsureFragment = new DaysToInsureFragment();
        daysToInsureFragment.setArguments(bundle);
        daysToInsureFragment.setCancelable(false);
        FragmentManager fragmentManager = getSupportFragmentManager();
        daysToInsureFragment.show(fragmentManager,"days");
    }


    public static String showId(){
        return idDataBase;
    }

    @Override
    public void confirmDays(String id, Integer days) {
            if (days>0){
                confirmInsurance(id,days);
            }else {
                cancelInsurance(id);
                Toast.makeText(getApplicationContext(), "Su seguro NO se activó", Toast.LENGTH_SHORT).show();
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
}

