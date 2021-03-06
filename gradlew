package co.com.solucionesytecnologia.apppedidos;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnIngresar;
    private EditText txtUsuario;
    private EditText txtPassword;
    obtenerWebService hiloconexion;
    Location location;
    LocationManager locationManager;
    LocationListener locationListener;
    SQLiteDatabase BD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnIngresar = findViewById(R.id.btnIngresar);
        btnIngresar.setOnClickListener(this);

        txtUsuario = findViewById(R.id.txtUsuario);
        txtPassword = findViewById(R.id.txtPassword);

        BD = openOrCreateDatabase("pedidos-SOLTEC", MODE_PRIVATE, null);
        /*Crea la tabla usuarios*/
       BD.execSQL(
                "CREATE TABLE IF NOT EXISTS usuario (usuario VARCHAR(200), password VARCHAR(200),token VARCHAR(500), nombre VARCHAR(200))"
        );

        Cursor cursor = BD.rawQuery("select usuario, password, nombre from usuario", null);
        if(cursor.getCount()<=0) {
            /*se inserta un usuario inicial*/
            ContentValues row1 = new ContentValues();
            row1.put("usuario", "admin");
            row1.put("password", "1234");
            row1.put("nombre", "Jose Luis Rodriguez Peñaranda");
            row1.put("token", "1");
            BD.insert("usuario", null, row1);
            cursor.close();
        }
        /**/
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);



        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
        }else{
            location= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        }else{
            location= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        mostarUvicacion(location);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mostarUvicacion(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

    }

    @Override
    protected void onStop() {
        super.onStop();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }else{
                locationManager.removeUpdates(locationListener);
            }

        }else{
            locationManager.removeUpdates(locationListener);
        }
    }

    private void mostarUvicacion(Location location) {
        hiloconexion = new obtenerWebService();
    }

    @Override
    public void onClick(View v) {
        SQLiteDatabase BD =
                openOrCreateDatabase("pedidos-SOLTEC", MODE_PRIVATE, null);

        if(v.getId()==R.id.btnIngresar){
            Cursor cursor = BD.rawQuery("select usuario, password, nombre from usuario", null);
            String nombre="";
            String password="";
            String usuario = "";
            while(cursor.moveToNext()) {
                usuario = cursor.getString(0);
                password = cursor.getString(1);
                nombre = cursor.getString(2);
            }
            cursor.close();
            Toast.makeText(this,usuario+" contra"+password,Toast.LENGTH_LONG).show();
            if(!usuario.isEmpty() && usuario.equals(txtUsuario.getText()) &&  password.equals(txtPassword.getText())) {
                Intent i = new Intent(this, tabPrincipal.class);
                startActivity(i);
            }else{
                Toast.makeText(this,"Usuario o Contraseña Incorrectas",Toast.LENGTH_LONG).show();

            }
        }
    }
}

                                                                                                                  