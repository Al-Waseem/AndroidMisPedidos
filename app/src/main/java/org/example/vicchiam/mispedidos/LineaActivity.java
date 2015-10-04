package org.example.vicchiam.mispedidos;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.example.vicchiam.adapters.SearchSuggestionAdapter;
import org.example.vicchiam.bbdd.DBProducto;
import org.example.vicchiam.bbdd.Linea;
import org.example.vicchiam.bbdd.Producto;
import org.example.vicchiam.utilidades.BBDD;
import org.example.vicchiam.utilidades.PedidoTemp;
import org.example.vicchiam.utilidades.Utilidades;

public class LineaActivity extends AppCompatActivity implements TextWatcher{

    private SearchView searchView;
    private SearchSuggestionAdapter searchAdapter;

    private TextView codigo,nombre,familia,precio,descripcion,und,total;
    private ImageView img_codigo, img_producto, img_familia;
    private EditText cantidad,descuento,iva;
    private Button borrar,guardar;

    private long id;
    private Linea linea;
    private String campo_busqueda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linea);

        id=getIntent().getExtras().getLong("id",0);

        iniToolbar();

        /*TextView***********************************************/
        codigo=(TextView)findViewById(R.id.dl_codigo);

        nombre=(TextView)findViewById(R.id.dl_nombre);
        familia=(TextView)findViewById(R.id.dl_familia);
        precio=(TextView)findViewById(R.id.dl_precio);
        descripcion=(TextView)findViewById(R.id.dl_desc);
        und=(TextView)findViewById(R.id.dl_und);
        total=(TextView)findViewById(R.id.dl_total);

        cantidad=(EditText)findViewById(R.id.dl_cantidad);
        cantidad.addTextChangedListener(this);
        descuento=(EditText)findViewById(R.id.dl_descuento);
        descuento.addTextChangedListener(this);
        iva=(EditText)findViewById(R.id.dl_iva);
        iva.addTextChangedListener(this);


        /*ImageView*********************************************/
        img_codigo=(ImageView)findViewById(R.id.dl_img_codigo);
        img_codigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorImageView(img_codigo, DBProducto.PRODUCTO_COLS[1]);
            }
        });
        img_producto=(ImageView)findViewById(R.id.dl_img_nombre);
        img_producto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorImageView(img_producto,DBProducto.PRODUCTO_COLS[2]);
            }
        });
        img_familia=(ImageView)findViewById(R.id.dl_img_familia);
        img_familia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorImageView(img_familia,DBProducto.PRODUCTO_COLS[3]);
            }
        });


        /*Button************************************************/
        borrar=(Button)findViewById(R.id.dl_boton_borrar);
        if(id==0){
            borrar.setEnabled(false);
        }
        borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrarLinea();
            }
        });

        guardar=(Button)findViewById(R.id.dl_boton_guardar);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizaDatosLinea();
            }
        });

        //Pongo el foco para q lo pierda edittext
        LinearLayout ll=(LinearLayout)findViewById(R.id.dl_focus);
        ll.requestFocus();


        if(id!=0) {
            if(PedidoTemp.pedido.isEntregado()){
                borrar.setEnabled(false);
                guardar.setEnabled(false);
            }
            linea = PedidoTemp.pedido.getLinea(id);
            mostrarLinea();
        }
        else{
            linea=new Linea(0);
        }

        if(id<=0) {
            //Linea nueva por defecto nombre
            campo_busqueda = DBProducto.PRODUCTO_COLS[2];
            img_producto.setBackgroundColor(getResources().getColor(R.color.my_accent));
        }
        recalcular();
    }

    private void iniToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar_prod);
        setSupportActionBar(toolbar);
        final ActionBar ab=getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**********SEARCH********************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(id<=0) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_linea, menu);
            searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
            iniSearchView(searchView);
        }
        return true;
    }

    private void iniSearchView(final SearchView searchView){

        //Hint
        searchView.setQueryHint(getResources().getString(R.string.dl_search_hint));

        //text color
        AutoCompleteTextView searchText = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchText.setTextColor(ContextCompat.getColor(this, R.color.my_icons));
        searchText.setHintTextColor(ContextCompat.getColor(this, R.color.my_icons));

        //icons
        ImageView searchIcon = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        searchIcon.setImageResource(R.drawable.ic_search_white_24dp);
        ImageView searchMagIcon = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        searchMagIcon.setImageResource(R.drawable.ic_search_white_24dp);
        ImageView searchClose = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        searchClose.setImageResource(R.drawable.ic_close_white_24dp);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
               return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 3) {
                    updateSearchSuggestion(newText);
                }
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor=(Cursor)searchAdapter.getItem(position);
                long id=cursor.getLong(cursor.getColumnIndex("_id"));
                Producto p=BBDD.dbProducto.obtenerProductoId(id);
                mostrarProducto(p);
                setProductoLinea(p);
                searchView.clearFocus();
                recalcular();
                return true;
            }
        });
    }

    private void updateSearchSuggestion(String query){

        String campo2= DBProducto.PRODUCTO_COLS[2];//Por defecto nombre producto
        if(campo_busqueda.equals(campo2)) {
            campo2 = DBProducto.PRODUCTO_COLS[1];//Segundo campo es codigo
        }

        Cursor cursor = BBDD.dbProducto.obtenerProductosAutocompletar(query,campo_busqueda,campo2);

        searchAdapter = new SearchSuggestionAdapter(this, cursor, this.campo_busqueda, campo2);
        searchView.setSuggestionsAdapter(searchAdapter);
    }

    /************METODOS********************************************/

    private void colorImageView(ImageView iv,String columna) {
        if (id <= 0) {
            int color1 = getResources().getColor(R.color.my_white);
            int color2 = getResources().getColor(R.color.my_accent);

            img_codigo.setBackgroundColor((img_codigo == iv) ? color2 : color1);
            img_producto.setBackgroundColor((img_producto == iv) ? color2 : color1);
            img_familia.setBackgroundColor((img_familia == iv) ? color2 : color1);

            //Abro el buscador
            if(searchView.isIconified()) {
                searchView.setIconified(false);
                searchView.requestFocusFromTouch();
            }

            campo_busqueda=columna;
        }
    }

    private void mostrarProducto(Producto p){
        codigo.setText(p.getCodigo());
        nombre.setText(p.getNombre());
        familia.setText(p.getFamilia());
        und.setText(p.getUnidad());
        descripcion.setText(p.getDescripcion());
        precio.setText(Utilidades.Redondear(p.getPrecio()));
    }

    private void mostrarLinea(){
        if(linea.getProducto_cod()!=null) {
            codigo.setText(linea.getProducto_cod());
            nombre.setText(linea.getProducto_nom());
            familia.setText(linea.getProducto_fam());
            und.setText(linea.getProducto_und());
            descripcion.setText(linea.getProducto_desc());
            precio.setText(Utilidades.Redondear(linea.getProducto_precio()));
            cantidad.setText(linea.getCantidad() + "");
            descuento.setText(Utilidades.Redondear(linea.getDescuento() * 100) + "");
            iva.setText(Utilidades.Redondear(linea.getIva() * 100) + "");
        }
    }

    private void setProductoLinea(Producto p){
        linea.setProducto_cod(p.getCodigo());
        linea.setProducto_nom(p.getNombre());
        linea.setProducto_fam(p.getFamilia());
        linea.setProducto_und(p.getUnidad());
        linea.setProducto_precio(p.getPrecio());
        linea.setProducto_desc(p.getDescripcion());
    }

    private void actualizaDatosLinea(){
        if(linea.getProducto_cod()==null){
            Utilidades.Alert(this,getResources().getString(R.string.error_producto));
            return;
        }

        try {
            float c = Float.parseFloat(cantidad.getText().toString());
            float d = Float.parseFloat(descuento.getText().toString());
            float i = Float.parseFloat(iva.getText().toString());
            if(c<=0){
                Utilidades.Alert(this,getResources().getString(R.string.error_cantidad));
                return;
            }

            linea.setCantidad(c);
            linea.setDescuento(d / 100);
            linea.setIva(i / 100);

            //No esta aún creada
            if(linea.getId()==0){
                linea.setId(PedidoTemp.getIDTemp());
                PedidoTemp.pedido.setLinea(linea);
            }

            if(PedidoTemp.pedido.getId()>0){//Pedido existe en la BBDD
                linea.setPedido(PedidoTemp.pedido);

                if(linea.getId()<0){//Linea nueva
                    Linea aux=BBDD.dbPedido.insertarLinea(linea);
                    linea.setId(aux.getId());
                }
                else{
                    BBDD.dbPedido.actualizarLinea(linea);
                }
            }

            setResult(Activity.RESULT_OK);
            finish();
        }
        catch (NumberFormatException e){
            Utilidades.Alert(this, getResources().getString(R.string.error_linea));
            e.printStackTrace();
        }
    }

    private void borrarLinea(){
        Utilidades.Confirmar(this, getResources().getString(R.string.dl_borrar_linea), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(PedidoTemp.pedido.getId()>0 && linea.getId()>0){//La linea existe en la BBDD
                    BBDD.dbPedido.borrarLinea(linea);
                }
                PedidoTemp.pedido.removeLinea(linea.getId());

                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }

    private void recalcular(){
        if(cantidad.getText().length()>0 && descuento.getText().length()>0 && iva.getText().length()>0) {
            if (cantidad.getText().length() > 0 && descuento.getText().length() > 0 && iva.getText().length() > 0 && precio.getText().length()>0) {
                try {

                    double c = Double.parseDouble(cantidad.getText().toString());
                    double d = Double.parseDouble(descuento.getText().toString());
                    double i = Double.parseDouble(iva.getText().toString());

                    String aux = precio.getText().toString().replace("Euros", "").replace(".","").replace(",", ".");
                    double pu = Double.parseDouble(aux);

                    pu = pu * c;
                    d = (d / 100) * pu;
                    i = (i / 100) + 1;

                    double res = (pu - d) * i;

                    total.setText(Utilidades.Redondear(res));

                }catch(NumberFormatException nfe){
                    nfe.printStackTrace();
                }
            }
        }
    }


    /************************TEXTCHANGE**********************************/
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(s.length()>0) {
            recalcular();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
