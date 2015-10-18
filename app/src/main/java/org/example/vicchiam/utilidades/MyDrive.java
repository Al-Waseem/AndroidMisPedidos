package org.example.vicchiam.utilidades;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.ChildReference;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vicch on 25/09/2015.
 */
public final class MyDrive {

    public static Drive servicio;
    public static GoogleAccountCredential credencial=null;
    public static String email=null;
    public static String idCarpeta=null;
    public static final String DRIVE_FOLDER="AppPedidos";
    public static List<String> FICHEROS_VALIDOS=new ArrayList<String>(){{
        add("clientes.csv");
        add("productos.csv");
        add("conf.json");
    }};

    //Guardar el mail de envío
    public static String email_envio=null;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST=9000;

    public static boolean comprobarGooglePlayServices(final Activity activity){
        int result= GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if(result!= ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(result)){
                GooglePlayServicesUtil.getErrorDialog(result, activity, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    public static boolean comprobarCarpeta(Activity activity){
        //Buscamos la carpeta de la aplicacion
        List<File> lista=new ArrayList<File>();
        Drive.Files.List request=null;
        try {
            request = MyDrive.servicio.files().list();
            request.setQ("title='" + MyDrive.DRIVE_FOLDER + "' and mimeType='application/vnd.google-apps.folder'");
            FileList files = request.execute();
            Log.e("REQUEST", request.toString());
            lista.addAll(files.getItems());
            request.setPageToken(files.getNextPageToken());
        } catch (UserRecoverableAuthIOException e) {
            activity.startActivityForResult(e.getIntent(), 1);
        }catch (IOException e){
            e.printStackTrace();
            request.setPageToken(null);
        }

        if(lista.size()>0 && lista.get(0).getTitle().equals(MyDrive.DRIVE_FOLDER)){
            MyDrive.idCarpeta=lista.get(0).getId();
            Utilidades.guardarPreferencias(activity);
            return true;
        }
        else{
            return false;
        }
    }

    public static void sincronizarArchivos(Context context) throws IOException{
        List<String> list=new ArrayList<String>();

        Drive.Children.List request=MyDrive.servicio.children().list(MyDrive.idCarpeta);
        request.setQ("title='" + MyDrive.FICHEROS_VALIDOS.get(0) + "' or title='" + MyDrive.FICHEROS_VALIDOS.get(1) + "' or title='"+MyDrive.FICHEROS_VALIDOS.get(2)+"'");
        //request.setQ("title='" + MyDrive.FICHEROS_VALIDOS.get(2) + "'");

        do{
            ChildList children=request.execute();
            for(ChildReference child:children.getItems()){
                list.add(child.getId());
            }

            request.setPageToken(children.getNextPageToken());
        }while (request.getPageToken()!=null && request.getPageToken().length()>0);

        for(String id:list){
            File file = MyDrive.servicio.files().get(id).execute();
            InputStream content=MyDrive.downloadFile(file);

            List<String> lineas=new ArrayList<String>();
            String str="";

            //Evitar cabecera
            int cont=0;

            if(content!=null){
                BufferedReader reader = new BufferedReader(new InputStreamReader(content, "UTF-8"));
                while ((str = reader.readLine()) != null) {

                    if (file.getTitle().equals(MyDrive.FICHEROS_VALIDOS.get(2))) {//fichero conf.json
                        //El fichero solo tiene una linea, solo pasará aquí
                        if (file.getTitle().equals("conf.json")) {
                            try {
                                JSONObject json = new JSONObject(str);
                                MyDrive.email_envio = json.getString("mail");
                                Utilidades.guardarPreferencias(context);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        if (cont > 0) {
                            Log.d("LINEAS", str);
                            lineas.add(str);
                            if (lineas.size() == 100) {

                                //Es la primera introduccion de datos (cont==99) así que borro las tablas
                                if (cont == 100 && file.getTitle().equals(BBDD.dbCliente.FICHERO_ASOCIADO)) {
                                    BBDD.dbCliente.truncate();
                                }
                                if (cont == 100 && file.getTitle().equals(BBDD.dbProducto.FICHERO_ASOCIADO)) {
                                    BBDD.dbProducto.truncate();
                                }

                                agregarTabla(file.getTitle(), lineas);
                                lineas.clear();
                            }
                        }
                        cont++;
                    }
                    if (cont < 100) {
                        //Es la primera introduccion de datos (cont<100) así que borro las tablas
                        if (file.getTitle().equals(BBDD.dbCliente.FICHERO_ASOCIADO)) {
                            BBDD.dbCliente.truncate();
                            agregarTabla(file.getTitle(), lineas);
                        }
                        if (file.getTitle().equals(BBDD.dbProducto.FICHERO_ASOCIADO)) {
                            BBDD.dbProducto.truncate();
                            agregarTabla(file.getTitle(), lineas);
                        }
                    }
                }
            }
        }
    }

    private static void agregarTabla(String nombre, List<String> lineas){
        if(nombre.equals(BBDD.dbCliente.FICHERO_ASOCIADO)){
            for(String linea: lineas){
                BBDD.dbCliente.insertarCliente(linea);
            }
        }
        if(nombre.equals(BBDD.dbProducto.FICHERO_ASOCIADO)){
            for(String linea: lineas){
                BBDD.dbProducto.insertarProducto(linea);
            }
        }
    }

    public static InputStream downloadFile(File file) throws IOException {
        if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0) {
            HttpResponse resp =servicio.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl())).execute();
            return resp.getContent();
        } else {
            return null;
        }
    }

}
