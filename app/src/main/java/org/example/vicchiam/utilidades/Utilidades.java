package org.example.vicchiam.utilidades;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v7.app.AlertDialog;

import org.example.vicchiam.mispedidos.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vicch on 25/09/2015.
 */
public final class Utilidades {

    /***********PREFERENCIAS*******************************/

    public static void cargarPreferencias(Context contex){
        SharedPreferences pref=contex.getSharedPreferences("AppPedidos", Context.MODE_PRIVATE);
        MyDrive.email=pref.getString("email",null);
        MyDrive.idCarpeta=pref.getString("idCarpeta", null);
        MyDrive.email_envio=pref.getString("emailEnvio", null);
    }

    public static void guardarPreferencias(Context contex){
        SharedPreferences pref=contex.getSharedPreferences("AppPedidos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();
        if(MyDrive.email!=null){
            editor.putString("email",MyDrive.email);
        }
        if(MyDrive.idCarpeta!=null){
            editor.putString("idCarpeta", MyDrive.idCarpeta);
        }
        if(MyDrive.email_envio!=null){
            editor.putString("emailEnvio", MyDrive.email_envio);
        }
        editor.commit();
    }

    public static void borrarPreferencias(Context contex){
        SharedPreferences pref=contex.getSharedPreferences("AppPedidos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();
        editor.putString("email",null);
        editor.putString("idCarpeta", null);
        editor.commit();
    }

    /****************RED************************************************/

    public static boolean RedDisponible(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /*******************DIALOGS******************************************/

    public static void Info(Activity activity, String msj){
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        builder .setMessage(msj)
                .setTitle(activity.getResources().getString(R.string.info))
                .setIcon(R.drawable.ic_info_black_24dp)
                .setNeutralButton(activity.getResources().getString(R.string.dialog_btn_neutral), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    public static void Alert(Activity activity, String msj){
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        builder .setMessage(msj)
                .setTitle(activity.getResources().getString(R.string.alerta))
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setNeutralButton(activity.getResources().getString(R.string.dialog_btn_neutral), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    public static void Error(final Activity activity, String msj){
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        builder .setMessage(msj)
                .setTitle(activity.getResources().getString(R.string.error))
                .setIcon(R.drawable.ic_error_black_24dp)
                .setNeutralButton(activity.getResources().getString(R.string.dialog_btn_neutral), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        activity.finish();
                    }
                });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    public static void Confirmar(final Activity activity, String msj, DialogInterface.OnClickListener onClickListener){
        final AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        builder .setMessage(msj)
                .setTitle(activity.getResources().getString(R.string.confirm))
                .setIcon(R.drawable.ic_help_black_24dp)
                .setPositiveButton(activity.getResources().getString(R.string.dialog_btn_yes), onClickListener)
                .setNegativeButton(activity.getResources().getString(R.string.dialog_btn_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    /************************FECHAS*************************/
    public static Date StringToDate(String date){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date StringToSmallDate(String date){
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String DateToString(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }

    public static String DateToSmallString(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        return formatter.format(date);
    }

    /**************NUMERO*******************************/

     public static String Redondear(double d){
         return String.format("%1$,.2f", d);
     }


    /**********************FICHEROS*************************************/

    public static String guardarFichero(String cuerpo){
        String path= Environment.getExternalStorageDirectory().toString();
        File f=new File(path+"/data.csv");
        if(f.exists())
            f.delete();
        try{
            FileOutputStream out=new FileOutputStream(f);
            out.write(cuerpo.getBytes());
            out.close();
            return f.getAbsolutePath();
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
