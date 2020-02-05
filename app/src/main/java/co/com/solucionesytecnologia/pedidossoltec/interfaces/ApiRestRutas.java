package co.com.solucionesytecnologia.pedidossoltec.interfaces;

import java.util.ArrayList;

import co.com.solucionesytecnologia.pedidossoltec.modelo.Ruta;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiRestRutas {

    @GET("rutas/dia/")
    Call<ArrayList<Ruta>> cargarRutero(@Query("idUsuario") String idUsuario,@Query("diaSemana") Integer  diaSemana);

    @POST("rutas/visita/")
    Call<Ruta> guardarVisita(@Body Ruta ruta);
}
