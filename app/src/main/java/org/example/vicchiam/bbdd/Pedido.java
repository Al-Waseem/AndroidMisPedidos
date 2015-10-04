package org.example.vicchiam.bbdd;


import android.database.Cursor;

import org.example.vicchiam.utilidades.Utilidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vicch on 22/09/2015.
 */
public class Pedido implements Serializable {

    private static final long serialVersionUID = 679845876;

    private long id;
    private Date fecha_creacion;
    private Date fecha_entrega;
    private String cuenta_creacion;

    private String cliente_cod;
    private String cliente_cif;
    private String cliente_nom;
    private String cliente_tel;
    private String cliente_mail;
    private String cliente_calle;
    private String cliente_pob;
    private String cliente_cp;
    private String cliente_pais;
    private String cliente_empresa;

    private List<Linea> lineas;

    public Pedido(long id){
        this.id=id;
        this.lineas=new ArrayList<Linea>();
    }

    public Pedido(long id, Date fecha_creacion, Date fecha_entrega, String cuenta_creacion, String cliente_cod,
                  String cliente_cif, String cliente_nom, String cliente_tel, String cliente_mail, String cliente_calle,
                  String cliente_pob, String cliente_cp, String cliente_pais, String cliente_empresa) {
        this.id = id;
        this.fecha_creacion = fecha_creacion;
        this.fecha_entrega = fecha_entrega;
        this.cuenta_creacion = cuenta_creacion;
        this.cliente_cod = cliente_cod;
        this.cliente_cif = cliente_cif;
        this.cliente_nom = cliente_nom;
        this.cliente_tel = cliente_tel;
        this.cliente_mail = cliente_mail;
        this.cliente_calle = cliente_calle;
        this.cliente_pob = cliente_pob;
        this.cliente_cp = cliente_cp;
        this.cliente_pais = cliente_pais;
        this.cliente_empresa = cliente_empresa;

        this.lineas=new ArrayList<Linea>();
    }

    public Pedido(Cursor cursor){
        this.id = cursor.getLong(cursor.getColumnIndex(DBPedido.PEDIDO_CAB_COLS[0]));
        String fc= cursor.getString(cursor.getColumnIndex(DBPedido.PEDIDO_CAB_COLS[1]));
        this.fecha_creacion= Utilidades.StringToDate(fc);
        String fe= cursor.getString(cursor.getColumnIndex(DBPedido.PEDIDO_CAB_COLS[2]));
        this.fecha_entrega = Utilidades.StringToDate(fe);
        this.cuenta_creacion =  cursor.getString(cursor.getColumnIndex(DBPedido.PEDIDO_CAB_COLS[3]));
        this.cliente_cod =  cursor.getString(cursor.getColumnIndex(DBPedido.PEDIDO_CAB_COLS[4]));
        this.cliente_cif = cursor.getString(cursor.getColumnIndex(DBPedido.PEDIDO_CAB_COLS[5]));
        this.cliente_nom = cursor.getString(cursor.getColumnIndex(DBPedido.PEDIDO_CAB_COLS[6]));
        this.cliente_tel = cursor.getString(cursor.getColumnIndex(DBPedido.PEDIDO_CAB_COLS[7]));
        this.cliente_mail = cursor.getString(cursor.getColumnIndex(DBPedido.PEDIDO_CAB_COLS[8]));
        this.cliente_calle = cursor.getString(cursor.getColumnIndex(DBPedido.PEDIDO_CAB_COLS[9]));
        this.cliente_pob = cursor.getString(cursor.getColumnIndex(DBPedido.PEDIDO_CAB_COLS[10]));
        this.cliente_cp = cursor.getString(cursor.getColumnIndex(DBPedido.PEDIDO_CAB_COLS[11]));
        this.cliente_pais = cursor.getString(cursor.getColumnIndex(DBPedido.PEDIDO_CAB_COLS[12]));
        this.cliente_empresa = cursor.getString(cursor.getColumnIndex(DBPedido.PEDIDO_CAB_COLS[13]));
        this.lineas=new ArrayList<Linea>();
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

    public Date getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(Date fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    public Date getFecha_entrega() {
        return fecha_entrega;
    }

    public void setFecha_entrega(Date fecha_entrega) {
        this.fecha_entrega = fecha_entrega;
    }

    public String getCuenta_creacion() {
        return cuenta_creacion;
    }

    public void setCuenta_creacion(String cuenta_creacion) {
        this.cuenta_creacion = cuenta_creacion;
    }

    public String getCliente_cod() {
        return cliente_cod;
    }

    public void setCliente_cod(String cliente_cod) {
        this.cliente_cod = cliente_cod;
    }

    public String getCliente_cif() {
        return cliente_cif;
    }

    public void setCliente_cif(String cliente_cif) {
        this.cliente_cif = cliente_cif;
    }

    public String getCliente_nom() {
        return cliente_nom;
    }

    public void setCliente_nom(String cliente_nom) {
        this.cliente_nom = cliente_nom;
    }

    public String getCliente_tel() {
        return cliente_tel;
    }

    public void setCliente_tel(String cliente_tel) {
        this.cliente_tel = cliente_tel;
    }

    public String getCliente_mail() {
        return cliente_mail;
    }

    public void setCliente_mail(String cliente_mail) {
        this.cliente_mail = cliente_mail;
    }

    public String getCliente_calle() {
        return cliente_calle;
    }

    public void setCliente_calle(String cliente_calle) {
        this.cliente_calle = cliente_calle;
    }

    public String getCliente_pob() {
        return cliente_pob;
    }

    public void setCliente_pob(String cliente_pob) {
        this.cliente_pob = cliente_pob;
    }

    public String getCliente_cp() {
        return cliente_cp;
    }

    public void setCliente_cp(String cliente_cp) {
        this.cliente_cp = cliente_cp;
    }

    public String getCliente_pais() {
        return cliente_pais;
    }

    public void setCliente_pais(String cliente_pais) {
        this.cliente_pais = cliente_pais;
    }

    public String getCliente_empresa() {
        return cliente_empresa;
    }

    public void setCliente_empresa(String cliente_empresa) {
        this.cliente_empresa = cliente_empresa;
    }

    public List<Linea> getLineas() {
        return lineas;
    }

    public void setLineas(List<Linea> lineas) {
        for(Linea l:lineas){
            l.setPedido(this);
        }
        this.lineas = lineas;
    }

    public void setLinea(Linea linea){
        linea.setPedido(this);
        for(int i=0;i<lineas.size();i++){
            if(lineas.get(i).equals(linea)){
                lineas.set(i,linea);
                return;
            }
        }
        this.lineas.add(linea);
    }

    public float getPrecio(){
        float res=0;
        for(Linea l: lineas){
            res+=l.getPrecio();
        }
        return res;
    }

    public float getPrecioImpuestos(){
        float res=0;
        for(Linea l: lineas){
            res+=l.getPrecioImpuestos();
        }
        return res;
    }

    public boolean isEntregado(){
        if(this.fecha_entrega==null){
            return false;
        }
        return (fecha_entrega.compareTo(new Date())<0);
    }

    public String getDireccion(){
        String res="";
        res+=cliente_calle+", "+cliente_pob+"("+cliente_cp+"), "+cliente_pais;
        return res;
    }

    public Linea getLinea(long id){
        for(Linea l:lineas){
            if(l.getId()==id){
                return l;
            }
        }
        return null;
    }

    public void removeLinea(long id){
        Linea aux=null;
        for(Linea l:lineas){
            if(l.getId()==id){
                aux=l;
            }
        }
        if(aux!=null){
            lineas.remove(aux);
        }
    }

    @Override
    public String toString() {
        String res= "Pedido{" +
                "id=" + id +
                ", fecha_creacion=" + fecha_creacion +
                ", fecha_entrega=" + fecha_entrega +
                ", cuenta_creacion='" + cuenta_creacion + '\'' +
                ", cliente_cod='" + cliente_cod + '\'' +
                ", cliente_cif='" + cliente_cif + '\'' +
                ", cliente_nom='" + cliente_nom + '\'' +
                ", cliente_tel='" + cliente_tel + '\'' +
                ", cliente_mail='" + cliente_mail + '\'' +
                ", cliente_calle='" + cliente_calle + '\'' +
                ", cliente_pob='" + cliente_pob + '\'' +
                ", cliente_cp='" + cliente_cp + '\'' +
                ", cliente_pais='" + cliente_pais + '\'' +
                ", cliente_empresa='" + cliente_empresa + '\'' +
                '}';
        res+="\n";
        for(Linea l:lineas){
            res+=l.toString()+"\n";
        }

        return res;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pedido pedido = (Pedido) o;

        return id == pedido.id;

    }

    public String toCSV(){
        String cab="FECHA_CREACION;FECHA_ENTREGA;CUENTA_CREACION;CLIENTE_COD;CLIENTE_CIF;CLIENTE_NOM;CLIENTE_TEL;CLIENTE_MAIL;CLIENTE_CALLE;CLIENTE_POB;CLIENTE_CP;CLIENTE_PAIS;CLIENTE_EMPRESA;CANTIDAD;DESCUENTO;IVA;PRODUCTO_COD;PRODUCTO_NOM;PRODUCTO_FAM;PRODUCTO_UND;PRODUCTO_PRECIO;PRODUCTO_DESC";
        String cuerpo=Utilidades.DateToSmallString(fecha_creacion)+";"+Utilidades.DateToSmallString(fecha_entrega)+";"+cuenta_creacion+";"
                +cliente_cod+";"+cliente_cif+";"+cliente_nom+";"+cliente_tel+";"+cliente_mail+";"+cliente_calle+";"+cliente_pob+";"+cliente_cp+";"+cliente_pais+";"+cliente_empresa;
        String res=cab+"\n";
        for(Linea l:lineas){
            res+=cuerpo+";"+l.toCSV()+"\n";
        }
        return res;
    }



}
