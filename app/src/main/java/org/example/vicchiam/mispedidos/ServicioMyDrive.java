package org.example.vicchiam.mispedidos;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.ChildReference;
import com.google.api.services.drive.model.File;

import org.example.vicchiam.utilidades.MyDrive;
import org.example.vicchiam.utilidades.Utilidades;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vicch on 26/09/2015.
 */
public class ServicioMyDrive extends Service {

    private static Thread t;
    private Service self;
    private boolean running;

    @Override
    public void onCreate(){
        self=this;
    }

    @Override
    public int onStartCommand(Intent i, int flags, int startId){
        Log.d("SERVICE", "Service running");

        Utilidades.cargarPreferencias(self);
        running=true;

        if(t==null || !t.isAlive()) {
            t = new Thread(new Run());
            t.start();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("SERVICE", "Service stop");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class Run implements Runnable {
        @Override
        public void run() {
            while (running) {
                synchronized (this) {
                    try {
                        if(Utilidades.RedDisponible(self) && MyDrive.email!=null && MyDrive.idCarpeta!=null && MyDrive.servicio!=null) {

                            SharedPreferences pref=self.getSharedPreferences("AppPedidos", Context.MODE_PRIVATE);
                            Map<String,Long> tiempos=new HashMap<String,Long>();

                            for(String s:MyDrive.FICHEROS_VALIDOS){
                                Long value=pref.getLong("servicio_" + s, 0);
                                tiempos.put(s,value);
                            }

                            List<String> idDriveFiles=new ArrayList<String>();

                            Drive.Children.List request=MyDrive.servicio.children().list(MyDrive.idCarpeta);
                            request.setQ("title='" + MyDrive.FICHEROS_VALIDOS.get(0) + "' or title='" + MyDrive.FICHEROS_VALIDOS.get(1) + "'");

                            do{
                                ChildList children=request.execute();
                                for(ChildReference child:children.getItems()){
                                    idDriveFiles.add(child.getId());
                                }

                                request.setPageToken(children.getNextPageToken());
                            }while (request.getPageToken()!=null && request.getPageToken().length()>0);



                            for(String id:idDriveFiles) {
                                File file = MyDrive.servicio.files().get(id).execute();
                                String key=file.getTitle();
                                Long newValue=file.getModifiedDate().getValue();
                                if(tiempos.containsKey(key)) {
                                    Long oldValue = tiempos.get(key);
                                    if(oldValue<(newValue-1000)){
                                        notificar();
                                        SharedPreferences.Editor editor=pref.edit();
                                        editor.putLong("servicio_"+key,newValue);
                                        editor.commit();
                                    }
                                }
                            }

                        }
                        this.wait(5*60 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException ioe){
                        ioe.printStackTrace();
                    }
                }
            }
        }
    }

    private void notificar(){
        NotificationCompat.Builder builder=new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.drawable.ic_cloud_download_black_24dp);
        builder.setContentTitle(getResources().getString(R.string.app_name));
        builder.setContentText(getResources().getString(R.string.nuevo_recurso));
        NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.notify(1, builder.build());
    }

}
