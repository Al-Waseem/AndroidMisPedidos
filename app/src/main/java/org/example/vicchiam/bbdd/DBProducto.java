package org.example.vicchiam.bbdd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vicch on 22/09/2015.
 */
public class DBProducto extends SQLiteOpenHelper {

    public static final String FICHERO_ASOCIADO="productos.csv";

    public static final String DATABASE_NAME="AppPedidos.db";
    public static final String PRODUCTO_TABLA="PRODUCTO";

    public static final String[] PRODUCTO_COLS={"id","codigo","nombre","familia","und","precio","descripcion"};
    public static final String[] PRODUCTO_COLS_TYPE={"INTEGER PRIMARY KEY AUTOINCREMENT","TEXT","TEXT","TEXT","TEXT","REAL","TEXT"};

    public static final int RESULTADOS_A_MOSTRAR=20;

    public static String CREATE_SQL(){
        String SQL="CREATE TABLE IF NOT EXISTS "+PRODUCTO_TABLA+" (";
        for(int i=0;i<(PRODUCTO_COLS.length-1);i++){
            SQL+=PRODUCTO_COLS[i]+" "+PRODUCTO_COLS_TYPE[i]+", ";
        }
        SQL+=PRODUCTO_COLS[PRODUCTO_COLS.length-1]+" "+PRODUCTO_COLS_TYPE[PRODUCTO_COLS.length-1]+") ";
        return SQL;
    }

    private static final String DROP_SQL="DROP TABLE IF EXISTS "+PRODUCTO_TABLA;


    public DBProducto(Context context){
        super(context, DATABASE_NAME, null, 1);
        getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_SQL());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newversion){
        db.execSQL(DROP_SQL);
        onCreate(db);
    }

    public void truncate(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DROP_SQL);
        onCreate(db);
    }


    public List<Producto> obtenerProductos(){
        List<Producto> list=new ArrayList<Producto>();

        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("SELECT * FROM "+PRODUCTO_TABLA,null);
        res.moveToFirst();
        while(res.isAfterLast()==false){
            list.add(new Producto(res));
            res.moveToNext();
        }
        return list;

    }

    public List<Producto> obtenerProductos(int pag){
        pag=(pag-1)*RESULTADOS_A_MOSTRAR;

        List<Producto> list=new ArrayList<Producto>();

        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("SELECT * FROM " + PRODUCTO_TABLA + " LIMIT ?, " + RESULTADOS_A_MOSTRAR, new String[]{Integer.toString(pag)});
        res.moveToFirst();
        while(res.isAfterLast()==false){
            list.add(new Producto(res));
            res.moveToNext();
        }
        res.close();
        return list;

    }

    public Producto obtenerProductoId(long id){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("SELECT * FROM " + PRODUCTO_TABLA + " WHERE " + PRODUCTO_COLS[0] + "=?", new String[]{Long.toString(id)});
        res.moveToFirst();
        return new Producto(res);
    }

    //AUTOCOMPLETAR
    public Cursor obtenerProductosAutocompletar(String query, String columna1, String columna2){
        query=query.toLowerCase();
        SQLiteDatabase db=this.getReadableDatabase();
        String SQL="SELECT "+PRODUCTO_COLS[0]+" AS _id, "+columna1+", "+columna2+" FROM "+
                PRODUCTO_TABLA+" WHERE LOWER("+columna1+") LIKE '%"+query+"%' order by id DESC LIMIT 40 ";
        Cursor res=db.rawQuery(SQL,null);
        return res;
    }

    public List<Producto> buscarProductos(String columna, String valor){
        List<Producto> list=new ArrayList<Producto>();

        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("SELECT * FROM "+PRODUCTO_TABLA+" WHERE ?='?'",new String[] { columna,valor });
        res.moveToFirst();
        while(res.isAfterLast()==false){
            list.add(new Producto(res));
            res.moveToNext();
        }
        res.close();
        return list;
    }

    public int totalProductos(){
        SQLiteDatabase db=this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, PRODUCTO_TABLA);
    }

    public long insertarProducto(String csv){
        String aux[]=csv.split(";");

        //Si el csv tiene alguna fila erronea
        if(aux.length!=(PRODUCTO_COLS.length-1)){
            return -1;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        for(int i=0;i<aux.length;i++) {
            cv.put(PRODUCTO_COLS[i + 1], aux[i]);
        }
        long id=db.insert(PRODUCTO_TABLA, null, cv);
        return id;
    }

    public Producto insertarProducto(String codigo, String nombre, String familia, String unidad, float precio, String descripcion){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(PRODUCTO_COLS[1],codigo);
        cv.put(PRODUCTO_COLS[2],nombre);
        cv.put(PRODUCTO_COLS[3],familia);
        cv.put(PRODUCTO_COLS[4],unidad);
        cv.put(PRODUCTO_COLS[5],precio);
        cv.put(PRODUCTO_COLS[6],descripcion);
        long id=db.insert(PRODUCTO_TABLA,null,cv);
        return new Producto(id,codigo,nombre,familia,unidad,precio,descripcion);
    }

    public Producto insertarProducto(Producto producto){
        return insertarProducto(producto.getCodigo(),producto.getNombre(),producto.getFamilia(),producto.getUnidad(),producto.getPrecio(),producto.getDescripcion());
    }

    public Producto actualizarProducto(long id, String codigo, String nombre, String familia, String unidad, float precio, String descripcion){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(PRODUCTO_COLS[1],codigo);
        cv.put(PRODUCTO_COLS[2],nombre);
        cv.put(PRODUCTO_COLS[3],familia);
        cv.put(PRODUCTO_COLS[4],unidad);
        cv.put(PRODUCTO_COLS[5],precio);
        cv.put(PRODUCTO_COLS[6],descripcion);
        db.update(PRODUCTO_TABLA,cv,"id=?",new String[]{Long.toString(id)});
        return new Producto(id,codigo,nombre,familia,unidad,precio,descripcion);
    }

    public Producto actualizarProducto(Producto producto){
        return actualizarProducto(producto.getId(),producto.getCodigo(),producto.getNombre(),producto.getFamilia(),producto.getUnidad(),producto.getPrecio(),producto.getDescripcion());
    }

    public int borrarProducto(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(PRODUCTO_TABLA,"id = ? ",new String[] { Long.toString(id) });
    }

    public int borrarProducto(Producto producto){
        return borrarProducto(producto.getId());
    }

}
