package org.example.vicchiam.bbdd;

import android.database.Cursor;

import java.io.Serializable;

/**
 * Created by vicch on 22/09/2015.
 */
public class Producto implements Serializable {

    private static final long serialVersionUID = 679845875;

    private long id;
    private String codigo;
    private String nombre;
    private String familia;
    private String unidad;
    private float precio;
    private String descripcion;

    public Producto(long id, String codigo, String nombre, String familia, String unidad, float precio, String descripcion){
        this.id=id;
        this.codigo=codigo;
        this.nombre=nombre;
        this.familia=familia;
        this.unidad=unidad;
        this.precio=precio;
        this.descripcion=descripcion;
    }

    public Producto(Cursor cursor){
        this.id=cursor.getLong(cursor.getColumnIndex(DBProducto.PRODUCTO_COLS[0]));
        this.codigo=cursor.getString(cursor.getColumnIndex(DBProducto.PRODUCTO_COLS[1]));
        this.nombre=cursor.getString(cursor.getColumnIndex(DBProducto.PRODUCTO_COLS[2]));
        this.familia=cursor.getString(cursor.getColumnIndex(DBProducto.PRODUCTO_COLS[3]));
        this.unidad=cursor.getString(cursor.getColumnIndex(DBProducto.PRODUCTO_COLS[4]));
        this.precio=cursor.getFloat(cursor.getColumnIndex(DBProducto.PRODUCTO_COLS[5]));
        this.descripcion=cursor.getString(cursor.getColumnIndex(DBProducto.PRODUCTO_COLS[6]));
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFamilia() {
        return familia;
    }

    public void setFamilia(String familia) {
        this.familia = familia;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public String getDescripcion(){
        return this.descripcion;
    }

    public void setDescripcion(String descripcion){
        this.descripcion=descripcion;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", familia='" + familia + '\'' +
                ", unidad='" + unidad + '\'' +
                ", precio=" + precio + '\'' +
                ", descripcion=" + descripcion +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Producto producto = (Producto) o;

        return codigo.equals(producto.codigo);

    }

}
