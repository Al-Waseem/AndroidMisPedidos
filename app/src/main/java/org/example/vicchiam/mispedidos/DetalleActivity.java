package org.example.vicchiam.mispedidos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import org.example.vicchiam.adapters.SearchSuggestionAdapter;
import org.example.vicchiam.bbdd.Cliente;
import org.example.vicchiam.bbdd.DBCliente;
import org.example.vicchiam.bbdd.Pedido;
import org.example.vicchiam.fragments.LineasFragment;
import org.example.vicchiam.fragments.PedidoFragment;
import org.example.vicchiam.utilidades.BBDD;
import org.example.vicchiam.utilidades.MyDrive;
import org.example.vicchiam.utilidades.PedidoTemp;
import org.example.vicchiam.utilidades.Utilidades;

import java.io.File;

public class DetalleActivity extends AppCompatActivity {

    public static final int NUEVA_LINEA=1;
    public static final int MODIFICAR_LINEA=2;

    private SearchSuggestionAdapter searchAdapter;
    private SearchView searchView;

    private ViewPager viewPager;
    private TabLayout tabLayout;

    private PedidoFragment pedidoFragment;
    private LineasFragment lineasFragment;

    private long id;
    private String campo_busqueda;

    private boolean lineas_modificadas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        id=getIntent().getExtras().getLong("id",0);

        pedidoFragment=PedidoFragment.newInstace(id);
        lineasFragment=LineasFragment.newInstance(id);

        iniToolbar();
        iniTabLayout();

        if(id>0) {
            PedidoTemp.pedido = BBDD.dbPedido.obtenerPedidoId(id);
        }
        else{
            PedidoTemp.pedido=new Pedido(PedidoTemp.getIDTemp());
            id=PedidoTemp.pedido.getId();
        }

        //Por defecto busca por nombre empresa
        campo_busqueda= DBCliente.CLIENTE_COLS[10];

        lineas_modificadas=false;

    }

    private void iniToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab=getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void iniTabLayout(){
        tabLayout=(TabLayout)findViewById(R.id.tab_layout);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        viewPager=(ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(new PageAdapter(getSupportFragmentManager(), DetalleActivity.this));
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        if(tabLayout.getSelectedTabPosition()==0){
            if(lineas_modificadas){//Obligo a pedidos a actualizar
                setResult(Activity.RESULT_OK);
            }
            finish();
        }
        else{
            tabLayout.getTabAt(0).select();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(tabLayout.getSelectedTabPosition()==0){
                    if(lineas_modificadas){//Obligo a pedidos a actualizar
                        setResult(Activity.RESULT_OK);
                    }
                    finish();
                }
                else{
                    tabLayout.getTabAt(0).select();
                }
                break;
            case R.id.menu_send:
                String path=Utilidades.guardarFichero(PedidoTemp.pedido.toCSV());
                if(path==null){
                    Utilidades.Alert(this,getResources().getString(R.string.error_mail));
                }else{
                    Log.d("File",path+" "+MyDrive.email_envio);

                    String to=((MyDrive.email_envio==null)?"":MyDrive.email_envio);

                    File f=new File(path);
                    Intent i=new Intent(Intent.ACTION_SEND);
                    i.putExtra(Intent.EXTRA_EMAIL,new String[]{to});
                    i.putExtra(Intent.EXTRA_SUBJECT, "AppPedidos");
                    i.putExtra(Intent.EXTRA_TEXT, "Pedido");
                    i.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + f.getAbsolutePath()));
                    i.setType("message/rfc822");
                    startActivity(i);
                }
                break;
            case R.id.menu_add_linea:
                if(tabLayout.getSelectedTabPosition()==0){
                    tabLayout.getTabAt(1).select();
                }
                else {
                    if (!PedidoTemp.pedido.isEntregado()) {
                        Intent i = new Intent(this, LineaActivity.class);
                        i.putExtra("id", 0L);
                        startActivityForResult(i, DetalleActivity.NUEVA_LINEA);
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(final int requestCode,final int resultCode,final Intent data) {
        if(resultCode== Activity.RESULT_OK){
            lineas_modificadas=true;
            lineasFragment.actualizarVista();
        }
    }


    /**********SEARCH********************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (id <= 0){
            inflater.inflate(R.menu.menu_detalle, menu);
            searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
            iniSearchView(searchView);
        }
        else{
            inflater.inflate(R.menu.menu_enviar, menu);
        }
        return true;
    }

    private void iniSearchView(final SearchView searchView){

        //Hint
        searchView.setQueryHint(getResources().getString(R.string.dp_search_hint));

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

        //searchView.setIconified(false);
        //searchView.requestFocusFromTouch();

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

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor=(Cursor)searchAdapter.getItem(position);
                long id_cliente=cursor.getLong(cursor.getColumnIndex("_id"));
                Cliente c=BBDD.dbCliente.obtenerClienteId(id_cliente);
                actualizarPedido(c);
                searchView.clearFocus();
                return true;
            }
        });
    }

    private void updateSearchSuggestion(String query){

        String campo2=DBCliente.CLIENTE_COLS[10];//Por defecto 2campo es nombre empresa
        if(campo_busqueda.equals(campo2)){
            campo2=DBCliente.CLIENTE_COLS[1];//Segundo campo es codigo
        }
        Cursor cursor = BBDD.dbCliente.obtenerClientesAutocompletar(query,campo_busqueda, campo2);

        searchAdapter = new SearchSuggestionAdapter(this, cursor, this.campo_busqueda, campo2);
        searchView.setSuggestionsAdapter(searchAdapter);

    }

    public void abrirSearch(String campo_busqueda){
        this.campo_busqueda=campo_busqueda;
        if(searchView.isIconified()) {
            searchView.setIconified(false);
            searchView.requestFocusFromTouch();
        }
    }

    //Se llama cuando elegimos un cliente en buscar
    private void actualizarPedido(Cliente c){
        pedidoFragment.setClientePedido(c);
    }

    private class PageAdapter extends FragmentPagerAdapter{

        public final int NUM_PAGES=2;
        private Context context;

        public PageAdapter(FragmentManager fm,Context context){
            super(fm);
            this.context=context;
        }

        @Override
        public Fragment getItem(int position) {
            if(position==0){
                return pedidoFragment;
            }
            else{
                return lineasFragment;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position==0)
                return getResources().getString(R.string.page1);
            else
                return getResources().getString(R.string.page2);
        }

    }

}
