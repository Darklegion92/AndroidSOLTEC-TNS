package co.com.solucionesytecnologia.pedidossoltec.config;

public class config {

    //private final String apiRest = "http://200.116.155.111:65150";
    private final String apiRest = "http://192.168.1.17:3001";
    private final String nameDB = "pedidos-SOLTEC";

    public String getNameDB() {
        return nameDB;
    }

    public String getApiRest() {
        return apiRest;
    }
}
