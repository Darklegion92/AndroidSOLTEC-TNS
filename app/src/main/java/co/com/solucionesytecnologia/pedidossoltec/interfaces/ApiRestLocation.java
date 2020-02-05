package co.com.solucionesytecnologia.pedidossoltec.interfaces;



import co.com.solucionesytecnologia.pedidossoltec.modelo.Location;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiRestLocation {

    @POST("location/guardar")
    Call<Location> guardar(@Body Location location);
}
