package com.example.phone;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddPeopleActivity extends AppCompatActivity implements View.OnClickListener{

    private Button backButton,sureButton;
    private EditText nameEdit,phoneEdit,AddressEdit;
    private String oldName,oldPhone,oldAddress;
    private MyDatabaseHelper dbhelper;

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                Intent intent = new Intent(AddPeopleActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.sure:
                String name = nameEdit.getText().toString();
                String phone = phoneEdit.getText().toString();
                String address = AddressEdit.getText().toString();
                if (name.equals("") || phone.equals("")){
                    Toast.makeText(AddPeopleActivity.this,"姓名和电话不能为空",Toast.LENGTH_SHORT).show();
                }else{
                    if (oldName !=null && oldPhone != null){
                        updataPeople(name,phone,address,oldName,oldPhone);
                    }else{
                        insertPeople(name,phone,address);
                    }
                    Intent i = new Intent(AddPeopleActivity.this,Information_Detail.class);
                    i.putExtra("name",name);
                    i.putExtra("phone",phone);
                    i.putExtra("address",address);
                    startActivity(i);
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_people);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);  //透明导航栏

        dbhelper = new MyDatabaseHelper(this,"People.db",null,1);

        Intent intent = getIntent();
        oldName = intent.getStringExtra("name");
        oldPhone = intent.getStringExtra("phone");
        oldAddress = intent.getStringExtra("address");

        backButton = (Button) findViewById(R.id.back);
        sureButton = (Button) findViewById(R.id.sure);
        nameEdit = (EditText) findViewById(R.id.name);
        phoneEdit = (EditText) findViewById(R.id.phone);
        AddressEdit = (EditText) findViewById(R.id.address);

        if (oldName != null){
            nameEdit.setText(oldName);
        }
        if(oldPhone != null){
            phoneEdit.setText(oldPhone);
        }
        if(oldAddress!=null){
            AddressEdit.setText(oldAddress);
        }
        backButton.setOnClickListener(this);
        sureButton.setOnClickListener(this);

    }

    private void insertPeople(String name,String phone,String address){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name",name);
        values.put("phone",phone);
        values.put("address",address);
        db.insert("people",null,values);
        values.clear();
    }

    private void updataPeople(String name,String phone,String address,String oldname,String oldphone){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name",name);
        values.put("phone",phone);
        values.put("address",address);
        db.update("people",values,"name=? AND phone=?",new String[]{oldname,oldphone});
        values.clear();
    }

}
