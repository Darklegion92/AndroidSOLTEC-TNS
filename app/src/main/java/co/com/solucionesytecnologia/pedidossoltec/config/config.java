package co.com.solucionesytecnologia.pedidossoltec.config;

public class config {

    private final String apiRest = "http://192.168.43.34:3001";
    //private final String apiRest = "http://45.82.72.196:3001";
    private final String nameDB = "pedidos-SOLTEC";

    public String getNameDB() {
        return nameDB;
    }

    public String getApiRest() {
        return apiRest;
    }
}
