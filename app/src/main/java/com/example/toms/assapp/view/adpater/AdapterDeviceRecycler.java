package com.example.toms.assapp.view.adpater;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.toms.assapp.R;
import com.example.toms.assapp.controller.ControllerPricing;
import com.example.toms.assapp.model.Device;
import com.example.toms.assapp.util.ResultListener;
import com.example.toms.assapp.util.SwipeAndDragHelper;
import com.example.toms.assapp.view.DeviceDetail;
import com.example.toms.assapp.view.MainActivity;
import com.example.toms.assapp.view.fragments.MyInsuranceFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AdapterDeviceRecycler extends RecyclerView.Adapter {

    private FirebaseStorage mStorage;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    //Atributos
    private List<Device> deviceList;
    private Context context;
    protected AdaptadorInterface adaptadorInterface;
    private ItemTouchHelper touchHelper;
    private DeviceViewHolder deviceViewHolder;

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

    //Creamos metodo para asignar el Helper
    public void setTouchHelper(ItemTouchHelper touchHelper){
        this.touchHelper = touchHelper;
    }

    public interface AdaptadorInterface{
        void goToDetails(Device device, Integer position);
        void goToLogIn();
        void goToFinalVerification(String id);

    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder{

        //Atributos
        private TextView idDevice;
        private TextView name;
        private TextView premmium;
        private TextView timeInsured;
        private ImageView image;
        private ImageView shield;
        private Switch switchInsurance;

        public DeviceViewHolder(@NonNull final View itemView) {
            super(itemView);

            idDevice = itemView.findViewById(R.id.idDevice);
            name = itemView.findViewById(R.id.nameDevice);
            premmium = itemView.findViewById(R.id.insuranceAmountDevice);
            timeInsured = itemView.findViewById(R.id.timeInsured);
            image = itemView.findViewById(R.id.imageDevice);
            shield = itemView.findViewById(R.id.imageInsurance);
            switchInsurance = itemView.findViewById(R.id.switchInsurance);

            //firebase
            mDatabase = FirebaseDatabase.getInstance();
            mReference = mDatabase.getReference();

            //Listener to be or not to be protected
            switchInsurance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MainActivity.isLogon(context)) {
                        clickInsurance();
                    }else {
                        switchInsurance.setChecked(false);
                        Toast.makeText(context, "Primero debes estar logeado para asegurar tu equipo", Toast.LENGTH_SHORT).show();
                        adaptadorInterface.goToLogIn();
                    }

                }
            });

            //Poner listener al item para ir al detalle
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Device device = deviceList.get(getAdapterPosition());
                    Integer position = deviceList.indexOf(device);
                    adaptadorInterface.goToDetails(device,position);
                }
            });
        }

        private void confirmDialogDemo() {
            final DatabaseReference deviceDb = mReference.child(MainActivity.showId()).child(context.getResources().getString(R.string.device_reference_child)).child(idDevice.getText().toString());
            final DatabaseReference idDeviceInsured = mReference.child(MainActivity.showId()).child(context.getResources().getString(R.string.device_reference_child)).child(idDevice.getText().toString()).child("insured");
            final DatabaseReference idinsuranceDate = mReference.child(MainActivity.showId()).child(context.getResources().getString(R.string.device_reference_child)).child(idDevice.getText().toString()).child("insuranceDate");
            DatabaseReference idVerif = mReference.child(MainActivity.showId()).child(context.getResources().getString(R.string.device_reference_child)).child(idDevice.getText().toString()).child("finalVerification");

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Confirmación");
            builder.setMessage("Por favor confirmar que usted quiere asegurar su equipo");
            builder.setCancelable(false);
            builder.setIcon(context.getDrawable(R.drawable.escudo_grey));
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SimpleDateFormat y = new SimpleDateFormat("yyyy");
                    SimpleDateFormat d = new SimpleDateFormat("dd");
                    SimpleDateFormat m = new SimpleDateFormat("MM");
                    Date today = new Date();
                    String year = String.valueOf(Integer.valueOf(y.format(today))-1);
                    String day = d.format(today);
                    String month = m.format(today);
                    String insuranceDate = (day + "/" + month + "/" + year);
                    idinsuranceDate.setValue(insuranceDate);
                    shield.setVisibility(View.VISIBLE);
                    Toast.makeText(context, "El seguro de su " + name.getText().toString() + " esta Activo", Toast.LENGTH_SHORT).show();
                    idDeviceInsured.setValue(true);

                    deviceDb.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Device device = dataSnapshot.getValue(Device.class);
                            daysCalculation(device);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switchInsurance.setChecked(false);
                    Toast.makeText(getApplicationContext(), "Su seguro NO se activó", Toast.LENGTH_SHORT).show();
                }
            });

            builder.show();
        }

        public void clickInsurance(){
            final DatabaseReference deviceDb = mReference.child(MainActivity.showId()).child(context.getResources().getString(R.string.device_reference_child)).child(idDevice.getText().toString());
            final DatabaseReference idDeviceInsured = mReference.child(MainActivity.showId()).child(context.getResources().getString(R.string.device_reference_child)).child(idDevice.getText().toString()).child("insured");
            final DatabaseReference idinsuranceDate = mReference.child(MainActivity.showId()).child(context.getResources().getString(R.string.device_reference_child)).child(idDevice.getText().toString()).child("insuranceDate");
            DatabaseReference idVerif = mReference.child(MainActivity.showId()).child(context.getResources().getString(R.string.device_reference_child)).child(idDevice.getText().toString()).child("finalVerification");
            if (switchInsurance.isChecked()) {
                idVerif.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Boolean check = (Boolean) dataSnapshot.getValue();
                        if (check){
                            confirmDialogDemo();
                        }else {
                            adaptadorInterface.goToFinalVerification(idDevice.getText().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            } else {
                shield.setVisibility(View.INVISIBLE);
                Toast.makeText(context, "El seguro de su " + name.getText().toString() + " ah sido desactivado", Toast.LENGTH_SHORT).show();
                idDeviceInsured.setValue(false);
                idinsuranceDate.setValue("");
                timeInsured.setText("");
            }
        }

        public void cargar(final Context context, Device device){
            //Gerente
            mStorage = FirebaseStorage.getInstance();
            //Raiz del Storage
            StorageReference raiz = mStorage.getReference();

            if(device.getPhotoList()!=null) {
                StorageReference imageReference = raiz.child(device.getPhotoList().get(1));
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(context).load(uri).into(image);
                    }
                });
            }else {
                Glide.with(context).load(context.getDrawable(R.drawable.logo_solo)).into(image);
            }

            if (device.getInsured()==true){
                switchInsurance.setChecked(true);
                shield.setVisibility(View.VISIBLE);
            }else {
                switchInsurance.setChecked(false);
                shield.setVisibility(View.INVISIBLE);
            }

            idDevice.setText(device.getId());
            name.setText(device.getName());

            ControllerPricing controllerPricing = new ControllerPricing();
            controllerPricing.givePricing(device.getTypeDevice(), new ResultListener<Double>() {
                @Override
                public void finish(Double resultado) {
                    String precio = "$ " + resultado.toString() + " /mes";
                    premmium.setText(precio);
                }
            });

            daysCalculation(device);

        }

        public void daysCalculation(Device device){
            String insuranceDate = device.getInsuranceDate();
            if (!insuranceDate.isEmpty()){

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date myDate = null;
                try {
                    myDate = dateFormat.parse(insuranceDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Date today = new Date();
                long timeDiff = today.getTime() - myDate.getTime();
                TimeUnit unit = TimeUnit.DAYS;
                long diference = unit.convert(timeDiff,TimeUnit.MILLISECONDS);
                long daysRestantes = 395 - diference;

                String time = daysRestantes + " días restantes";

                timeInsured.setText(time);
            }else {
                timeInsured.setText("");
            }
        }

    }

    //--------Metodo para eliminar recetas con el Swipe----------------------------------------//
    public void eliminarReceta (int posicion){
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        final DatabaseReference deviceDb = mReference.child(MainActivity.showId()).child(context.getResources().getString(R.string.device_reference_child)).child(deviceList.get(posicion).getId());
        deviceDb.removeValue();
        deviceList.remove(posicion);
        notifyItemRemoved(posicion);
    }

    //--------Metodo para volver a agregar la receta que se elimino------------------------------//
    public void noRemoverReceta (Device device, int posicion){
        deviceList.add(posicion,device);
        notifyItemInserted(posicion);
    }
}
