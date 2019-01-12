package com.example.toms.assapp.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.toms.assapp.R;
import com.example.toms.assapp.util.Util;
import com.example.toms.assapp.view.fragments.MyInsuranceFragment;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity implements MyInsuranceFragment.OnFragmentFormNotify {

    public static final int KEY_LOGIN=101;
    public static final String KEY_NAME = "name";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private static String idGuest;

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

        mAuth = FirebaseAuth.getInstance();

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
                        return true;
                    case R.id.aboutUs:
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

    //Ir al Login
    public void goLogIn(){
        if (currentUser!=null){
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            currentUser = null;
            updateUI(currentUser);
            Toast.makeText(this, "Has salido de tu sesion", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
                    break;

            }
        }

    }

    //Actualizar experiencia de usuario
    public void updateUI(FirebaseUser user){
        //Call fragment to add new devices
        MyInsuranceFragment myInsuranceFragment = new MyInsuranceFragment();
        cargarFragment(myInsuranceFragment);

        if (user != null) {
            navigationView.getMenu().findItem(R.id.login).setTitle(getResources().getString(R.string.logout));
            String name = user.getDisplayName();
            Uri uri = user.getPhotoUrl();
        }else {
            navigationView.getMenu().findItem(R.id.login).setTitle(getResources().getString(R.string.login));
            //Commenting log in when starting cause the client wants the customer to be able to add device to see the pricing
            //goLogIn();
        }
    }

    //Al salir de la app destruir la base de datos del guest
    @Override
    protected void onDestroy() {
        if (idGuest!=null){
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
            DatabaseReference mReference = mDatabase.getReference();
            FirebaseStorage mStorage = FirebaseStorage.getInstance();
            StorageReference raiz = mStorage.getReference();

            mReference.child(idGuest).removeValue();
        }

        super.onDestroy();
    }

    @Override
    public void showIdGuest(String id) {
        idGuest = id;
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
    }

    public static String showId(){
        return idGuest;
    }
}

