package com.example.phone;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Information_Detail extends AppCompatActivity implements View.OnClickListener {

    private ImageView alert_del,detil_close_alert;
    private LinearLayout alertLayout,activity_information__detail;
    private Button detil_back,del_peopleBtn;
    private TextView name_content,call,addressText;
    private String callphone="";
    private FloatingActionButton admenButton;
    private RadioButton shareButton,collectionButton;
    private MyDatabaseHelper dbhelper;
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.share:
                String shareText = name_content.getText().toString()+"的电话："+call.getText().toString();
                Intent textIntent = new Intent(Intent.ACTION_SEND);
                textIntent.setType("text/plain"); //分享的是文本类型
                textIntent.putExtra(Intent.EXTRA_TEXT, shareText); //分享出去的内容
                startActivity(Intent.createChooser(textIntent, "分享到"));
                break;
            case R.id.diadel:
                alertLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.detil_back:
                Intent i = new Intent(Information_Detail.this,MainActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.detil_close_alert:
                alertLayout.setVisibility(View.GONE);
                break;
            case R.id.call:
                String rename = name_content.getText().toString();
                insertTh(rename);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_CALL);//设置活动类型
                intent.setData(Uri.parse("tel:"+callphone));//设置数据
                startActivity(intent);//开启意图
                break;
            case R.id.goAdd:
                String addname = name_content.getText().toString();
                String addphone = call.getText().toString();
                String addAddress = addressText.getText().toString();
                Intent go = new Intent(Information_Detail.this,AddPeopleActivity.class);
                go.putExtra("name",addname);
                go.putExtra("phone",addphone);
                go.putExtra("address",addAddress);
                startActivity(go);
                finish();
                break;
            case R.id.del_people:
                String name = name_content.getText().toString();
                String phone = call.getText().toString();
                deletePeople(name,phone);
                Toast.makeText(Information_Detail.this,"删除成功",Toast.LENGTH_SHORT).show();
                Intent del = new Intent(Information_Detail.this,MainActivity.class);
                startActivity(del);
                finish();
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_detail);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);  //透明导航栏

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String phone = intent.getStringExtra("phone");
        String address = intent.getStringExtra("address");

        dbhelper = new MyDatabaseHelper(this,"People.db",null,1);
        callphone = phone;

        alertLayout = (LinearLayout) findViewById(R.id.content_alert);
        activity_information__detail = (LinearLayout) findViewById(R.id.activity_information__detail);
        alert_del = (ImageView) findViewById(R.id.diadel);
        detil_close_alert = (ImageView) findViewById(R.id.detil_close_alert);
        detil_back = (Button)findViewById(R.id.detil_back);
        name_content = (TextView)findViewById(R.id.name_content);
        call = (TextView)findViewById(R.id.call);
        addressText = (TextView)findViewById(R.id.address);
        admenButton = (FloatingActionButton)findViewById(R.id.goAdd);
        shareButton = (RadioButton)findViewById(R.id.share);
        del_peopleBtn = (Button)findViewById(R.id.del_people);

        name_content.setText(name);
        call.setText(phone);
        if (address != null){
            addressText.setText(address);
        }

        detil_back.setOnClickListener(this);
        detil_close_alert.setOnClickListener(this);
        alert_del.setOnClickListener(this);
        call.setOnClickListener(this);
        admenButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);
        del_peopleBtn.setOnClickListener(this);

    }

    private void deletePeople(String name,String phone){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        db.delete("people","name=? AND phone=?",new String[]{name,phone});
    }

    private void insertTh(String name){
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式

        String time = dateFormat.format( now );

        SQLiteDatabase db = dbhelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name",name);
        values.put("time",time);
        db.insert("record",null,values);
        values.clear();

    }

}
