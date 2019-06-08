package com.example.phone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TuohuaFragment extends Fragment {

    private  LayoutInflater currurInflater;
    private  View currurView;
    private ListView THlist;
    private TextView th_warnText;
    private MyDatabaseHelper dbHelper;
    private List<Thjilu> thjilus = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        currurInflater = inflater;
        currurView = currurInflater.inflate(R.layout.fragment_tuohua, container, false);
        dbHelper = new MyDatabaseHelper(getContext(),"People.db",null,1);
        THlist = (ListView) currurView.findViewById(R.id.thlv);
        th_warnText = (TextView)currurView.findViewById(R.id.th_warn);
        initData();
        return currurView;
    }

    public class MyAdapter extends ArrayAdapter<Thjilu>{

        private int resourceId;

        public MyAdapter(Context context, int textViewResourceId, List<Thjilu> objects){
            super(context,textViewResourceId,objects);
            resourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Thjilu th = getItem(position);
            View view = currurInflater.inflate(resourceId,parent,false);
            TextView lvName = (TextView)view.findViewById(R.id.outname);
            TextView lvTime = (TextView)view.findViewById(R.id.outtime);
            lvName.setText(th.name);
            lvTime.setText(th.time);

            return view;
        }
    }

    private void initData(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("record",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do {
                Thjilu l = new Thjilu();
                l.name = cursor.getString(cursor.getColumnIndex("name"));
                l.time = cursor.getString(cursor.getColumnIndex("time"));
                thjilus.add(l);
            }while (cursor.moveToNext());

        }
        if (thjilus.size()>0){
            th_warnText.setVisibility(View.GONE);
            THlist.setAdapter(new MyAdapter(getContext(),R.layout.th_item,thjilus));
        }else{
            th_warnText.setVisibility(View.VISIBLE);
        }
    }

}
