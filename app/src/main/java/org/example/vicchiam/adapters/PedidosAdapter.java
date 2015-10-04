package org.example.vicchiam.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.example.vicchiam.bbdd.Pedido;
import org.example.vicchiam.mispedidos.R;
import org.example.vicchiam.utilidades.Utilidades;

import java.util.List;

/**
 * Created by vicch on 26/09/2015.
 */
public class PedidosAdapter extends RecyclerView.Adapter<PedidosAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<Pedido> pedidos;
    private View.OnClickListener onClickListener;

    public PedidosAdapter(Context contexto, List<Pedido> pedidos){
        inflater=(LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.pedidos=pedidos;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView entregado;
        public TextView empresa;
        public TextView precio;
        public TextView entrega;

        public ViewHolder(View itemView) {
            super(itemView);
            entregado = (ImageView) itemView.findViewById(R.id.pedido_item_img);
            empresa = (TextView) itemView.findViewById(R.id.pedido_item_empresa);
            precio = (TextView) itemView.findViewById(R.id.pedido_item_total);
            entrega = (TextView) itemView.findViewById(R.id.pedido_item_entrega);
        }
    }

    public void setPedidos(List<Pedido> pedidos){
        this.pedidos=pedidos;
        this.notifyDataSetChanged();
    }

    public Pedido getPedido(int position){
        return pedidos.get(position);
    }

    public void setOnItemClickListener(View.OnClickListener onClickListener){
        this.onClickListener=onClickListener;
    }

    // Creamos el ViewHolder con las vista de un elemento sin personalizar
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflamos la vista desde el xml
        View v = inflater.inflate(R.layout.pedido_item, null);
        v.setOnClickListener(onClickListener);
        return new ViewHolder(v);
    }

    // Usando como base el ViewHolder y lo personalizamos
    @Override
    public void onBindViewHolder(ViewHolder holder, int posicion) {
        final Pedido p=pedidos.get(posicion);

        holder.empresa.setText(p.getCliente_empresa());
        holder.precio.setText(Utilidades.Redondear(p.getPrecioImpuestos())+" EUR");
        holder.entrega.setText(Utilidades.DateToSmallString(p.getFecha_entrega()));

    }

    // Indicamos el número de elementos de la lista
    @Override public int getItemCount() {
        return pedidos.size();
    }
}
