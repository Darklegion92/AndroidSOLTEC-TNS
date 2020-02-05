package co.com.solucionesytecnologia.pedidossoltec.modelo;

import java.sql.Date;

public class Usuario {
    private String _id;
    private String nombre;
    private String usuario;
    private String password;
    private String Autorization_key;
    private Date creacion;

    public String getPassword() {
        return password;
    }

    public String getAutorization_key() {
        return Autorization_key;
    }

    public void setAutorization_key(String autorization_key) {
        Autorization_key = autorization_key;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreacion() {
        return creacion;
    }

    public void setCreacion(Date creacion) {
        this.creacion = creacion;
    }
}
