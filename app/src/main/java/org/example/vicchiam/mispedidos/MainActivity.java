package org.example.vicchiam.mispedidos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import org.example.vicchiam.utilidades.BBDD;
import org.example.vicchiam.utilidades.MyDrive;
import org.example.vicchiam.utilidades.Utilidades;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Activity self;
    Intent service;
    TextView num_cliente, num_prod;
    ListView listView;

    private boolean tablasCreadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /******TextView***************************************/
        num_cliente=(TextView)findViewById(R.id.m_cliente);
        num_prod=(TextView)findViewById(R.id.m_prod);

        listView=(ListView)findViewById(R.id.list_ventas);

        self=this;

        iniApp();

        iniToolbar();
        iniNavigationView();

        //Tabla inicializadas
        /*tablasCreadas=false;
        if(BBDD.tablasCargadas(this)){
            tablasCreadas=true;
        }*/
    }

    private void iniApp(){
        BBDD.iniciarBaseDatos(self);

        Utilidades.cargarPreferencias(self);

        if(MyDrive.email==null || MyDrive.idCarpeta==null){
            startActivity(new Intent(this,AccountActivity.class));
        }
        else{
            MyDrive.credencial= GoogleAccountCredential.usingOAuth2(self, Arrays.asList(DriveScopes.DRIVE));
            MyDrive.credencial.setSelectedAccountName(MyDrive.email);
            MyDrive.servicio=new Drive.Builder(AndroidHttp.newCompatibleTransport(),new GsonFactory(),MyDrive.credencial).build();
        }

    }

    @Override
    protected void onStart(){
        super.onStart();
        service=new Intent(self,ServicioMyDrive.class);
        startService(service);
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(drawerLayout!=null) {
            drawerLayout.openDrawer(Gravity.LEFT);
            cargarDatos();
        }
    }

    @Override
    protected void onStop(){
        if(service!=null) {
            stopService(service);
        }
        super.onStop();
    }

    private void iniToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab=getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);

    }

    private void iniNavigationView(){
        drawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView=(NavigationView) findViewById(R.id.navigation);

        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.menu_home:
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            case R.id.menu_pedido:
                if(!BBDD.tablasCargadas(this)){
                    Utilidades.Alert(this,getResources().getString(R.string.error_tabla));
                }
                else {
                    startActivity(new Intent(this, PedidosActivity.class));
                }
                break;
            case R.id.menu_info:
                startActivity(new Intent(this,InfoActivity.class));
                break;
            case R.id.menu_cuenta:
                startActivity(new Intent(this,AccountActivity.class));
                break;
            case R.id.menu_sync:
                Utilidades.Confirmar(self, getResources().getString(R.string.hacer_sincro), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new SincronizarTask().execute();
                    }
                });
                break;
        }

        return false;
    }



    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(navigationView)){
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
        else{
            finish();
        }
    }


    /*******************SINCRONIZAR***************************/

    private static final Integer SIN_GOOGLE_PLAY=1;
    private static final Integer SIN_RED=2;
    private static final Integer OK=3;
    private static final Integer ERROR=4;

    private class SincronizarTask extends AsyncTask<Void,Void,Integer>{

        private final ProgressDialog progress;

        public SincronizarTask(){
            progress=new ProgressDialog(self);
            progress.setTitle(getResources().getString(R.string.espere));
            progress.setMessage(getResources().getString(R.string.sincronizando));
            progress.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            progress.show();
            if(MyDrive.comprobarGooglePlayServices(self)){
                onCancelled(SIN_GOOGLE_PLAY);
            }
            if(!Utilidades.RedDisponible(self)){
                onCancelled(SIN_RED);
            }
            if(MyDrive.email==null || MyDrive.idCarpeta==null){
                progress.dismiss();
                Utilidades.Error(self,getResources().getString(R.string.agregar_cuenta_drive));
            }
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            try {
                MyDrive.sincronizarArchivos(getApplicationContext());
                return OK;
            } catch (IOException e) {
                e.printStackTrace();
                return ERROR;
            }
        }

        @Override
        protected void onPostExecute(Integer res) {
            progress.dismiss();
            if(res==SIN_GOOGLE_PLAY){
                Utilidades.Alert(self, getResources().getString(R.string.error_gplay));
            }
            else if(res==SIN_RED){
                Utilidades.Alert(self, getResources().getString(R.string.error_red));
            }
            else if(res==ERROR){
                Utilidades.Alert(self, getResources().getString(R.string.sin_acceso_carpeta));
            } else {
                Utilidades.Info(self, getResources().getString(R.string.sincro_completada));
            }
            BBDD.cargarTablas(self);
            cargarDatos();
            super.onPostExecute(res);
        }
    }

    /**********METODOS**********************************************************/

    public void cargarDatos(){
        long c= BBDD.dbCliente.totalClientes();
        num_cliente.setText(c + "");

        long p=BBDD.dbProducto.totalProductos();
        num_prod.setText(p + "");

        List<Venta> list=new ArrayList<Venta>();
        String anyo_ant="";

        Cursor res=BBDD.dbPedido.obtenerEstadisticas();
        if(res!=null) {
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                double ventas = res.getDouble(0);
                String mes = res.getString(1);
                String anyo = res.getString(2);

                if (!anyo.equals(anyo_ant)) {//Nueva Cabecera
                    list.add(new Venta(anyo,"", 0d));
                    anyo_ant = anyo;
                }

                Venta v = new Venta(anyo, mes, ventas);
                list.add(v);
                res.moveToNext();
            }
            res.close();

            MainAdapter adapter = new MainAdapter(this, list);
            listView.setAdapter(adapter);
        }

    }

    private class MainAdapter extends BaseAdapter{

        private Context contex;
        private List<Venta> ventas;
        private LayoutInflater inflater;
        TextView anyo,mes,suma;

        public MainAdapter(Context context,List<Venta> ventas){
            this.contex=context;
            this.ventas=ventas;
            this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return ventas.size();
        }

        @Override
        public Object getItem(int position) {
            return ventas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view=convertView;

            Venta v=ventas.get(position);
            if(v.getMes()==""){
                view=inflater.inflate(R.layout.main_header_item,null);
                anyo=(TextView)view.findViewById(R.id.item_cabecera);
                anyo.setText(v.getAnyo()+"");
            }
            else{
                view=inflater.inflate(R.layout.main_item,null);
                mes=(TextView)view.findViewById(R.id.item_mes);
                suma=(TextView)view.findViewById(R.id.item_venta);
                mes.setText(v.getMes()+"");
                suma.setText(Utilidades.Redondear(v.getVentas())+" Euros");
            }

            return view;
        }
    }

    private class Venta{

        private String anyo,mes;
        private double ventas;

        public Venta(String anyo, String mes, Double ventas){
            this.anyo=anyo;
            this.mes=mes;
            this.ventas=ventas;
        }

        public String getAnyo() {
            return anyo;
        }

        public String getMes() {

            return mes;
        }

        public double getVentas() {
            return ventas;
        }
    }


}
