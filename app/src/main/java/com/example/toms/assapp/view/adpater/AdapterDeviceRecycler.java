package com.example.toms.assapp.view.adpater;

import android.content.Context;
import android.net.Uri;
import android.service.autofill.LuhnChecksumValidator;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.toms.assapp.R;
import com.example.toms.assapp.model.Device;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AdapterDeviceRecycler extends RecyclerView.Adapter {

    private FirebaseStorage mStorage;

    //Atributos
    private List<Device> deviceList;
    private Context context;
    protected AdaptadorInterface adaptadorInterface;

    //Constructor


    public AdapterDeviceRecycler(List<Device> deviceList, AdaptadorInterface adaptadorInterface) {
        this.deviceList = deviceList;
        this.adaptadorInterface = adaptadorInterface;
    }

    //Setter
    public void setDeviceList(List<Device> deviceList) {
        this.deviceList = deviceList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        //Buscamos contexto
        context = parent.getContext();

        //pasamos contexto al inflador
        LayoutInflater inflater = LayoutInflater.from(context);

        //inflamos view
        View view = inflater.inflate(R.layout.device_card,parent,false);

        //pasamos hoder
        DeviceViewHolder deviceViewHolder = new DeviceViewHolder(view);

        return deviceViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        //Buscamos datos
        Device device = deviceList.get(i);

        //Casteamos
        DeviceViewHolder deviceViewHolder = (DeviceViewHolder) viewHolder;

        //Cargamos dato
        deviceViewHolder.cargar(context,device);
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public interface AdaptadorInterface{
        void goToDetails(Device device, Integer position);
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder{

        //Atributos
        private TextView name;
        private TextView premmium;
        private ImageView image;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameDevice);
            premmium = itemView.findViewById(R.id.insuranceAmountDevice);
            image = itemView.findViewById(R.id.imageDevice);

            //Poner listener al item para ir al detalle
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO IR AL DETALLE
                }
            });
        }

        public void cargar(final Context context, Device device){
            //Gerente
            mStorage = FirebaseStorage.getInstance();
            //Raiz del Storage
            StorageReference raiz = mStorage.getReference();

            StorageReference imageReference = raiz.child(device.getPhotoList().get(1));
            imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context).load(uri).into(image);
                }
            });

            name.setText(device.getName());
            //TODO SETEAR EL PRECIO
            premmium.setText("$$$$$$$");
        }
    }

}
