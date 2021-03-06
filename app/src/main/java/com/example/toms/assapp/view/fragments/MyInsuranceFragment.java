package com.example.toms.assapp.view.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.toms.assapp.R;
import com.example.toms.assapp.controller.ControllerFirebaseDataBase;
import com.example.toms.assapp.model.Device;
import com.example.toms.assapp.util.ResultListener;
import com.example.toms.assapp.util.SwipeAndDragHelper;
import com.example.toms.assapp.view.AddNewDevice;
import com.example.toms.assapp.view.DeviceDetail;
import com.example.toms.assapp.view.FinalVerification;
import com.example.toms.assapp.view.LogInActivity;
import com.example.toms.assapp.view.MainActivity;
import com.example.toms.assapp.view.adpater.AdapterDeviceRecycler;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyInsuranceFragment extends Fragment implements AdapterDeviceRecycler.AdaptadorInterface {

    public static final int KEY_LOGIN = 202;
    public static final int KEY_FINAL_VERIF = 203;
    public static final int KEY_ADD_DEVICE = 201;
    public static final String KEY_ID_DB = "db";

    private String idDataBase;
    private AdapterDeviceRecycler adapterDeviceRecycler;
    private List<Device> deviceList = new ArrayList<>();

    public MyInsuranceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_my_insurance, container, false);

        //Views
        FloatingActionButton fabAddInsurance = view.findViewById(R.id.fabAddInsurance);

        //Recycler Adapter
        adapterDeviceRecycler = new AdapterDeviceRecycler(new ArrayList<Device>(),this);

        //Info DataBAse
        ControllerFirebaseDataBase controllerFirebaseDataBase = new ControllerFirebaseDataBase();
        if (MainActivity.showId()!=null) {
            controllerFirebaseDataBase.giveDeviceList(getContext(), MainActivity.showId(), new ResultListener<List<Device>>() {
                @Override
                public void finish(List<Device> resultado) {
                    if (resultado.size() > 0) {
                        adapterDeviceRecycler.setDeviceList(resultado);
                        deviceList.addAll(resultado);
                    }
                }
            });
        }

        //RecyclerView
        RecyclerView recyclerDevices = view.findViewById(R.id.recyclerDevices);
        recyclerDevices.setHasFixedSize(true);
        LinearLayoutManager llm =new LinearLayoutManager(view.getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerDevices.setLayoutManager(llm);
        recyclerDevices.setAdapter(adapterDeviceRecycler);

        //Swipe and Drag
        SwipeAndDragHelper swipeAndDragHelper =new SwipeAndDragHelper(new SwipeAndDragHelper.ActionCompletionContract() {
            @Override
            public void onViewMoved(int oldPosition, int newPosition) {

            }

            @Override
            public void onViewSwiped(final int position) {
                final Device device = deviceList.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Confirmación");
                builder.setMessage("Por favor confirmar que usted quiere quitar este equipo de su lista");
                builder.setCancelable(false);
                builder.setIcon(getActivity().getDrawable(R.drawable.escudo_grey));
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       adapterDeviceRecycler.eliminarEquipo(position);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapterDeviceRecycler.noRemoverEquipo(device,position);
                        getActivity().recreate();
                    }
                });

                builder.show();
            }
        });

        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeAndDragHelper);
        adapterDeviceRecycler.setTouchHelper(touchHelper);
        touchHelper.attachToRecyclerView(recyclerDevices);

        //actions
        fabAddInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idDataBase = MainActivity.showId();
                Intent i = new Intent(view.getContext(),AddNewDevice.class);
                Bundle bundle = new Bundle();
                bundle.putString(AddNewDevice.KEY_ID_DB, idDataBase);
                i.putExtras(bundle);
                startActivityForResult(i,KEY_ADD_DEVICE);
            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        OnFragmentNotify onFragmentFormNotify = (OnFragmentNotify) getContext();

        if (resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case KEY_ADD_DEVICE:
                    Bundle bundle = data.getExtras();
                    idDataBase = bundle.getString(KEY_ID_DB);
                    if (idDataBase != null){
                        onFragmentFormNotify.showIdGuest(idDataBase);
                    }
                    break;
                case KEY_LOGIN:
                    Toast.makeText(getContext(), "Login exitoso", Toast.LENGTH_SHORT).show();
                    break;
                case KEY_FINAL_VERIF:
                    Toast.makeText(getContext(), "Verificacion Final exitoso", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    }


    //the guest id of the database
    public static Intent dataBaseId(String id){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ID_DB, id);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public void goToDetails(Device device, Integer position) {
        Intent intent = new Intent(getActivity(),DeviceDetail.class);
        Bundle bundle = new Bundle();
        bundle.putInt(DeviceDetailFragment.KEY_POSITION,position);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void goToLogIn() {
        Intent intent = new Intent(getContext(), LogInActivity.class);
        startActivityForResult(intent, KEY_LOGIN);
    }

    @Override
    public void goToFinalVerification(String id) {
        Intent intent = new Intent(getContext(), FinalVerification.class);
        Bundle bundle = new Bundle();
        bundle.putString(FinalVerification.KEY_ID_DEVICE_DB,id);
        intent.putExtras(bundle);
        startActivityForResult(intent, KEY_FINAL_VERIF);
    }

    @Override
    public void cargarDiasAdapter(String id) {
        OnFragmentNotify onFragmentFormNotify = (OnFragmentNotify) getContext();
        onFragmentFormNotify.cargarDias(id);
    }

    public interface OnFragmentNotify {
        public void showIdGuest(String id);
        public void cargarDias(String id);
    }

}
