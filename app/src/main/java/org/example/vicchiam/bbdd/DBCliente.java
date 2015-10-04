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
public class DBCliente extends SQLiteOpenHelper{

    public static final String FICHERO_ASOCIADO="clientes.csv";

    public static final String DATABASE_NAME="AppPedidos.db";
    public static final String CLIENTE_TABLA="CLIENTE";

    public static final String[] CLIENTE_COLS={"id","codigo","cif","nombre","telefono","mail","calle","poblacion","cp","pais","empresa"};
    public static final String[] CLIENTE_COLS_TYPE={"INTEGER PRIMARY KEY AUTOINCREMENT","TEXT","TEXT","TEXT","TEXT","TEXT","TEXT","TEXT","TEXT","TEXT","TEXT"};

    public static final int RESULTADOS_A_MOSTRAR=20;

    public static String CREATE_SQL(){
        String SQL="CREATE TABLE IF NOT EXISTS "+CLIENTE_TABLA+" (";
        for(int i=0;i<(CLIENTE_COLS.length-1);i++){
            SQL+=CLIENTE_COLS[i]+" "+CLIENTE_COLS_TYPE[i]+", ";
        }
        SQL+=CLIENTE_COLS[CLIENTE_COLS.length-1]+" "+CLIENTE_COLS_TYPE[CLIENTE_COLS.length-1]+") ";
        return SQL;
    }

    public static final String DROP_SQL="DROP TABLE IF EXISTS "+CLIENTE_TABLA+"";


    public DBCliente(Context context){
        super(context, DATABASE_NAME, null, 1);
        getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SQL());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_SQL);
        onCreate(db);
    }

    public void truncate(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DROP_SQL);
        onCreate(db);
    }

    public List<Cliente> obtenerClientes(){

        List<Cliente> list=new ArrayList<Cliente>();

        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + CLIENTE_TABLA, null);
        res.moveToFirst();
        while(res.isAfterLast()==false){
            list.add(new Cliente(res));
            res.moveToNext();
        }
        res.close();
        return list;
    }

    public List<Cliente> obtenerClientes(int pag){

        pag=(pag-1)*RESULTADOS_A_MOSTRAR;

        List<Cliente> list=new ArrayList<Cliente>();

        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+CLIENTE_TABLA+" LIMIT ?, "+RESULTADOS_A_MOSTRAR,new String[]{Integer.toString(pag)});
        res.moveToFirst();
        while(res.isAfterLast()==false){
            list.add(new Cliente(res));
            res.moveToNext();
        }
        res.close();
        return list;
    }

    //AUTOCOMPLETAR
    public Cursor obtenerClientesAutocompletar(String query, String columna1, String columna2){
        query=query.toLowerCase();
        SQLiteDatabase db=this.getReadableDatabase();
        String SQL="SELECT "+CLIENTE_COLS[0]+" AS _id, "+columna1+","+columna2+" ,"+CLIENTE_COLS[10]+" FROM "+
                CLIENTE_TABLA+" WHERE LOWER("+columna1+") LIKE '%"+query+"%' order by id DESC LIMIT 40";

        Cursor res=db.rawQuery(SQL,null);
        return res;
    }


    public Cliente obtenerClienteId(long id){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + CLIENTE_TABLA + " WHERE " + CLIENTE_COLS[0] + "=" + id, null);
        res.moveToFirst();
        Cliente c=new Cliente(res);
        res.close();
        return c;
    }

    public List<Cliente> buscarClientes(String columna, String valor){

        List<Cliente> list=new ArrayList<Cliente>();

        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+CLIENTE_TABLA+"WHERE ?='?'",new String[] { columna,valor });
        res.moveToFirst();
        while(res.isAfterLast()==false){
            list.add(new Cliente(res));
            res.moveToNext();
        }
        res.close();
        return list;

    }

    public int totalClientes(){
        SQLiteDatabase db=this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, CLIENTE_TABLA);
    }

    public long insertarCliente(String csv){
        String aux[]=csv.split(";");
        SQLiteDatabase db = this.getWritableDatabase();

        //Si el csv tiene alguna fila erronea
        if(aux.length!=(CLIENTE_COLS.length-1)){
            return -1;
        }

        ContentValues cv=new ContentValues();
        for(int i=0;i<aux.length;i++) {
            cv.put(CLIENTE_COLS[i+1], aux[i]);
        }
        long id=db.insert(CLIENTE_TABLA, null, cv);
        return id;
    }

    public Cliente insertarCliente(String codigo, String cif, String nombre, String telefono,
                                   String mail, String calle, String poblacion, String cp, String pais, String empresa){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(CLIENTE_COLS[1],codigo);
        cv.put(CLIENTE_COLS[2],cif);
        cv.put(CLIENTE_COLS[3],nombre);
        cv.put(CLIENTE_COLS[4],telefono);
        cv.put(CLIENTE_COLS[5],mail);
        cv.put(CLIENTE_COLS[6],calle);
        cv.put(CLIENTE_COLS[7],poblacion);
        cv.put(CLIENTE_COLS[8],cp);
        cv.put(CLIENTE_COLS[9],pais);
        cv.put(CLIENTE_COLS[10], empresa);
        long id=db.insert(CLIENTE_TABLA, null, cv);
        return new Cliente(id,codigo,cif,nombre,telefono,mail,calle,poblacion,cp,pais,empresa);
    }

    public Cliente insertarCliente(Cliente cliente){
        return insertarCliente(cliente.getCodigo(),cliente.getCif(),cliente.getNombre(),
                cliente.getTelefono(),cliente.getMail(), cliente.getCalle(),cliente.getPoblacion(),
                cliente.getCp(),cliente.getPais(),cliente.getEmpresa());
    }

    public Cliente actualizarCliente(String codigo, String cif, String nombre, String telefono,
                                     String mail, String calle, String poblacion, String cp, String pais, String empresa,long id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(CLIENTE_COLS[1],codigo);
        cv.put(CLIENTE_COLS[2],cif);
        cv.put(CLIENTE_COLS[3],nombre);
        cv.put(CLIENTE_COLS[4],telefono);
        cv.put(CLIENTE_COLS[5],mail);
        cv.put(CLIENTE_COLS[6],calle);
        cv.put(CLIENTE_COLS[7],poblacion);
        cv.put(CLIENTE_COLS[8],cp);
        cv.put(CLIENTE_COLS[9],pais);
        cv.put(CLIENTE_COLS[10], empresa);
        db.update(CLIENTE_TABLA,cv,"id = ?",new String[]{Long.toString(id)});
        return new Cliente(id,codigo,cif,nombre,telefono,mail,calle,poblacion,cp,pais,empresa);
    }

    public Cliente actualizarCliente(Cliente cliente){
        return actualizarCliente(cliente.getCodigo(),cliente.getCif(),cliente.getNombre(),
                cliente.getTelefono(),cliente.getMail(), cliente.getCalle(),cliente.getPoblacion(),
                cliente.getCp(),cliente.getPais(),cliente.getEmpresa(),cliente.getId());
    }

    public int borrarCliente(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CLIENTE_TABLA,"id = ? ",new String[] { Long.toString(id) });
    }

    public int borrarCliente(Cliente cliente) {
        return borrarCliente(cliente.getId());
    }



}

