package org.example.vicchiam.utilidades;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import org.example.vicchiam.bbdd.DBCliente;
import org.example.vicchiam.bbdd.DBPedido;
import org.example.vicchiam.bbdd.DBProducto;

/**
 * Created by vicch on 26/09/2015.
 */
public class BBDD {

    public static DBCliente dbCliente;
    public static DBProducto dbProducto;
    public static DBPedido dbPedido;


    public static void iniciarBaseDatos(Context context){
        dbPedido=new DBPedido(context);
        //dbPedido.iniPruebas();
        dbCliente=new DBCliente(context);
        dbProducto=new DBProducto(context);
        SQLiteDatabase db=dbPedido.getDb();
        db.execSQL(dbPedido.PRAGMA_SQL);
        db.execSQL(dbPedido.CREATE_SQL_PEDIDO_CAB());
        db.execSQL(dbPedido.CREATE_SQL_PEDIDO_LINEA());
        db.execSQL(dbCliente.CREATE_SQL());
        db.execSQL(dbProducto.CREATE_SQL());
    }

    public static boolean tablasCargadas(Context context){
        SharedPreferences pref=context.getSharedPreferences("AppPedidos", Context.MODE_PRIVATE);
        return pref.getBoolean("tablasCargadas",false);
    }

    public static void cargarTablas(Context context){
        SharedPreferences pref=context.getSharedPreferences("AppPedidos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();
        editor.putBoolean("tablasCargadas",true);
        editor.commit();
    }

}
