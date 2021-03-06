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



        i