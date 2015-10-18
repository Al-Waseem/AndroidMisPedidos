package org.example.vicchiam.mispedidos;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import org.example.vicchiam.utilidades.MyDrive;
import org.example.vicchiam.utilidades.Utilidades;

import java.util.Arrays;

public class AccountActivity extends AppCompatActivity{

    private static final int SOLICITUD_SELECCION_CUENTA=1;
    TextView textAccount;
    Activity self;
    LinearLayout contenedor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        self=this;

        textAccount=(TextView)findViewById(R.id.nombre_cuenta);
        contenedor=(LinearLayout)findViewById(R.id.contenedor);

        iniToolbar();
        iniFloatButton();

        comprobarCuenta();
    }

    private void iniToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab=getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);

    }

    private void iniFloatButton(){
        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.fab_acc);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String res = getResources().getString(R.string.quitar_acceso_cuenta);
                String msj = String.format(res, MyDrive.email);
                Utilidades.Confirmar(self, msj, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utilidades.borrarPreferencias(self);
                        comprobarCuenta();
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(final int requestCode,final int resultCode,final Intent data){
        switch (requestCode){
            //Obtenemos acceso a la cuenta
            case SOLICITUD_SELECCION_CUENTA:{
                if(resultCode==RESULT_OK && data!=null && data.getExtras()!=null &&
                        (MyDrive.email=data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME))!=null){
                    MyDrive.credencial.setSelectedAccountName(MyDrive.email);
                    MyDrive.servicio=new Drive.Builder(AndroidHttp.newCompatibleTransport(),new GsonFactory(),MyDrive.credencial).build();
                    Utilidades.guardarPreferencias(this);
                    comprobarCuenta();
                }
                else{
                    String res=getResources().getString(R.string.error_conectar_cuenta);
                    String msj=String.format(res,getResources().getString(R.string.sin_nombre_cuenta));
                    Utilidades.Error(self, msj);
                    setImage(false);
                }
            }
        }
    }


    /***************LOGICA*******************************************************************/

    private void comprobarCuenta(){
        Utilidades.cargarPreferencias(this);
        String text=getResources().getString(R.string.nombre_cuenta);

        if(MyDrive.credencial==null){
            MyDrive.credencial= GoogleAccountCredential.usingOAuth2(self, Arrays.asList(DriveScopes.DRIVE));
        }

        if(MyDrive.email==null){
            setImage(false);
            text+=" "+getResources().getString(R.string.sin_nombre_cuenta);
            textAccount.setText(text);
            startActivityForResult(MyDrive.credencial.newChooseAccountIntent(), SOLICITUD_SELECCION_CUENTA);
        }
        else{
            MyDrive.credencial.setSelectedAccountName(MyDrive.email);

            text+=" "+MyDrive.email;
            textAccount.setText(text);

            if(MyDrive.servicio==null) {
                MyDrive.servicio = new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), MyDrive.credencial).build();
            }

            if(MyDrive.idCarpeta==null){
                setImage(false);
                new ComprobarCarpetaTask().execute();
            }
            else{
                setImage(true);
            }

        }
    }

    private void setImage(boolean ok){
        contenedor.removeAllViews();
        int resource;
        if(ok)
            resource=R.drawable.drive;
        else
            resource=R.drawable.drive_bn;
        ImageView i = new ImageView(getApplicationContext());
        i.setMaxWidth(45);
        i.setMaxHeight(45);
        i.setScaleType(ImageView.ScaleType.FIT_CENTER);
        i.setImageResource(resource);
        contenedor.addView(i);
    }


    /**************HILOS****************************************************************************/

    private static final Integer SIN_GOOGLE_PLAY=1;
    private static final Integer SIN_RED=2;
    private static final Integer OK=3;
    private static final Integer ERROR=4;


    private class ComprobarCarpetaTask extends AsyncTask<Void,Void,Integer>{


        final ProgressDialog progress;

        public ComprobarCarpetaTask(){
            progress=new ProgressDialog(self);
            progress.setTitle(getResources().getString(R.string.espere));
            progress.setMessage(getResources().getString(R.string.comprobando_cuenta));
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
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            if(MyDrive.comprobarCarpeta(self))
                return OK;
            else
                return ERROR;
        }

        @Override
        protected void onPostExecute(Integer res) {
            super.onPostExecute(res);
            progress.dismiss();
            if(res==SIN_GOOGLE_PLAY){
                Utilidades.Alert(self, getResources().getString(R.string.error_gplay));
            }
            else if(res==SIN_RED){
                Utilidades.Alert(self, getResources().getString(R.string.error_red));
            }
            else if(res==ERROR){
                Utilidades.Alert(self, getResources().getString(R.string.sin_acceso_carpeta));
            }
            else{
                Utilidades.Info(self,getResources().getString(R.string.cuenta_agregada));
                comprobarCuenta();
            }
        }

    }


}
