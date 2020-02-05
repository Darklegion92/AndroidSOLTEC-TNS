package co.com.solucionesytecnologia.pedidossoltec.modelo;

import java.util.ArrayList;
import java.util.Date;

public class Ruta {

    private String _id;
    private String documento;
    private Integer idTNS;
    private String nombre;
    private ArrayList<Cartera> carteras;
    private String direccion;
    private String barrio;
    private String telefono;
    private Double longitude;
    private Double latitude;
    private Boolean novedad;
    private String idNovedad;
    private Date ultVisita;
    private Date creado;
    private String idUsuario;
    private Boolean visitado;
    private Integer diaSemana;

    public Integer getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(Integer diaSemana) {
        this.diaSemana = diaSemana;
    }

    public String getIdNovedad() {
        return idNovedad;
    }

    public void setIdNovedad(String idNovedad) {
        this.idNovedad = idNovedad;
    }

    public Boolean getVisitado() {
        return visitado;
    }

    public void setVisitado(Boolean visitado) {
        this.visitado = visitado;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public Integer getIdTNS() {
        return idTNS;
    }

    public void setIdTNS(Integer idTNS) {
        this.idTNS = idTNS;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Boolean getNovedad() {
        return novedad;
    }

    public void setNovedad(Boolean novedad) {
        this.novedad = novedad;
    }

    public Date getUltVisita() {
        return ultVisita;
    }

    public void setUltVisita(Date ultVisita) {
        this.ultVisita = ultVisita;
    }

    public Date getCreado() {
        return creado;
    }

    public void setCreado(Date creado) {
        this.creado = creado;
    }

    public ArrayList<Cartera> getCarteras() {
        return carteras;
    }

    public void setCarteras(ArrayList<Cartera> carteras) {
        this.carteras = carteras;
    }
}
