package co.com.solucionesytecnologia.pedidossoltec.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import co.com.solucionesytecnologia.pedidossoltec.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class adaptadorClientes extends RecyclerView.Adapter<adaptadorClientes.ClientesViewHolder> implements View.OnClickListener{

    ArrayList<String[]> datos;
    private View.OnClickListener listener;

    public adaptadorClientes(ArrayList<String[]> datos) {
        this.datos = datos;
    }

    @NonNull
    @Override
    public ClientesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_clientes,null,false);
        view.setOnClickListener(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(
                MATCH_PARENT,
                WRAP_CONTENT));
        return new ClientesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientesViewHolder holder, int position) {
        holder.txtDocumentoCliente.setText(datos.get(position)[0]);
        holder.txtNombreCliente.setText(datos.get(position)[1].toUpperCase());
        holder.txtDireccionCliente.setText(datos.get(position)[2].toUpperCase());
        holder.txtTelefonoCliente.setText(datos.get(position)[3].toUpperCase());
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    public void setOnClicListener(View.OnClickListener listener){
        this.listener = listener;
    }


    @Override
    public void onClick(View v) {
            if(listener!=null){
                listener.onClick(v);
            }
    }

    public class ClientesViewHolder extends RecyclerView.ViewHolder {
        TextView txtDocumentoCliente, txtNombreCliente, txtDireccionCliente, txtTelefonoCliente;

        public ClientesViewHolder(View itemView) {
            super(itemView);
            txtTelefonoCliente = itemView.findViewById(R.id.txtTelefonoCliente);
            txtDireccionCliente = itemView.findViewById(R.id.txtDireccionCliente);
            txtDocumentoCliente = itemView.findViewById(R.id.txtDocumentoCliente);
            txtNombreCliente = itemView.findViewById(R.id.txtNombreCliente);
        }
    }

    public void setDatos(ArrayList<String[]> datos) {
        this.datos = datos;
    }
}
