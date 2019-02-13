package com.example.toms.assapp.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.toms.assapp.R;
import com.example.toms.assapp.controller.ControllerFirebaseDataBase;
import com.example.toms.assapp.model.Device;
import com.example.toms.assapp.util.ResultListener;
import com.example.toms.assapp.view.adpater.ViewPagerAdapter;
import com.example.toms.assapp.view.fragments.DeviceDetailFragment;

import java.util.ArrayList;
import java.util.List;

public class DeviceDetail extends AppCompatActivity {

    private List<Device> deviceList = new ArrayList<>();
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);

        //intent
        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        final Integer posicion = bundle.getInt(DeviceDetailFragment.KEY_POSITION);

        //Lista de fragments
        final List<Fragment> fragments = new ArrayList<>();
        //Adapter
        adapter = new ViewPagerAdapter(getSupportFragmentManager(),new ArrayList<Fragment>());

        ControllerFirebaseDataBase controllerFirebaseDataBase = new ControllerFirebaseDataBase();

        if (MainActivity.showId()!=null) {
            controllerFirebaseDataBase.giveDeviceList(this, MainActivity.showId(), new ResultListener<List<Device>>() {
                @Override
                public void finish(List<Device> resultado) {
                    deviceList.addAll(resultado);
                }
            });
        }

        //Progess dialog
        final ProgressDialog prog= new ProgressDialog(DeviceDetail.this);
        prog.setTitle("Por favor espere");
        prog.setMessage("Estamos cargando su equipo");
        prog.setCancelable(false);
        prog.setIndeterminate(true);
        prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        prog.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                for (Integer i = 0; i<deviceList.size();i++){
                    fragments.add(DeviceDetailFragment.giveDevice(getApplicationContext(),posicion,deviceList.get(i)));
                }
                adapter.setFragmentList(fragments);
                //ViewPager
                ViewPager viewPager = findViewById(R.id.viewPager);
                viewPager.setAdapter(adapter);

                //Inicializado
                viewPager.setCurrentItem(posicion);
                prog.dismiss();
            }
        }, 1000);

    }
}
