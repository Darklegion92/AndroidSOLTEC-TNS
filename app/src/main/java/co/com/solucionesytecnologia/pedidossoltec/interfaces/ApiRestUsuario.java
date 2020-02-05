package co.com.solucionesytecnologia.pedidossoltec.interfaces;



import co.com.solucionesytecnologia.pedidossoltec.modelo.Usuario;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiRestUsuario {

    @GET("usuario/login")
    Call<Usuario> getUser();

    @POST("usuario/login")
    @FormUrlEncoded
    Call<Usuario> login(@Field("usuario") String usuario, @Field("password") String password);
}
