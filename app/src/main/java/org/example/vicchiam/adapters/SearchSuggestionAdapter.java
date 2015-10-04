package org.example.vicchiam.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.example.vicchiam.mispedidos.R;

/**
 * Created by vicch on 26/09/2015.
 */
public class SearchSuggestionAdapter extends CursorAdapter {

    private String col_name_1;
    private String col_name_2;

    public SearchSuggestionAdapter(Context context, Cursor cursor, String col_name_1, String col_name_2){
        super(context,cursor,0);
        this.col_name_1=col_name_1;
        this.col_name_2=col_name_2;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.search_suggestion_item,parent,false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView t1=(TextView)view.findViewById(R.id.search_suggestion_item_1);
        TextView t2=(TextView)view.findViewById(R.id.search_suggestion_item_2);

        t1.setText(cursor.getString(cursor.getColumnIndex(col_name_1)));
        t2.setText(cursor.getString(cursor.getColumnIndex(col_name_2)));
    }

    public void refill(Cursor cursor){
        changeCursor(cursor);
    }

}
