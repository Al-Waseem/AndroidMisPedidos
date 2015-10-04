package org.example.vicchiam.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import org.example.vicchiam.bbdd.Cliente;
import org.example.vicchiam.bbdd.DBCliente;
import org.example.vicchiam.bbdd.Linea;
import org.example.vicchiam.bbdd.Pedido;
import org.example.vicchiam.mispedidos.DetalleActivity;
import org.example.vicchiam.mispedidos.R;
import org.example.vicchiam.utilidades.BBDD;
import org.example.vicchiam.utilidades.MyDrive;
import org.example.vicchiam.utilidades.PedidoTemp;
import org.example.vicchiam.utilidades.Utilidades;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by vicch on 27/09/2015.
 */
public class PedidoFragment extends Fragment{

    private TextView entrega,codigo,empresa,contacto,email,telefono,direccion;
    private ImageView img_entrega, img_codigo, img_empresa, img_contacto, img_email, img_telefono, img_direccion;
    private Button borrar,guardar,enviar,llamar;
    private Fragment self;

    private long id;
    DetalleActivity padre;


    public static PedidoFragment newInstace(long id){
        PedidoFragment pf=new PedidoFragment();

        Bundle args=new Bundle();
        args.putLong("id", id);
        pf.setArguments(args);
        return pf;
    }

    public PedidoFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self=this;
        Bundle args=getArguments();
        id=args.getLong("id",0);

        padre=(DetalleActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pedido, container, false);


        /*TextView*********************************************************************************/

        codigo=(TextView)view.findViewById(R.id.dp_codigo);
        empresa=(TextView)view.findViewById(R.id.dp_nombre);
        contacto=(TextView)view.findViewById(R.id.dp_contacto);
        email=(TextView)view.findViewById(R.id.dp_email);
        telefono=(TextView)view.findViewById(R.id.dp_telef);
        direccion=(TextView)view.findViewById(R.id.dp_dir);
        entrega=(TextView)view.findViewById(R.id.dp_entrega);


        /*ImageView*********************************************************************************/

        img_codigo=(ImageView)view.findViewById(R.id.dp_img_codigo);
        img_codigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorImageView(img_codigo, DBCliente.CLIENTE_COLS[1]);
            }
        });
        img_empresa=(ImageView)view.findViewById(R.id.dp_img_nombre);
        img_empresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorImageView(img_empresa,DBCliente.CLIENTE_COLS[10]);
            }
        });
        //Es nuevo puede buscar pongo empresa por defecto
        if(id<=0){
            img_empresa.setBackgroundColor(getResources().getColor(R.color.my_accent));
        }
        img_contacto=(ImageView)view.findViewById(R.id.dp_img_contacto);
        img_contacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorImageView(img_contacto,DBCliente.CLIENTE_COLS[3]);
            }
        });
        img_email=(ImageView)view.findViewById(R.id.dp_img_email);
        img_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorImageView(img_email,DBCliente.CLIENTE_COLS[5]);
            }
        });
        img_telefono=(ImageView)view.findViewById(R.id.dp_img_telef);
        img_telefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorImageView(img_telefono,DBCliente.CLIENTE_COLS[4]);
            }
        });
        img_direccion=(ImageView)view.findViewById(R.id.dp_img_dir);
        img_entrega=(ImageView)view.findViewById(R.id.dp_img_entrega);
        img_entrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                final int year = c.get(Calendar.YEAR);
                final int month = c.get(Calendar.MONTH);
                final int day = c.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(self.getContext(), myDateListener, year, month, day).show();
            }
        });


        /*Button************************************************************************************/

        enviar=(Button)view.findViewById(R.id.dp_boton_mail);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().length()>0) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{(email.getText() + "")});
                    i.putExtra(Intent.EXTRA_SUBJECT, "Contact us");
                    i.setType("message/rfc822");
                    startActivity(i);
                }
            }
        });

        llamar=(Button)view.findViewById(R.id.dp_boton_telf);
        llamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(telefono.getText().length()>0) {
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telefono.getText())));
                }
            }
        });

        borrar=(Button)view.findViewById(R.id.dp_boton_borrar);
        if(id==0){
            borrar.setEnabled(false);
        }
        borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilidades.Confirmar(self.getActivity(), getResources().getString(R.string.dp_borrar_pedido), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        borrarPedido();
                    }
                });

            }
        });

        guardar=(Button)view.findViewById(R.id.dp_boton_guardar);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarDatosPedido();
            }
        });



        if(id!=0){
            if(PedidoTemp.pedido.isEntregado()){
                img_entrega.setEnabled(false);
                borrar.setEnabled(false);
                guardar.setEnabled(false);
            }
            mostrarPedido();
        }
        else{
            PedidoTemp.pedido=new Pedido(0);
        }

        return view;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int year, int month, int day) {
            month++;
            entrega.setText(((day<10)?"0"+day:day)+"-"+((month<10)?"0"+month:month)+"-"+year);
        }
    };

    private void colorImageView(ImageView iv,String columna) {
        if (id <= 0) {
            int color1 = getResources().getColor(R.color.my_white);
            int color2 = getResources().getColor(R.color.my_accent);

            img_contacto.setBackgroundColor((img_contacto == iv) ? color2 : color1);
            img_empresa.setBackgroundColor((img_empresa == iv) ? color2 : color1);
            img_codigo.setBackgroundColor((img_codigo == iv) ? color2 : color1);
            img_email.setBackgroundColor((img_email == iv) ? color2 : color1);
            img_telefono.setBackgroundColor((img_telefono == iv) ? color2 : color1);
            img_direccion.setBackgroundColor((img_direccion == iv) ? color2 : color1);

            padre.abrirSearch(columna);
        }
    }

    public void setClientePedido(Cliente c){
        PedidoTemp.pedido.setCliente_cod(c.getCodigo());
        PedidoTemp.pedido.setCliente_nom(c.getNombre());
        PedidoTemp.pedido.setCliente_cif(c.getCif());
        PedidoTemp.pedido.setCliente_mail(c.getMail());
        PedidoTemp.pedido.setCliente_empresa(c.getEmpresa());
        PedidoTemp.pedido.setCliente_tel(c.getTelefono());
        PedidoTemp.pedido.setCliente_calle(c.getCalle());
        PedidoTemp. pedido.setCliente_pob(c.getPoblacion());
        PedidoTemp.pedido.setCliente_cp(c.getCp());
        PedidoTemp.pedido.setCliente_pais(c.getPais());

        this.mostrarCliente(c);
    }

    private void mostrarCliente(Cliente c){
        codigo.setText(c.getCodigo());
        empresa.setText(c.getEmpresa());
        contacto.setText(c.getNombre());
        email.setText(c.getMail());
        telefono.setText(c.getTelefono());
        direccion.setText(c.getDireccion());
    }

    private void mostrarPedido(){
        codigo.setText(PedidoTemp.pedido.getCliente_cod());
        empresa.setText(PedidoTemp.pedido.getCliente_empresa());
        contacto.setText(PedidoTemp.pedido.getCliente_nom());
        email.setText(PedidoTemp.pedido.getCliente_mail());
        telefono.setText(PedidoTemp.pedido.getCliente_tel());
        direccion.setText(PedidoTemp.pedido.getDireccion());
        entrega.setText(Utilidades.DateToSmallString(PedidoTemp.pedido.getFecha_entrega()));
    }

    //Actualiza los datos propios del pedido
    private void actualizarDatosPedido(){

        //Compruba que haya linea con id<>0
        boolean encontrado=false;
        for(Linea l:PedidoTemp.pedido.getLineas()){
            Log.e("LINEA",l.toString());
            if(l.getId()!=0){
                encontrado=true;
            }
        }
        if(!encontrado){
            Utilidades.Alert(getActivity(),getResources().getString(R.string.error_sin_linea));
            return;
        }

        if(PedidoTemp.pedido.getCliente_cod()==null){
            Utilidades.Alert(getActivity(),getResources().getString(R.string.error_cliente));
            return;
        }

        String fecha_entrega=entrega.getText().toString();
        if(fecha_entrega.length()==0){
            Utilidades.Alert(getActivity(),getResources().getString(R.string.error_fecha));
            return;
        }

        Date fe=Utilidades.StringToSmallDate(fecha_entrega);
        PedidoTemp.pedido.setFecha_creacion(new Date());
        PedidoTemp.pedido.setFecha_entrega(fe);
        PedidoTemp.pedido.setCuenta_creacion(MyDrive.email);

        actualizarPedidoBaseDatos();

    }

    private void actualizarPedidoBaseDatos(){
        Pedido aux;
        if(PedidoTemp.pedido.getId()>0){//Es una actualizacion
            aux=BBDD.dbPedido.actualizarPedido(PedidoTemp.pedido);
        }
        else{//Es nuevo
            aux=BBDD.dbPedido.insertarPedido(PedidoTemp.pedido);

            //Si el pedido es nuevo todas las lineas son para insertar, si no ya se han gurdado las modifcaciones
            for(Linea linea:PedidoTemp.pedido.getLineas()){
                linea.setPedido(aux);
                BBDD.dbPedido.insertarLinea(linea);
            }
        }

        if(aux==null){
            Utilidades.Alert(getActivity(), getResources().getString(R.string.error_actualizar));
            return;
        }

        this.getActivity().setResult(Activity.RESULT_OK);
        this.getActivity().finish();
    }

    private void borrarPedido(){
        BBDD.dbPedido.borrarPedido(PedidoTemp.pedido.getId());
        this.getActivity().setResult(Activity.RESULT_OK);
        this.getActivity().finish();
    }
}
