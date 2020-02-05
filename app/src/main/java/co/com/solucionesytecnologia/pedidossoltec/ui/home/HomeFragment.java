package co.com.solucionesytecnologia.pedidossoltec.ui.home;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import co.com.solucionesytecnologia.pedidossoltec.R;
import co.com.solucionesytecnologia.pedidossoltec.alertas.DialogRutas;
import co.com.solucionesytecnologia.pedidossoltec.config.config;
import co.com.solucionesytecnologia.pedidossoltec.interfaces.Dlg2Listener;

public class HomeFragment extends Fragment implements Dlg2Listener {

    private HomeViewModel homeViewModel;

    private TextView txtDocumento, txtNombre, txtDireccion;

    private Location location;
    private config CONFIG = new config();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //inicializan los componentes
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        SharedPreferences myPreferences
                = PreferenceManager.getDefaultSharedPreferences(getActivity());
        txtDocumento = root.findViewById(R.id.txtDocumento);
        txtDireccion = root.findViewById(R.id.txtDireccion);
        txtNombre = root.findViewById(R.id.txtNombre);

        iniciarDialog();

        return root;
    }
    @Override
    public void onOkClick(String dato) {
        String[] datos = dato.split(";");
        txtNombre.setText(datos[1].toUpperCase());
        txtDireccion.setText(datos[2].toUpperCase());
        txtDocumento.setText(datos[0]);
    }

    //funciones usadas

    /**
     * Se encarga iniciar el dialog de rutero para seleccionar el cliente que se va a visitar
     */
    private void iniciarDialog() {
        //se realiza la consulta para cargar el dialog
        SQLiteDatabase BD = getActivity().getApplicationContext().openOrCreateDatabase(CONFIG.getNameDB(), getActivity().getApplicationContext().MODE_PRIVATE, null);
        Cursor consultaRutas = BD.query("rutero",null,null,null,null,null,null);
        String[] datos = new String[consultaRutas.getCount()];
        if(consultaRutas.moveToFirst()){
            int i = 0;
            do{
                if(consultaRutas.getString(14).equals("0")) {
                    datos[i] = consultaRutas.getString(1) + ";" + consultaRutas.getString(3) + ";" + consultaRutas.getString(4) + ";" + consultaRutas.getString(6);
                    i++;
                }
            }while (consultaRutas.moveToNext());

            //se arranca el dialog con los clientes de la ruta
            if(!datos[0].equals("")){
                locationStart(getActivity());
                DialogRutas newFragment = new DialogRutas();
                AlertDialog alertaError = newFragment.createSingleListDialog(getActivity(), datos,this);
                alertaError.show();
            }else{
                Log.e("96","no hay rutas");
            }
        }
    }

    /**
     * se encarga de arrancar los servicios, permisos e interfases necesarias para arrancar la Geolocalizacion
     * @param context actividad donde se va a ejecutar el proceso
     */
    private void locationStart(Activity context) {
        try {
            LocationManager mlocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 1000);

                    return;
                } else {
                    location = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            } else {
                location = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }


            final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!gpsEnabled) {

                Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(settingsIntent);
            }
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 1000);
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}