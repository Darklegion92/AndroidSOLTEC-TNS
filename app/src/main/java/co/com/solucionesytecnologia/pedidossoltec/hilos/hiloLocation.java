package co.com.solucionesytecnologia.pedidossoltec.hilos;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;


import java.util.Date;

import co.com.solucionesytecnologia.pedidossoltec.config.config;

public class hiloLocation extends AsyncTask< String,String,String> {
    private config CONFIG = new config();
    private SQLiteDatabase BD;
    private Context context;
    public hiloLocation(Context context) {
        this.BD = context.openOrCreateDatabase(CONFIG.getNameDB(), context.MODE_PRIVATE, null);
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        BD = context.openOrCreateDatabase("pedidos-SOLTEC", context.MODE_PRIVATE, null);
        Date date = new Date();
        ContentValues param = new ContentValues();
        param.put("visitado", 1);
        param.put("latitude", strings[1]);
        param.put("longitude", strings[2]);
        param.put("ultVisita", date.toString());
        String[] filtro = new String[]{strings[0]};
        Log.e("holoLocation","35 "+strings[0]);
        Log.e("hiloLocation",BD.update("rutero", param, "documento=?", filtro)+" 36");
        BD.close();

        return null;
    }
}