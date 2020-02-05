package co.com.solucionesytecnologia.pedidossoltec.alertas;


import android.app.Activity;
import android.app.AlertDialog;

import android.content.SharedPreferences;

import android.preference.PreferenceManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import co.com.solucionesytecnologia.pedidossoltec.R;
import co.com.solucionesytecnologia.pedidossoltec.adaptadores.adaptadorClientes;

import co.com.solucionesytecnologia.pedidossoltec.interfaces.Dlg2Listener;



public class DialogRutas extends DialogFragment implements View.OnClickListener {

    /**
     * Crea un Diálogo con una lista de selección simple
     *
     * @return La instancia del diálogo
     */
    private Activity context;
    private RecyclerView rv;
    private AlertDialog alerta;
    private adaptadorClientes adaptadorClientes;
    private TextView txtFiltroCliente;
    private String[] datos;
    private Dlg2Listener _dlgEscucha;

    public AlertDialog createSingleListDialog(Activity context, String[] datosAnt,Dlg2Listener _dlgEscucha) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);

        //iniciamos componentes
        this._dlgEscucha = _dlgEscucha;
        LayoutInflater inflater = context.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog__clientes, null);
        txtFiltroCliente = v.findViewById(R.id.txtFiltroCliente);

        //evento para poder filtrar
        txtFiltroCliente.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               if(s.length()>0){
                    ArrayList<String[]> data = new ArrayList<>();
                    String[] datas = new String[datosAnt.length];
                    int i = 0;
                    for(String da:datosAnt){
                        String[] cliente = da.split(";");
                        if(cliente[1].contains(s)) {
                            data.add(cliente);
                            datas[i] = da;
                            i++;
                        }

                    }
                   datos = datas;
                    Log.e("79",datas[0]+"");
                   adaptadorClientes.setDatos(data);
                   rv.setAdapter(adaptadorClientes);

                }else {
                    ArrayList<String[]> data = new ArrayList<>();
                   String[] datas = new String[datosAnt.length];
                   int i = 0;
                    for(String da:datosAnt){
                        String[] cliente = da.split(";");
                        data.add(cliente);
                        datas[i] = da;
                        i++;
                    }

                   datos = datas;
                   adaptadorClientes.setDatos(data);
                   rv.setAdapter(adaptadorClientes);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        builder.setView(v);
        this.datos=datosAnt;

        //se preparan los datos para ser listados
        ArrayList<String[]> data = new ArrayList<>();

        for(String da:datos){
            if(da != null) {
                String[] cliente = da.split(";");
                data.add(cliente);
            }
        }
        rv = v.findViewById(R.id.reciclerClientes);

        rv.setLayoutManager(new LinearLayoutManager(context));


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                ((LinearLayoutManager) rv.getLayoutManager()).getOrientation());
        rv.addItemDecoration(dividerItemDecoration);
        adaptadorClientes = new adaptadorClientes(data);

        //se agrega el evento para realizar la visita
        adaptadorClientes.setOnClicListener(this);

        this.context = context;
        rv.setAdapter(adaptadorClientes);
        alerta = builder.create();
        return alerta;
    }

    @Override
    public void onClick(View view) {
        Log.e("rutas ","prueba "+rv.getChildAdapterPosition(view));
        iniciar(rv.getChildAdapterPosition(view),datos);
    }

    //funciones

    private void iniciar(int i,String[] datos){
        SharedPreferences prefs
                = PreferenceManager.getDefaultSharedPreferences(context.getBaseContext());
        SharedPreferences.Editor editor = prefs.edit();
        String[] data = datos[i].split(";");
        editor.putString("Documento", data[0]);
        editor.commit();
        _dlgEscucha.onOkClick(datos[i]);
        alerta.dismiss();
    }

}
