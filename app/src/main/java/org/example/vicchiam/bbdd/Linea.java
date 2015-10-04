package org.example.vicchiam.bbdd;


import android.database.Cursor;

import java.io.Serializable;

/**
 * Created by vicch on 23/09/2015.
 */
public class Linea implements Serializable {

    private static final long serialVersionUID = 679845877;

    private long id;
    private Pedido pedido;
    private float cantidad;
    private float descuento;
    private float iva;

    private String producto_cod;
    private String producto_nom;
    private String producto_fam;
    private String producto_und;
    private String producto_desc;
    private float producto_precio;

    public Linea(long id){
        this.id=id;
    }

    public Linea(long id, float cantidad, float descuento, float iva, String producto_cod, String producto_nom, String producto_fam, String producto_und, String producto_desc, float producto_precio) {
        this.id = id;
        this.pedido=null;
        this.cantidad = cantidad;
        this.descuento = descuento;
        this.iva = iva;
        this.producto_cod = producto_cod;
        this.producto_nom = producto_nom;
        this.producto_fam = producto_fam;
        this.producto_und = producto_und;
        this.producto_desc=producto_desc;
        this.producto_precio = producto_precio;
    }

    public Linea(Cursor cursor){
        this.id = cursor.getLong(cursor.getColumnIndex(DBPedido.PEDIDO_LINEA_COLS[0]));
        this.pedido=null;
        this.cantidad =cursor.getFloat(cursor.getColumnIndex(DBPedido.PEDIDO_LINEA_COLS[2]));
        this.descuento = cursor.getFloat(cursor.getColumnIndex(DBPedido.PEDIDO_LINEA_COLS[3]));
        this.iva = cursor.getFloat(cursor.getColumnIndex(DBPedido.PEDIDO_LINEA_COLS[4]));
        this.producto_cod = cursor.getString(cursor.getColumnIndex(DBPedido.PEDIDO_LINEA_COLS[5]));
        this.producto_nom = cursor.getString(cursor.getColumnIndex(DBPedido.PEDIDO_LINEA_COLS[6]));
        this.producto_fam = cursor.getString(cursor.getColumnIndex(DBPedido.PEDIDO_LINEA_COLS[7]));
        this.producto_und = cursor.getString(cursor.getColumnIndex(DBPedido.PEDIDO_LINEA_COLS[8]));
        this.producto_desc = cursor.getString(cursor.getColumnIndex(DBPedido.PEDIDO_LINEA_COLS[9]));
        this.producto_precio = cursor.getFloat(cursor.getColumnIndex(DBPedido.PEDIDO_LINEA_COLS[10]));
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

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public float getCantidad() {
        return cantidad;
    }

    public void setCantidad(float cantidad) {
        this.cantidad = cantidad;
    }

    public float getPrecio() {
        return cantidad*producto_precio;
    }

    public float getPrecioImpuestos() {
        float precio=cantidad*producto_precio;
        float desc=precio*descuento;
        return (precio-desc)*(iva+1);
    }

    public float getDescuento() {
        return descuento;
    }

    public void setDescuento(float descuento) {
        this.descuento = descuento;
    }

    public float getIva() {
        return iva;
    }

    public void setIva(float iva) {
        this.iva = iva;
    }

    public String getProducto_cod() {
        return producto_cod;
    }

    public void setProducto_cod(String producto_cod) {
        this.producto_cod = producto_cod;
    }

    public String getProducto_nom() {
        return producto_nom;
    }

    public void setProducto_nom(String producto_nom) {
        this.producto_nom = producto_nom;
    }

    public String getProducto_fam() {
        return producto_fam;
    }

    public void setProducto_fam(String producto_fam) {
        this.producto_fam = producto_fam;
    }

    public String getProducto_und() {
        return producto_und;
    }

    public void setProducto_und(String producto_und) {
        this.producto_und = producto_und;
    }

    public float getProducto_precio() {
        return producto_precio;
    }

    public void setProducto_precio(float producto_precio) {
        this.producto_precio = producto_precio;
    }

    public String getProducto_desc(){
        return this.producto_desc;
    }

    public void setProducto_desc(String desc){
        this.producto_desc=desc;
    }

    @Override
    public String toString() {
        return "Linea{" +
                "id=" + id +
                ", cantidad=" + cantidad +
                ", descuento=" + descuento +
                ", iva=" + iva +
                ", producto_cod='" + producto_cod + '\'' +
                ", producto_nom='" + producto_nom + '\'' +
                ", producto_fam='" + producto_fam + '\'' +
                ", producto_und='" + producto_und + '\'' +
                ", producto_precio=" + producto_precio +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Linea linea = (Linea) o;

        return id == linea.id;

    }

    public String toCSV(){
        String res=cantidad+";"+descuento+";"+iva+";"+producto_cod+";"+producto_nom+";"+producto_fam+";"+producto_und+";"+producto_precio+";"+producto_desc;
        return res;
    }

}
