package co.com.solucionesytecnologia.pedidossoltec.modelo;


import java.util.Date;

public class Location {
    private String _id ;
    private String idUsuario ;
    private Double latitude ;
    private Double longitude ;
    private Date fecha ;

    public Location( String idUsuario, Double latitude, Double longitude, Date fecha) {
        this.idUsuario = idUsuario;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fecha = fecha;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String get_id() {
        return _id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
