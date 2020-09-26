package co.com.solucionesytecnologia.pedidossoltec;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import co.com.solucionesytecnologia.pedidossoltec.alertas.Alertas;
import co.com.solucionesytecnologia.pedidossoltec.alertas.DialogNovedad;
import co.com.solucionesytecnologia.pedidossoltec.config.config;
import co.com.solucionesytecnologia.pedidossoltec.interfaces.ApiRestRutas;
import co.com.solucionesytecnologia.pedidossoltec.modelo.Ruta;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class principal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private Alertas alertas = new Alertas();
    private config CONFIG = new config();
    private AppBarConfiguration mAppBarConfiguration;

    private String idUsuario;

    private LocationManager locationManager;
    private SQLiteDatabase BD;
    private DrawerLayout drawer;
    private Location location;

    private ProgressBar barraPrincipal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //inizializamos componentes
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        drawer.openDrawer(Gravity.LEFT);
        barraPrincipal = findViewById(R.id.barraPrincipal);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                 R.id.nav_gallery,R.id.nav_home, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        barraPrincipal.setVisibility(View.VISIBLE);

        //se carga extra que viene del main activity
        String idUsuario = this.getIntent().getExtras().getString("idUsuario");
        this.idUsuario = idUsuario;

        SQLiteDatabase BD = openOrCreateDatabase(CONFIG.getNameDB(), MODE_PRIVATE, null);
        Cursor cursor = BD.query("rutero", null, null, null, null, null, null);

        //se verifica si hay rutas almacenadas
        if(cursor.getCount()<=0){
            cargarInformacion(idUsuario);
        }else if(cursor.moveToFirst()) {

            try {
                SimpleDateFormat ff = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",Locale.ENGLISH);
                Date hoy =new Date();
                Date fecha = ff.parse(cursor.getString(12));

                //valida si lo descargado es del dia o para proceder a descargar lo del dia
                if (hoy.getDay() == fecha.getDay()) {

                } else {
                    BD.delete("rutero",null,null);
                    cargarInformacion(idUsuario);
                }
            }catch (Exception e ){
                e.printStackTrace();
            }
        }
        BD.close();
        cursor.close();
        barraPrincipal.setVisibility(View.INVISIBLE);
    }

    ////Metodos sobreescritos usados

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_actualizar:
                //cargarInformacion(this.idUsuario);
                return true;
            case R.id.action_novedad:
                DialogNovedad newFragment = new DialogNovedad();
                SharedPreferences myPreferences
                        = PreferenceManager.getDefaultSharedPreferences(this);
                String documento = myPreferences.getString("Documento", null);
                AlertDialog alertaError = newFragment.createLoginDialogo(this, documento,drawer);
                alertaError.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
    ////Funciones que se estan usando

    /**
     * se encargar de cargar la informacion de las rutas dependiendo del dia y el usuario
     * @param idUsuario
     */
    private void cargarInformacion(String idUsuario) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CONFIG.getApiRest())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiRestRutas apiRestRutas = retrofit.create(ApiRestRutas.class);
        Calendar c = Calendar.getInstance();
        Call<ArrayList<Ruta>> call = apiRestRutas.cargarRutero(idUsuario,c.get(Calendar.DAY_OF_WEEK)-1);
        call.enqueue(new Callback<ArrayList<Ruta>>() {
            @Override
            public void onResponse(Call<ArrayList<Ruta>> call, Response<ArrayList<Ruta>> response) {

                if (response.code() == 200) {
                    ArrayList<Ruta> rutas = response.body();
                    guardarRutaLocal(rutas);
                } else if (response.code() == 201) {
                    AlertDialog alertaError = alertas.alertaError(principal.this,"SIN RUTA ASIGNADA","No Tiene Clientes Asignados, Informe A Su Superior");
                    alertaError.show();
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Ruta>> call, Throwable t) {
                t.printStackTrace();
                AlertDialog alertaError = alertas.alertaError(principal.this,"SIN RUTA ASIGNADA","No Tiene Clientes Asignados, Informe A Su Superior");
                alertaError.show();
            }

        });
    }

    /**
     * Almacena localmente la ruta consultada del cliente
     * @param rutas
     */
    private void guardarRutaLocal(ArrayList<Ruta> rutas) {

        SQLiteDatabase BD = openOrCreateDatabase(CONFIG.getNameDB(), MODE_PRIVATE, null);
        try {
            Date fecha = new Date();
            for (Ruta ruta : rutas) {
                ContentValues valores = new ContentValues();
                valores.put("documento", ruta.getDocumento());
                valores.put("id", ruta.get_id());
                valores.put("barrio", ruta.getBarrio());
                valores.put("direccion", ruta.getDireccion());
                valores.put("idSIIGO", ruta.getIdSIIGO());
                valores.put("nombre", ruta.getNombre());
                valores.put("telefono", ruta.getTelefono());
                valores.put("visitado", false);
                valores.put("creado",fecha.toString());
                valores.put("idUsuario",ruta.getIdUsuario());
                BD.insert("rutero", null, valores);
            }

        } catch (Exception e) {
            e.printStackTrace();
            AlertDialog alertaError = alertas.alertaError(principal.this,"ERROR AL ACTUALIZAR",e.getMessage());
            alertaError.show();
        }
        BD.close();

    }


}
