package co.com.solucionesytecnologia.pedidossoltec.interfaces;

import java.util.ArrayList;
import java.util.Map;

import co.com.solucionesytecnologia.pedidossoltec.modelo.Novedad;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;


public interface ApiRestNovedad {

    @GET("novedades/")
    Call<ArrayList<Novedad>> cargarNovedades(@HeaderMap Map<String, String> headers);

}
