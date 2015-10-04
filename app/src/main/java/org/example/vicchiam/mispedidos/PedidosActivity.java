package org.example.vicchiam.mispedidos;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import org.example.vicchiam.adapters.PedidosAdapter;
import org.example.vicchiam.adapters.SearchSuggestionAdapter;
import org.example.vicchiam.bbdd.DBPedido;
import org.example.vicchiam.bbdd.Pedido;
import org.example.vicchiam.utilidades.BBDD;

import java.util.List;


public class PedidosActivity extends AppCompatActivity {

    public static final int NUEVO_PEDIDO=1;
    public static final int MODIFICAR_PEDIDO=2;

    private SearchSuggestionAdapter searchAdapter;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private List<Pedido> pedidos;
    private PedidosAdapter pedidosAdapter;
    private Activity self;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);
        self=this;

        pedidos=BBDD.dbPedido.obtenerPedidos();

        iniToolbar();
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            iniFloatButton();
        }
        iniRecyclerView();
    }

    private void iniToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab=getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void iniFloatButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(self, DetalleActivity.class);
                i.putExtra("id", 0L);
                startActivityForResult(i, NUEVO_PEDIDO);
            }
        });
    }

    private void iniRecyclerView(){
        recyclerView=(RecyclerView)findViewById(R.id.pedidos_rv);
        LinearLayoutManager llm=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        iniAdapter();
    }

    private void iniAdapter(){
        pedidosAdapter=new PedidosAdapter(this,pedidos);
        pedidosAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos=recyclerView.getChildAdapterPosition(v);
                Intent i = new Intent(self, DetalleActivity.class);
                i.putExtra("id", pedidosAdapter.getPedido(pos).getId());
                startActivityForResult(i, MODIFICAR_PEDIDO);

            }
        });
        recyclerView.setAdapter(pedidosAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_add_pedido:
                Intent i = new Intent(self, DetalleActivity.class);
                i.putExtra("id", 0L);
                startActivityForResult(i, NUEVO_PEDIDO);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(final int requestCode,final int resultCode,final Intent data) {
        if(resultCode==Activity.RESULT_OK){
            pedidos=BBDD.dbPedido.obtenerPedidos();
            iniAdapter();
        }
    }

    /**********SEARCH********************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_pedidos,menu);
        searchView = (SearchView)menu.findItem(R.id.menu_search).getActionView();
        iniSearchView(searchView);

        return true;
    }

    private void iniSearchView(final SearchView searchView){

        //Hint
        searchView.setQueryHint(getResources().getString(R.string.search_hint));

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
                pedidos = BBDD.dbPedido.buscarPedidos(DBPedido.PEDIDO_CAB_COLS[13], query);
                pedidosAdapter.setPedidos(pedidos);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 2) {
                    updateSearchSuggestion(newText);
                }
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                pedidos = BBDD.dbPedido.obtenerPedidos();
                pedidosAdapter.setPedidos(pedidos);
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
                String empresa=cursor.getString(cursor.getColumnIndex(DBPedido.PEDIDO_CAB_COLS[13]));
                searchView.setQuery(empresa,true);
                return true;
            }
        });
    }

    private void updateSearchSuggestion(String query){

        int code=0;
        try{
            code=Integer.parseInt(query);
        }catch (NumberFormatException ne){

        }
        String col1=BBDD.dbPedido.PEDIDO_CAB_COLS[13];//empresa
        String col2=BBDD.dbPedido.PEDIDO_CAB_COLS[4];//codigo


        Cursor cursor;
        if(code>0) {
            //Busco por codigo
            cursor = BBDD.dbPedido.obtenerPedidosAutocompletar(query,col2);
        }
        else{
            //Busco por nombre
            cursor = BBDD.dbPedido.obtenerPedidosAutocompletar(query,col1);
        }

        if(searchAdapter==null){
            searchAdapter = new SearchSuggestionAdapter(this, cursor, col1, col2);
            searchView.setSuggestionsAdapter(searchAdapter);
        }
        else{
            searchAdapter.refill(cursor);
            searchView.setSuggestionsAdapter(searchAdapter);
        }
    }
}
