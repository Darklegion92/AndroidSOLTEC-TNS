package co.com.solucionesytecnologia.pedidossoltec.alertas;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import co.com.solucionesytecnologia.pedidossoltec.R;
import co.com.solucionesytecnologia.pedidossoltec.config.config;
import co.com.solucionesytecnologia.pedidossoltec.interfaces.ApiRestRutas;
import co.com.solucionesytecnologia.pedidossoltec.modelo.Ruta;
import co.com.solucionesytecnologia.pedidossoltec.services.Localizacion;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class DialogNovedad extends DialogFragment implements LocationListener{

    private config CONFIG = new config();

    private Button btnGuardar;
    private Spinner cbxNovedades;
    private  SQLiteDatabase BD;
    private Location location;


    public AlertDialog createLoginDialogo(Activity context, String documento, DrawerLayout drawer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        //inicalizacion de componentes
        LayoutInflater inflater = context.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_novedad, null);
        builder.setCancelable(false);
        builder.setView(v);
        btnGuardar = v.findViewById(R.id.btnGuardarNovedad);
        cbxNovedades = v.findViewById(R.id.cbxNovedades);

        //consultamos las novedades para el spinner
        SQLiteDatabase BD = context.openOrCreateDatabase(CONFIG.getNameDB(), context.MODE_PRIVATE, null);
        Cursor cursor = BD.query("novedades", null, null, null, null, null, null);
        String[] novedades = new String[cursor.getCount()];

        //llenamos el spinner
        int i = 0;
        if(cursor.moveToFirst()){
            do{
                novedades[i]=cursor.getString(0).toUpperCase();
                i++;
            }while(cursor.moveToNext());
        }
        BD.close();
        cbxNovedades.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item,novedades));
        AlertDialog alert =builder.create();

        btnGuardar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View vi) {
                        guardarDatos(context,documento,alert,drawer);

                    }
                }
        );
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);

        } else {
            iniciarLocalizacion(context);
        }

        return alert;
    }

    private void guardarDatos(Activity context, String documento, AlertDialog alert,DrawerLayout drawer) {
        //inicia localizacion GPS


            try {
            BD = context.openOrCreateDatabase(CONFIG.getNameDB(), context.MODE_PRIVATE, null);
            ContentValues valores = new ContentValues();
            Date date = new Date();
            valores.put("novedad", true);
            valores.put("idNovedad", cbxNovedades.getSelectedItem().toString());
            valores.put("visitado", true);
            valores.put("latitude", location.getLatitude());
            valores.put("longitude", location.getLongitude());
            valores.put("ultVisita",date.toString());

            //se actualiza el rutero con la novedad
           BD.update("rutero", valores, "documento = ?", new String[]{documento});
            alert.dismiss();

            //se marca documento como nulo
            SharedPreferences myPreferences
                    = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor myEditor = myPreferences.edit();
            myEditor.putString("Documento", "");
            myEditor.commit();

            //se consulta el rutero que este listo para enviar
            enviarDatos(context);

        }catch(Exception e){
            e.printStackTrace();
        }finally{
                BD.close();
        }
        drawer.openDrawer(Gravity.LEFT);
    }

    /**
     * se encarga de realizar el envio de datos al servidor
     * @param context ejecutar acciones de contexto activity
     */
    private void enviarDatos( Context context) {

        Cursor cursor = BD.query("rutero", null, null, null, null, null, null);

        //se valida si existe algun dato guardado
        if (cursor.moveToFirst()) {
            do {
                //se valida si estan completados para enviar
                if (cursor.getInt(14) == 1) {
                    try{
                        SimpleDateFormat ff = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                        Date fecha = ff.parse(cursor.getString(11));
                        Ruta ruta = new Ruta();
                        ruta.set_id(cursor.getString(0));
                        ruta.setDocumento(cursor.getString(1));
                        ruta.setIdSIIGO(cursor.getInt(2));
                        ruta.setNombre(cursor.getString(3));
                        ruta.setDireccion(cursor.getString(4));
                        ruta.setBarrio(cursor.getString(5));
                        ruta.setTelefono(cursor.getString(6));
                        ruta.setLatitude(cursor.getDouble(7));
                        ruta.setLongitude(cursor.getDouble(8));
                        ruta.setNovedad(true);
                        ruta.setIdNovedad(cursor.getString(10));
                        ruta.setUltVisita(fecha);
                        ruta.setIdUsuario(cursor.getString(13));
                        ruta.setVisitado(true);
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(CONFIG.getApiRest())
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        ApiRestRutas apiRestRutas = retrofit.create(ApiRestRutas.class);
                        Call<Ruta> call = apiRestRutas.guardarVisita(ruta);

                        call.enqueue(new Callback<Ruta>() {
                            @Override
                            public void onResponse(Call<Ruta> call, retrofit2.Response<Ruta> response) {
                                //si es correcto elimina el local y abre el draw
                                if (response.code() == 200) {
                                    BD = context.openOrCreateDatabase(CONFIG.getNameDB(), context.MODE_PRIVATE, null);
                                    BD.delete("rutero", "documento=?", new String[]{ruta.getDocumento()});
                                }
                            }
                            @Override
                            public void onFailure(Call<Ruta> call, Throwable t) {
                               t.printStackTrace();
                            }
                        });
                    }catch (Exception e){

                    }
                }
            } while (cursor.moveToNext());

            BD.close();
            cursor.close();
        }

    }

    private void iniciarLocalizacion(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!gpsEnabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    /**
     * se encarga de arrancar los servicios, permisos e interfases necesarias para arrancar la Geolocalizacion
     * @param context actividad donde se va a ejecutar el proceso
     */
   /* private void locationStart(Activity context) {
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
    }*/

}
