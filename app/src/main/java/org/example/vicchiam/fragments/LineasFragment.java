package org.example.vicchiam.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.example.vicchiam.bbdd.Linea;
import org.example.vicchiam.mispedidos.DetalleActivity;
import org.example.vicchiam.mispedidos.LineaActivity;
import org.example.vicchiam.mispedidos.R;
import org.example.vicchiam.utilidades.PedidoTemp;
import org.example.vicchiam.utilidades.Utilidades;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vicch on 27/09/2015.
 */
public class LineasFragment extends Fragment {

    private ListView listadoLineas;
    private LineaAdpater lineaAdapter;
    private Fragment self;

    private long id;

    public static LineasFragment newInstance(long id){
        LineasFragment lf=new LineasFragment();
        Bundle args=new Bundle();
        args.putLong("id", id);
        lf.setArguments(args);
        return lf;
    }

    public LineasFragment(){
        self=this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args=getArguments();
        id=args.getLong("id",0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lineas, container, false);

        listadoLineas=(ListView)view.findViewById(R.id.lineas_listview);
        listadoLineas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Linea l = lineaAdapter.getLinea(position);
                Intent i = new Intent(self.getContext(), LineaActivity.class);
                i.putExtra("id", l.getId());
                startActivityForResult(i, DetalleActivity.MODIFICAR_LINEA);
            }
        });

        //Evitar la memoria***********************/
        List<Linea> lineas=new ArrayList<Linea>();
        for(Linea l: PedidoTemp.pedido.getLineas()){
            if(l.getId()>0){
                lineas.add(l);
            }
        }
        PedidoTemp.pedido.setLineas(lineas);
        //**************************************/

        lineaAdapter=new LineaAdpater(view.getContext(),lineas);
        listadoLineas.setAdapter(lineaAdapter);

        if(!PedidoTemp.pedido.isEntregado()) {//Si esta entregado no se puede agregar lineas
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                iniFloatButton(view);
            }
        }

        return view;
    }

    public void actualizarVista(){
        lineaAdapter.setLineas(PedidoTemp.pedido.getLineas());
        lineaAdapter.notifyDataSetChanged();
        listadoLineas.requestLayout();
    }

    private void iniFloatButton(View v) {
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab_linea);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(self.getContext(), LineaActivity.class);
                i.putExtra("id", 0L);
                startActivityForResult(i, DetalleActivity.NUEVA_LINEA);
            }
        });
    }

    public LineaAdpater getLineaAdapter(){
        return this.lineaAdapter;
    }

    private class LineaAdpater extends BaseAdapter{

        List<Linea> lineas;
        LayoutInflater inflater;

        public LineaAdpater(Context context,List<Linea> lineas){
            this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.lineas=new ArrayList<Linea>();
            //Evitar aparezcan marcado para borrar
            for(Linea l:lineas){
                if(l.getCantidad()>0){
                    this.lineas.add(l);
                }
            }
        }

        @Override
        public int getCount() {
            return lineas.size();
        }

        @Override
        public Object getItem(int position) {
            return lineas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView=inflater.inflate(R.layout.linea_item,null);

            TextView producto=(TextView)convertView.findViewById(R.id.linea_item_producto);
            TextView cantidad=(TextView)convertView.findViewById(R.id.linea_item_cantidad);
            TextView descuento=(TextView)convertView.findViewById(R.id.linea_item_desc);
            TextView precio=(TextView)convertView.findViewById(R.id.linea_item_total);

            Linea l=lineas.get(position);
            producto.setText(l.getProducto_nom());
            cantidad.setText(l.getCantidad()+" "+l.getProducto_und());
            descuento.setText(Utilidades.Redondear(l.getDescuento()*100)+"% Desc.");
            precio.setText(Utilidades.Redondear(l.getPrecioImpuestos()));

            return convertView;
        }

        public Linea getLinea(int posicion){
            return lineas.get(posicion);
        }

        public void setLineas(List<Linea> lineas){
            this.lineas=new ArrayList<Linea>();
            //Evitar aparezcan marcado para borrar
            for(Linea l:lineas){
                if(l.getCantidad()>0){
                    this.lineas.add(l);
                }
            }
        }
    }

}
