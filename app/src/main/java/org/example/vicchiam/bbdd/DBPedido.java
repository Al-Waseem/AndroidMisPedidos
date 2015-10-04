package org.example.vicchiam.bbdd;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import org.example.vicchiam.utilidades.Utilidades;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vicch on 22/09/2015.
 */
public class DBPedido extends SQLiteOpenHelper {




    public static final String DATABASE_NAME="AppPedidos.db";
    public static final String PRAGMA_SQL="PRAGMA foreign_keys = ON;";
    private static final int RESULTADOS_A_MOSTRAR=20;

    /********PEDIDO**********************/

    public static final String FICHERO_ASOCIADO_CAB="pedidos.csv";

    public static final String PEDIDO_CAB_TABLA="PEDIDO";

    public static final String[] PEDIDO_CAB_COLS={"id","fecha_creacion","fecha_entrega","cuenta_creacion","cliente_cod",
            "cliente_cif","cliente_nom","cliente_tel","cliente_mail", "cliente_calle","cliente_pob","cliente_cp","cliente_pais","cliente_empresa"};

    public static final String[] PEDIDO_CAB_COLS_TYPE={"INTEGER PRIMARY KEY AUTOINCREMENT","TEXT","TEXT","TEXT","TEXT","TEXT","TEXT","TEXT","TEXT","TEXT","TEXT","TEXT","TEXT","TEXT"};

    public static String CREATE_SQL_PEDIDO_CAB(){
        String SQL="CREATE TABLE IF NOT EXISTS "+PEDIDO_CAB_TABLA+" (";
        for(int i=0;i<(PEDIDO_CAB_COLS.length-1);i++){
            SQL+=PEDIDO_CAB_COLS[i]+" "+PEDIDO_CAB_COLS_TYPE[i]+", ";
        }
        SQL+=PEDIDO_CAB_COLS[PEDIDO_CAB_COLS.length-1]+" "+PEDIDO_CAB_COLS_TYPE[PEDIDO_CAB_COLS.length-1]+") ";
        return SQL;
    }

    public static final String DROP_SQL_PEDIDO_CAB="DROP TABLE IF EXISTS "+PEDIDO_CAB_TABLA+"";

    /**************LINEA****************************/

    public static final String FICHERO_ASOCIADO_LINEA="lineas.csv";

    public static final String PEDIDO_LINEA_TABLA="LINEA";

    public static final String[] PEDIDO_LINEA_COLS={"id","id_pedido","cantidad","descuento",
            "iva","codigo","nombre","familia","und","descripcion","precio_und"};

    public static final String[] PEDIDO_LINEA_COLS_TYPE={" INTEGER PRIMARY KEY AUTOINCREMENT"," INTEGER","REAL","REAL","REAL",
        "TEXT","TEXT","TEXT","TEXT","TEXT","REAL"};

    public static String CREATE_SQL_PEDIDO_LINEA(){
        String SQL="CREATE TABLE IF NOT EXISTS "+PEDIDO_LINEA_TABLA+" (";
        for(int i=0;i<(PEDIDO_LINEA_COLS.length-1);i++){
            SQL+=PEDIDO_LINEA_COLS[i]+" "+PEDIDO_LINEA_COLS_TYPE[i]+", ";
        }
        SQL+=PEDIDO_LINEA_COLS[PEDIDO_LINEA_COLS.length-1]+" "+PEDIDO_LINEA_COLS_TYPE[PEDIDO_LINEA_COLS.length-1]+
                ", FOREIGN KEY(id_pedido) REFERENCES PEDIDO(id) ON DELETE CASCADE)";
        return SQL;
    }

    public static final String DROP_SQL_PEDIDO_LINEA="DROP TABLE IF EXISTS "+PEDIDO_LINEA_TABLA+"";

    /*************************************************************************************************************/
    private void crearPruebas(){
        Pedido p=insertarPedido(Utilidades.StringToSmallDate("01-03-2015"),Utilidades.StringToSmallDate("01-03-2015"),"una@iuna.com","100","N12345678","Pepe García","968774452","otra@otra.com","Mayor Nº20 pta 3","Valencia","46200","España","Bussiness");
        insertarLinea(p.getId(),6,0.1f,0.21f,"1000","Silla","Sillas","und","Silla de madera azul",25.2f);
        insertarLinea(p.getId(),2,0.1f,0.21f,"1001","Sofá","Sofas","und","Sofa de 3 plazas",250f);

        Pedido p2=insertarPedido(Utilidades.StringToSmallDate("01-06-2015"), Utilidades.StringToSmallDate("01-06-2015"), "dos@dos.com", "101", "N87654321", "Eva Marín", "9678421456", "otra2@otra2.com", "Menor Nº40 pta 8", "Alcira", "46287", "España", "Sofas Alcira");
        insertarLinea(p2.getId(),2,0.1f,0.21f,"1002","Sofa 3 plazas","Sofas","und","Sofa de 3 plazas de piel blanco",200f);
        insertarLinea(p2.getId(),10,0.1f,0.21f,"1003","Pintura","Otros","lts","Pintura roja",5f);

        Pedido p3=insertarPedido(Utilidades.StringToSmallDate("01-07-2015"),Utilidades.StringToSmallDate("01-07-2015"),"tres@tres.com","102","N87654111","Alberto Sanz","967887417","otra3@otra3.com","Avd Constitución Nº25 pta 8","Silla","46288","España","Muebles Silla");
        insertarLinea(p3.getId(),1,0.1f,0.21f,"1004","Visillo","Otros","und","Visillo amarillo",10f);

        Pedido p4=insertarPedido(Utilidades.StringToSmallDate("15-07-2015"),Utilidades.StringToSmallDate("15-07-2015"),"cuatro@cuatro.com","103","N87777111","Maria Perez","967887417","otra4@otra4.com","Plaza Nº25 pta 8","Valencia","46250","España","Pinturas Valencia");
        insertarLinea(p4.getId(),10,0.1f,0.21f,"1005","Pintura","Otros","lts","Pintura verde",5f);

        Pedido p5=insertarPedido(Utilidades.StringToSmallDate("01-08-2015"),Utilidades.StringToSmallDate("01-08-2015"),"cinco@cinco.com","104","N87777888","Jose Mas","967778896","otra5@otra5.com","Pol Ind Cercano Nº25 pta 8","Valencia","46250","España","Conforama");
        insertarLinea(p5.getId(),2,0.1f,0.21f,"1001","Sofá","Sofas","und","Sofa de 3 plazas",250f);
        insertarLinea(p5.getId(),2,0.1f,0.21f,"1006","Silla","Sillas","und","Silla verde",50f);

        Pedido p6=insertarPedido(Utilidades.StringToSmallDate("04-09-2015"),Utilidades.StringToSmallDate("04-09-2015"),"seis@seis.com","105","N84477111","Arturo Pans","987887417","otra4@otra4.com","Plaza Nº25 pta 8","Albacente","46250","España","Albacete Sofas");
        insertarLinea(p6.getId(),10,0.1f,0.21f,"1005","Pintura","Otros","lts","Pintura verde",5f);

        Pedido p7=insertarPedido(Utilidades.StringToSmallDate("06-10-2015"),Utilidades.StringToSmallDate("06-10-2015"),"cuatro@cuatro.com","103","N87777111","Maria Perez","967887417","otra4@otra4.com","Plaza Nº25 pta 8","Valencia","46250","España","Pinturas Valencia");
        insertarLinea(p7.getId(),10,0.1f,0.21f,"1005","Pintura","Otros","lts","Pintura verde",5f);

        Pedido p8=insertarPedido(Utilidades.StringToSmallDate("10-10-2015"),Utilidades.StringToSmallDate("10-10-2015"),"cuatro@cuatro.com","103","N87777111","Maria Perez","967887417","otra4@otra4.com","Plaza Nº25 pta 8","Valencia","46250","España","Pinturas Valencia");
        insertarLinea(p8.getId(),20,0.1f,0.21f,"1005","Pintura","Otros","lts","Pintura verde",5f);

        Pedido p9=insertarPedido(Utilidades.StringToSmallDate("04-10-2015"),Utilidades.StringToSmallDate("04-10-2015"),"seis@seis.com","105","N84477111","Arturo Pans","987887417","otra4@otra4.com","Plaza Nº25 pta 8","Albacente","46250","España","Albacete Sofas");
        insertarLinea(p9.getId(),2,0.1f,0.21f,"1006","Silla","Sillas","und","Silla verde",50f);
        insertarLinea(p9.getId(),2,0.1f,0.21f,"1001","Sofá","Sofas","und","Sofa de 3 plazas",250f);
        insertarLinea(p9.getId(),6,0.1f,0.21f,"1000","Silla","Sillas","und","Silla de madera azul",25.2f);
        insertarLinea(p9.getId(),10,0.1f,0.21f,"1005","Pintura","Otros","lts","Pintura verde",5f);

        Pedido p10=insertarPedido(Utilidades.StringToSmallDate("01-11-2015"),Utilidades.StringToSmallDate("01-11-2015"),"tres@tres.com","102","N87654111","Alberto Sanz","967887417","otra3@otra3.com","Avd Constitución Nº25 pta 8","Silla","46288","España","Muebles Silla");
        insertarLinea(p10.getId(),1,0.1f,0.21f,"1004","Visillo","Otros","und","Visillo amarillo",10f);

        Pedido p11=insertarPedido(Utilidades.StringToSmallDate("15-11-2015"),Utilidades.StringToSmallDate("15-11-2015"),"cuatro@cuatro.com","103","N87777111","Maria Perez","967887417","otra4@otra4.com","Plaza Nº25 pta 8","Valencia","46250","España","Pinturas Valencia");
        insertarLinea(p11.getId(),10,0.1f,0.21f,"1005","Pintura","Otros","lts","Pintura verde",5f);
    }


    /*************************************************************************************************************/

    private Context context;

    public DBPedido(Context context){
        super(context, DATABASE_NAME, null, 1);
        this.context=context;
    }

    public SQLiteDatabase getDb(){
        return getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PRAGMA_SQL);
        db.execSQL(CREATE_SQL_PEDIDO_CAB());
        db.execSQL(CREATE_SQL_PEDIDO_LINEA());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_SQL_PEDIDO_CAB);
        onCreate(db);
    }

    public void iniPruebas(){
        //Error getDatabase called recursively
        SharedPreferences pref=context.getSharedPreferences("AppPedidos", Context.MODE_PRIVATE);
        if(!pref.getBoolean("BBDD_INI",false)){
            crearPruebas();
            SharedPreferences.Editor editor=pref.edit();
            editor.putBoolean("BBDD_INI",true);
            editor.commit();
        }
    }

    public void truncate(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DROP_SQL_PEDIDO_CAB);
        db.execSQL(DROP_SQL_PEDIDO_LINEA);
        onCreate(db);
    }

    public Cursor obtenerEstadisticas(){
        SQLiteDatabase db=this.getReadableDatabase();
        try {
            Cursor res = db.rawQuery("SELECT sum(l.precio_und*l.cantidad*(1-l.descuento)*(1+l.iva)) AS VENTAS, strftime('%m',p.fecha_entrega) AS MES,strftime('%Y',p.fecha_entrega) AS ANYO, fecha_entrega FROM PEDIDO p, LINEA l where l.id_pedido=p.id GROUP BY strftime('%m-%Y',p.fecha_entrega) ORDER BY fecha_entrega DESC LIMIT 24", null);
            //Cursor res=db.rawQuery("SELECT strftime('%m-%Y',p.fecha_entrega) AS MES FROM PEDIDO p GROUP BY strftime('%m-%Y',p.fecha_entrega)",null);
            return res;
        }catch (SQLiteException e){
            return null;
        }
    }

    public List<Pedido> obtenerPedidos(){
        List<Pedido> list=new ArrayList<Pedido>();

        SQLiteDatabase db=this.getReadableDatabase();
        try {
            Cursor res = db.rawQuery("SELECT * FROM " + PEDIDO_CAB_TABLA + " WHERE date(" + PEDIDO_CAB_COLS[1] + ")>datetime('now','-6 months') ORDER BY date(" + PEDIDO_CAB_COLS[2] + ") DESC", null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                long id_pedido = res.getLong(res.getColumnIndex(PEDIDO_CAB_COLS[0]));
                List<Linea> lineas = obtenerLineas(id_pedido);
                Pedido p = new Pedido(res);
                p.setLineas(lineas);
                list.add(p);
                res.moveToNext();
            }
            res.close();
        }catch (SQLiteException e){
            e.printStackTrace();
        }

        return list;
    }

    public List<Pedido> obtenerPedidos(int pag){
        List<Pedido> list=new ArrayList<Pedido>();

        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + DBPedido.PEDIDO_CAB_TABLA + " LIMIT ?, " + RESULTADOS_A_MOSTRAR, new String[]{Integer.toString(pag)});
        res.moveToFirst();
        while (res.isAfterLast()==false){
            long id_pedido=res.getLong(res.getColumnIndex(PEDIDO_CAB_COLS[0]));
            List<Linea> lineas=obtenerLineas(id_pedido);
            Pedido p=new Pedido(res);
            p.setLineas(lineas);
            list.add(p);
            res.moveToNext();
        }
        res.close();
        return list;
    }

    public Pedido obtenerPedidoId(long id){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("SELECT * FROM " + PEDIDO_CAB_TABLA + " WHERE " + PEDIDO_CAB_COLS[0] + "=?", new String[]{Long.toString(id)});
        res.moveToFirst();
        Pedido p=new Pedido(res);
        List<Linea> lineas=obtenerLineas(id);
        p.setLineas(lineas);
        res.close();
        return p;
    }

    public List<Pedido> buscarPedidos(String columna, String valor){
        List<Pedido> list=new ArrayList<Pedido>();

        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("SELECT * FROM " + DBPedido.PEDIDO_CAB_TABLA + " WHERE " + columna + "='" + valor + "'", null);
        res.moveToFirst();
        while (res.isAfterLast()==false){
            long id_pedido=res.getLong(res.getColumnIndex(PEDIDO_CAB_COLS[0]));
            List<Linea> lineas=obtenerLineas(id_pedido);
            Pedido p=new Pedido(res);
            p.setLineas(lineas);
            list.add(p);
            res.moveToNext();
        }
        res.close();
        return list;
    }

    public int totalPedidos(){
        SQLiteDatabase db=this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, PEDIDO_CAB_TABLA);
    }

    //AUTOCOMPLETAR
    public Cursor obtenerPedidosAutocompletar(String query, String columna){
        query=query.toLowerCase();
        SQLiteDatabase db=this.getReadableDatabase();
        String SQL="SELECT MIN("+PEDIDO_CAB_COLS[0]+") AS _id, "+PEDIDO_CAB_COLS[4]+", "+PEDIDO_CAB_COLS[13]+" FROM "+
                PEDIDO_CAB_TABLA+" WHERE LOWER("+columna+") LIKE '%"+query+"%' GROUP BY "+PEDIDO_CAB_COLS[4]+", "+PEDIDO_CAB_COLS[13]+"  order by id DESC";
        //Log.d("SQL",SQL);
        Cursor res=db.rawQuery(SQL, null);
        return res;
    }

    //Devuelve el Pedido sin lineas
    public Pedido insertarPedido(Date fecha_creacion, Date fecha_entrega, String cuenta_creacion, String cliente_cod,
                                 String cliente_cif, String cliente_nom, String cliente_tel, String cliente_mail, String cliente_calle,
                                 String cliente_pob, String cliente_cp, String cliente_pais, String cliente_empresa){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(PEDIDO_CAB_COLS[1],Utilidades.DateToString(fecha_creacion));
        cv.put(PEDIDO_CAB_COLS[2],Utilidades.DateToString(fecha_entrega));
        cv.put(PEDIDO_CAB_COLS[3],cuenta_creacion);
        cv.put(PEDIDO_CAB_COLS[4],cliente_cod);
        cv.put(PEDIDO_CAB_COLS[5],cliente_cif);
        cv.put(PEDIDO_CAB_COLS[6],cliente_nom);
        cv.put(PEDIDO_CAB_COLS[7],cliente_tel);
        cv.put(PEDIDO_CAB_COLS[8],cliente_mail);
        cv.put(PEDIDO_CAB_COLS[9],cliente_calle);
        cv.put(PEDIDO_CAB_COLS[10],cliente_pob);
        cv.put(PEDIDO_CAB_COLS[11],cliente_cp);
        cv.put(PEDIDO_CAB_COLS[12],cliente_pais);
        cv.put(PEDIDO_CAB_COLS[13], cliente_empresa);
        long id=db.insert(PEDIDO_CAB_TABLA,null,cv);
        return new Pedido(id,fecha_creacion,fecha_entrega,cuenta_creacion,cliente_cod,
                cliente_cif,cliente_nom,cliente_tel,cliente_mail,cliente_calle,
                cliente_pob,cliente_cp,cliente_pais,cliente_empresa);
    }

    public Pedido insertarPedido(Pedido pedido){
        return insertarPedido(pedido.getFecha_creacion(),pedido.getFecha_entrega(),pedido.getCuenta_creacion(),pedido.getCliente_cod(),pedido.getCliente_cif(),pedido.getCliente_nom(),
                pedido.getCliente_tel(),pedido.getCliente_mail(),pedido.getCliente_calle(),pedido.getCliente_pob(),pedido.getCliente_cp(),pedido.getCliente_pais(),pedido.getCliente_empresa());
    }

    //Devuelve el Pedido sin lineas
    public Pedido actualizarPedido(long id,Date fecha_creacion, Date fecha_entrega, String cuenta_creacion, String cliente_cod,
                                   String cliente_cif, String cliente_nom, String cliente_tel, String cliente_mail, String cliente_calle,
                                   String cliente_pob, String cliente_cp, String cliente_pais, String cliente_empresa){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(PEDIDO_CAB_COLS[1], Utilidades.DateToString(fecha_creacion));
        cv.put(PEDIDO_CAB_COLS[2],Utilidades.DateToString(fecha_entrega));
        cv.put(PEDIDO_CAB_COLS[3],cuenta_creacion);
        cv.put(PEDIDO_CAB_COLS[4],cliente_cod);
        cv.put(PEDIDO_CAB_COLS[5],cliente_cif);
        cv.put(PEDIDO_CAB_COLS[6],cliente_nom);
        cv.put(PEDIDO_CAB_COLS[7],cliente_tel);
        cv.put(PEDIDO_CAB_COLS[8],cliente_mail);
        cv.put(PEDIDO_CAB_COLS[9],cliente_calle);
        cv.put(PEDIDO_CAB_COLS[10],cliente_pob);
        cv.put(PEDIDO_CAB_COLS[11],cliente_cp);
        cv.put(PEDIDO_CAB_COLS[12],cliente_pais);
        cv.put(PEDIDO_CAB_COLS[13],cliente_empresa);
        db.update(PEDIDO_CAB_TABLA, cv, "id="+id, null);
        return new Pedido(id,fecha_creacion,fecha_entrega,cuenta_creacion,cliente_cod,
                cliente_cif,cliente_nom,cliente_tel,cliente_mail,cliente_calle,
                cliente_pob,cliente_cp,cliente_pais,cliente_empresa);
    }

    public Pedido actualizarPedido(Pedido pedido){
        return actualizarPedido(pedido.getId(), pedido.getFecha_creacion(), pedido.getFecha_entrega(), pedido.getCuenta_creacion(), pedido.getCliente_cod(), pedido.getCliente_cif(), pedido.getCliente_nom(),
                pedido.getCliente_tel(), pedido.getCliente_mail(), pedido.getCliente_calle(), pedido.getCliente_pob(), pedido.getCliente_cp(), pedido.getCliente_pais(), pedido.getCliente_empresa());
    }

    public int borrarPedido(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(PEDIDO_CAB_TABLA, "id = ? ", new String[]{Long.toString(id)});
    }

    public int borrarPedido(Pedido pedido){
        return borrarPedido(pedido.getId());
    }



    /***************METODOS LINEAS****************************/

    public List<Linea> obtenerLineas(int pag){
        List<Linea> list=new ArrayList<Linea>();

        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("SELECT * FROM " + DBPedido.PEDIDO_LINEA_TABLA + " LIMIT ?, " + RESULTADOS_A_MOSTRAR, new String[]{Integer.toString(pag)});
        res.moveToFirst();
        while (res.isAfterLast()==false){
            list.add(new Linea(res));
            res.moveToNext();
        }
        res.close();
        return list;
    }

    public List<Linea> obtenerLineas(long id_pedido){
        List<Linea> list=new ArrayList<Linea>();
        SQLiteDatabase db=getReadableDatabase();

        Cursor res=db.rawQuery("SELECT * FROM " + DBPedido.PEDIDO_LINEA_TABLA + " WHERE " + PEDIDO_LINEA_COLS[1] + "="+id_pedido,null);
        res.moveToFirst();
        while (res.isAfterLast()==false){
            list.add(new Linea(res));
            res.moveToNext();
        }
        res.close();
        return list;
    }

    public Linea obtenerLineaId(long id){
        SQLiteDatabase db=getReadableDatabase();
        Cursor res=db.rawQuery("SELECT * FROM " + DBPedido.PEDIDO_LINEA_TABLA + " WHERE " + PEDIDO_LINEA_COLS[0] + "="+id,null);
        res.moveToFirst();
        Linea linea=new Linea(res);
        res.close();
        return linea;
    }

    //En este caso van sin el pedido -> pedido=null
    public List<Linea> buscarLineas(String columna, String valor){
        List<Linea> list=new ArrayList<Linea>();

        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("SELECT * FROM " + DBPedido.PEDIDO_LINEA_TABLA + " WHERE ?='?'", new String[]{columna, valor});
        res.moveToFirst();
        while (res.isAfterLast()==false){
            list.add(new Linea(res));
            res.moveToNext();
        }
        res.close();
        return list;
    }

    public int totalLineas(){
        SQLiteDatabase db=this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, PEDIDO_LINEA_TABLA);
    }



    //Devuelve la linea sin pedido
    public Linea insertarLinea(long id_pedido, float cantidad,  float descuento, float iva,
                               String producto_cod, String producto_nom, String producto_fam, String producto_und, String producto_desc, float producto_precio){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(PEDIDO_LINEA_COLS[1],id_pedido);
        cv.put(PEDIDO_LINEA_COLS[2],cantidad);
        cv.put(PEDIDO_LINEA_COLS[3],descuento);
        cv.put(PEDIDO_LINEA_COLS[4],iva);
        cv.put(PEDIDO_LINEA_COLS[5],producto_cod);
        cv.put(PEDIDO_LINEA_COLS[6],producto_nom);
        cv.put(PEDIDO_LINEA_COLS[7],producto_fam);
        cv.put(PEDIDO_LINEA_COLS[8],producto_und);
        cv.put(PEDIDO_LINEA_COLS[9],producto_desc);
        cv.put(PEDIDO_LINEA_COLS[10], producto_precio);
        long id=db.insert(PEDIDO_LINEA_TABLA,null,cv);
        return new Linea(id, cantidad, descuento, iva, producto_cod, producto_nom, producto_fam, producto_und, producto_desc, producto_precio);
    }

    public Linea insertarLinea(Linea linea){
        return insertarLinea(linea.getPedido().getId(), linea.getCantidad(), linea.getDescuento(),
                linea.getIva(), linea.getProducto_cod(), linea.getProducto_nom(), linea.getProducto_fam(), linea.getProducto_und(), linea.getProducto_desc(), linea.getProducto_precio());
    }




    //Devuelve la linea sin pedido
    public Linea actualizarLinea(long id,long id_pedido, float cantidad, float descuento, float iva,
                                 String producto_cod, String producto_nom, String producto_fam, String producto_und, String producto_desc, float producto_precio){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(PEDIDO_LINEA_COLS[1],id_pedido);
        cv.put(PEDIDO_LINEA_COLS[2],cantidad);
        cv.put(PEDIDO_LINEA_COLS[3],descuento);
        cv.put(PEDIDO_LINEA_COLS[4],iva);
        cv.put(PEDIDO_LINEA_COLS[5],producto_cod);
        cv.put(PEDIDO_LINEA_COLS[6],producto_nom);
        cv.put(PEDIDO_LINEA_COLS[7],producto_fam);
        cv.put(PEDIDO_LINEA_COLS[8],producto_und);
        cv.put(PEDIDO_LINEA_COLS[9],producto_desc);
        cv.put(PEDIDO_LINEA_COLS[10], producto_precio);
        db.update(PEDIDO_LINEA_TABLA, cv, "id="+id, null);
        return new Linea(id,  cantidad, descuento, iva, producto_cod, producto_nom, producto_fam, producto_und,producto_desc, producto_precio);
    }

    public Linea actualizarLinea(Linea linea){
        return actualizarLinea(linea.getId(), linea.getPedido().getId(), linea.getCantidad(), linea.getDescuento(),
                linea.getIva(), linea.getProducto_cod(), linea.getProducto_nom(), linea.getProducto_fam(), linea.getProducto_und(), linea.getProducto_desc(), linea.getProducto_precio());
    }

    public int borrarLinea(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(PEDIDO_LINEA_TABLA,"id="+id,null);
    }

    public int borrarLinea(Linea linea){
        return borrarLinea(linea.getId());
    }

}
