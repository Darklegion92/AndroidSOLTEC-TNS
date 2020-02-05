package co.com.solucionesytecnologia.pedidossoltec.alertas;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Alertas {

    public AlertDialog alertaError(Context context, String titulo, String mensaje){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        return builder.create();
    }
}
