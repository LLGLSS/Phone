package com.example.phone;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;



public class MyDatabaseHelper extends SQLiteOpenHelper {

    public static final String CREATE_PEOPLES = "create table people ("
            +"id integer primary key autoincrement,"
            +"name text,"
            +"phone text,"
            +"address text)";

    public static final String CREATE_RECORD = "create table record ("
            +"id integer primary key autoincrement,"
            +"name text,"
            +"time text)";

    private Context mContext;

    public MyDatabaseHelper(Context context, String name,
                            SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库的同时创建Peoples表和Record表
        db.execSQL(CREATE_PEOPLES);
        db.execSQL(CREATE_RECORD);
        //提示数据库创建成功
        Toast.makeText(mContext,"Create succeeded",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
