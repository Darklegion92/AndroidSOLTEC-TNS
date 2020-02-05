package co.com.solucionesytecnologia.pedidossoltec;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.auth0.android.jwt.DecodeException;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import co.com.solucionesytecnologia.pedidossoltec.alertas.Alertas;

import co.com.solucionesytecnologia.pedidossoltec.config.config;
import co.com.solucionesytecnologia.pedidossoltec.interfaces.ApiRestNovedad;
import co.com.solucionesytecnologia.pedidossoltec.interfaces.ApiRestUsuario;
import co.com.solucionesytecnologia.pedidossoltec.modelo.Novedad;
import co.com.solucionesytecnologia.pedidossoltec.modelo.Usuario;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     *
     *
     */
    private Button btnIngresar;
    private EditText txtUsuario;
    private EditText txtPassword;
    private ProgressBar barraLogin;


    private Alertas alertas = new Alertas();
    private config CONFIG = new config();

    private LocationManager locationManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Se inicializan los componentes
        setContentView(R.layout.activity_main);

        btnIngresar = findViewById(R.id.btnIngresar);
        btnIngresar.setOnClickListener(this);

        txtUsuario = findViewById(R.id.txtUsuario);
        txtPassword = findViewById(R.id.txtPassword);

        barraLogin = findViewById(R.id.barraLogin);

        alertas = new Alertas();
        //fin de inicalizacion de componentes

        //Se realiza la creacion de las tablas locales correspondientes
        SQLiteDatabase BD = openOrCreateDatabase(CONFIG.getNameDB(), MODE_PRIVATE, null);

        //Tabla usada para guardar la ruta de toma de pedidos
        BD.execSQL(
                "CREATE TABLE IF NOT EXISTS rutero (id INTEGER , documento VARCHAR(200),idTNS INTEGER,nombre VARCHAR(200)," +
                        "direccion VARCHAR(200),barrio VARCHAR(200)," +
                        "telefono VARCHAR(200),latitude DOUBLE,longitude DOUBLE,novedad INTEGER,idNovedad " +
                        "INTEGER,ultVisita DATE,creado DATE,idUsuario VARCHAR(200),visitado INTEGER)"
        );

        //tabla donde se almacenan los motivos de novedad
        BD.execSQL(
                "CREATE TABLE IF NOT EXISTS novedades (nombre VARCHAR(300) UNIQUE,idNovedad VARCHAR(200))"
        );

        //tabla donde se almacena el usuario para loguin offline
        BD.execSQL(
                "CREATE TABLE IF NOT EXISTS usuarios (fecha Date,usuario VARCHAR(10) UNIQUE,idUsuario VARCHAR(100) UNIQUE,Autorization_key VARCHAR(50), password VARCHAR(50))");
        //fin de creacion de tablas
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == btnIngresar.getId()){
            barraLogin.setVisibility(View.VISIBLE);
            String usuario = txtUsuario.getText().toString();
            String password = txtPassword.getText().toString();

            //validacion campos vacios
            if(!usuario.isEmpty() && !password.isEmpty()) {

                SQLiteDatabase BD = openOrCreateDatabase(CONFIG.getNameDB(), MODE_PRIVATE, null);
                String[] arg = new String[]{usuario};
                Cursor usuarioLocal = BD.query("usuarios", null, "usuario=?", arg, null, null, null);

                //validacion de usuario local existente
                    if(usuarioLocal.moveToFirst()) {
                        //@Falta Validar Token
                        do{
                            if(password.equals(usuarioLocal.getString(4))) {
                                consultarNovedades(usuarioLocal.getString(3));
                                iniciarPrincipal(usuarioLocal.getString(2));
                            }else{
                                AlertDialog alertaError = alertas.alertaError(MainActivity.this,"CREDENCIALES INCORRECTAS","Usuario o Contraseña Incorrecta, Intente Nuevamente");
                                alertaError.show();
                                txtPassword.setText("");
                                txtPassword.requestFocus();
                                barraLogin.setVisibility(View.INVISIBLE);
                            }
                        }while (usuarioLocal.moveToNext());

                    usuarioLocal.close();
                } else{

                    usuarioLocal.close();

                  //se realiza la consulta al servidor para validar las credenciales
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(CONFIG.getApiRest())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    ApiRestUsuario apiUsuario = retrofit.create(ApiRestUsuario.class);
                    Call<Usuario> callUsuario = apiUsuario.login(txtUsuario.getText().toString().trim(),txtPassword.getText().toString());
                    callUsuario.enqueue(new Callback<Usuario>() {
                        @Override
                        public void onResponse(Call<Usuario> call, retrofit2.Response<Usuario> response) {

                            //se valida si los datos suministrados son correctos
                            if(response.code()==200){

                                Usuario user = response.body();

                                Cursor nuevoUsuario = BD.query("usuarios", null, null,null, null, null, null);

                                //se verifica si hay otros usuarios creados y se procede a preguntar si desea eliminar datos locales
                                if(nuevoUsuario.getCount()>0){

                                    //@pendiente se debe preguntar si desea borrar informacion almacenada localmente, si la respuesta es positiva limpia todas las tablas

                                    BD.delete("usuarios",null,null);

                                }

                                //se agrega el usuario al almacenamiento local y se inicia actividad principal
                                ContentValues usuarioNuevo = new ContentValues();
                                Date fecha = new Date();
                                try {
                                    usuarioNuevo.put("fecha", fecha.toString());
                                    usuarioNuevo.put("usuario", usuario);
                                    usuarioNuevo.put("idUsuario", user.get_id());
                                    usuarioNuevo.put("Autorization_key", user.getAutorization_key());
                                    usuarioNuevo.put("password", password);
                                    BD.insert("usuarios", null, usuarioNuevo);

                                    consultarNovedades(user.getAutorization_key());
                                    iniciarPrincipal(user.get_id());

                                }catch (DecodeException e){
                                    e.printStackTrace();
                                }
                                nuevoUsuario.close();
                                BD.close();
                                barraLogin.setVisibility(View.INVISIBLE);

                            }else if(response.code()==201){
                                AlertDialog alertaError = alertas.alertaError(MainActivity.this,"CREDENCIALES INCORRECTAS","Usuario o Contraseña Incorrecta, Intente Nuevamente");
                                alertaError.show();
                                txtPassword.setText("");
                                txtPassword.requestFocus();
                                barraLogin.setVisibility(View.INVISIBLE);
                            }
                        }
                        @Override
                        public void onFailure(Call<Usuario> call, Throwable t) {
                            AlertDialog alertaError = alertas.alertaError(MainActivity.this,"ERROR AL INTENTAR CONECTAR","No tiene acceso al servidor, verifique su conexión a internet e intentelo nuevamente");
                            alertaError.show();
                            barraLogin.setVisibility(View.INVISIBLE);
                        }
                    });

                }
            }else{
                barraLogin.setVisibility(View.INVISIBLE);
                AlertDialog error = alertas.alertaError(this,"CAMPOS OBLIGATORIOS","Los Campos Usuario y Contraseña No Pueden Estar Vacíos");
                error.show();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertNoGps();
        }

    }

    /**
     * Dialog para encender GPS
     */
    private void AlertNoGps() {
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(
                this);
        alertDialogBuilder
                .setMessage("Debe Activar GPS para continuar")
                .setCancelable(false)
                .setPositiveButton("Habilitar GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.dismiss();
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(0);
                    }
                });
        androidx.appcompat.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void consultarNovedades(String Autorization_key){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CONFIG.getApiRest())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiRestNovedad apiNovedad = retrofit.create(ApiRestNovedad.class);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Autorization_key", Autorization_key);
        Call<ArrayList<Novedad>> callNovedades = apiNovedad.cargarNovedades(headers);
        callNovedades.enqueue(new Callback<ArrayList<Novedad>>() {
            @Override
            public void onResponse(Call<ArrayList<Novedad>> call, Response<ArrayList<Novedad>> response) {
                if (response.code() == 200) {
                    SQLiteDatabase BD = openOrCreateDatabase(CONFIG.getNameDB(), MODE_PRIVATE, null);
                    BD.delete("novedades",null,null);
                    ArrayList<Novedad> novedades = response.body();
                    for (Novedad novedad : novedades) {
                        ContentValues valores = new ContentValues();
                        valores.put("nombre", novedad.getNombre());
                        valores.put("idNovedad", novedad.get_id());
                        BD.insert("novedades", null, valores);
                    }
                    BD.close();
                }else if(response.code()==201){

                }
            }
            @Override
            public void onFailure(Call<ArrayList<Novedad>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error al consultar novedades: " + t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }

    private void iniciarPrincipal(String idUsuario){
        Intent i = new Intent(getApplicationContext(), principal.class);
        i.putExtra("idUsuario", idUsuario);
        startActivity(i);
        txtPassword.setText("");
        txtUsuario.setText("");
    }

}
