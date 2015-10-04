package org.example.vicchiam.bbdd;

import android.database.Cursor;

import java.io.Serializable;

/**
 * Created by vicch on 22/09/2015.
 */
public class Cliente implements Serializable {

    private static final long serialVersionUID = 679845874;

    private long id;
    private String codigo;
    private String cif;
    private String nombre;
    private String telefono;
    private String mail;
    private String calle;
    private String poblacion;
    private String cp;
    private String pais;
    private String empresa;

    public Cliente(Cursor cursor){
        this.id=cursor.getLong(cursor.getColumnIndex(DBCliente.CLIENTE_COLS[0]));
        this.codigo=cursor.getString(cursor.getColumnIndex(DBCliente.CLIENTE_COLS[1]));
        this.cif=cursor.getString(cursor.getColumnIndex(DBCliente.CLIENTE_COLS[2]));
        this.nombre=cursor.getString(cursor.getColumnIndex(DBCliente.CLIENTE_COLS[3]));
        this.telefono=cursor.getString(cursor.getColumnIndex(DBCliente.CLIENTE_COLS[4]));
        this.mail=cursor.getString(cursor.getColumnIndex(DBCliente.CLIENTE_COLS[5]));
        this.calle=cursor.getString(cursor.getColumnIndex(DBCliente.CLIENTE_COLS[6]));
        this.poblacion=cursor.getString(cursor.getColumnIndex(DBCliente.CLIENTE_COLS[7]));
        this.cp=cursor.getString(cursor.getColumnIndex(DBCliente.CLIENTE_COLS[8]));
        this.pais=cursor.getString(cursor.getColumnIndex(DBCliente.CLIENTE_COLS[9]));
        this.empresa=cursor.getString(cursor.getColumnIndex(DBCliente.CLIENTE_COLS[10]));
    }

    public Cliente(long id, String codigo, String cif, String nombre, String telefono, String mail,
                   String calle, String poblacion, String cp, String pais, String empresa) {
        this.id = id;
        this.codigo = codigo;
        this.cif = cif;
        this.nombre = nombre;
        this.telefono = telefono;
        this.mail = mail;
        this.calle = calle;
        this.poblacion = poblacion;
        this.cp = cp;
        this.pais = pais;
        this.empresa = empresa;
    }

    public long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getCif() {
        return cif;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getMail() {
        return mail;
    }

    public String getCalle() {
        return calle;
    }

    public String getPoblacion() {
        return poblacion;
    }

    public String getCp() {
        return cp;
    }

    public String getPais() {
        return pais;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getDireccion(){
        String res="";
        res+=calle+", "+poblacion+"("+cp+"), "+pais;
        return res;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", cif='" + cif + '\'' +
                ", nombre='" + nombre + '\'' +
                ", telefono='" + telefono + '\'' +
                ", mail='" + mail + '\'' +
                ", calle='" + calle + '\'' +
                ", poblacion='" + poblacion + '\'' +
                ", cp='" + cp + '\'' +
                ", pais='" + pais + '\'' +
                ", empresa='" + empresa + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cliente cliente = (Cliente) o;

        return codigo.equals(cliente.getCodigo());

    }



}
