package com.example.phone;

import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView dialogBtn,closeAlert;
    ViewPager viewPager;
    RadioGroup radioGroup;
    private List<Fragment> fragmentList = new ArrayList<>();
    private MyPagerAdapter adapter;
    private  Cursor c;
    private Handler handler;
    private LinearLayout layoutAlert;
    private MyDatabaseHelper dbHelper;
    private Button copy_dataBtn,recovery_data;
    public List<JsonBean> list = new ArrayList<>();
    private List<AddressPeople> peoplelist = new ArrayList<>();
    private Map<String ,Object> jsonmap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);  //透明导航栏

        dialogBtn = (ImageView) findViewById(R.id.dialog);
        closeAlert = (ImageView) findViewById(R.id.close_alert);
        layoutAlert = (LinearLayout) findViewById(R.id.alert);
        copy_dataBtn = (Button)findViewById(R.id.copy_data);
        recovery_data = (Button)findViewById(R.id.recovery_data);

        layoutAlert.setVisibility(View.GONE);
        dialogBtn.setOnClickListener(this);
        closeAlert.setOnClickListener(this);
        copy_dataBtn.setOnClickListener(this);
        recovery_data.setOnClickListener(this);


        //构建一个 MyDatabaseHelper 对象，通过构造函数将数据库名指定为 People.db
        dbHelper = new MyDatabaseHelper(this,"People.db",null,1);

        //读取手机联系人
        readContacts();
        /**
         *调用getWritableDatabase() 方法
         * 自动检测当前程序中 People.db 这个数据库
         * 如果不存在则创建该数据库并调用 onCreate() 方法
         * 同时People表也会被创建
         */
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //cursor是每行的集合
        Cursor cursor = db.query("people",null,null,null,null,null,null);
        if(!cursor.moveToFirst()){
            ContentValues values = new ContentValues();
            for (int i=0; i<list.size(); i++){
                values.put("name",list.get(i).people);
                values.put("phone",list.get(i).phone);
                db.insert("people",null,values);
                values.clear();
            }
        }
        initData();
        initView();
    }

    @Override
    //调用requestPermissions()后，系统弹出权限申请的对话框，选择后回调到下面这个函数，授权结果会封装到grantResults
    //grant授权
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readContacts();
                } else {
                    Toast.makeText(this, "你被拒绝此权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    //读取手机联系人
    private void readContacts() {
        Cursor cursor = null;
        try {
            //cursor指针 query询问 contract协议 kinds种类
            // projection告诉Provider要返回的内容 selection设置条件,相当于SQL语句中的where
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    JsonBean jsonBean = new JsonBean();
                    jsonBean.people = displayName;
                    jsonBean.phone = number;
                    list.add(jsonBean);
                }
                //notify公布,调用notifyDataSetChanged()通知更新数据
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.dialog:
                layoutAlert.setVisibility(View.VISIBLE);
                break;
            case R.id.close_alert:
                layoutAlert.setVisibility(View.GONE);
                break;
            case R.id.copy_data:
                initCopyData();
                Gson gson = new Gson();
                String jsonStr = gson.toJson(jsonmap);
                sendCopyData(jsonStr);
                break;
            case R.id.recovery_data:
                recoveryData();
                break;
        }
    }

    //获取需要备份的数据
    private void initCopyData()  {
        peoplelist = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("people",null,null,null,null,null,null);

        if(cursor.moveToFirst()){
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String phone = cursor.getString(cursor.getColumnIndex("phone"));
                String address = cursor.getString(cursor.getColumnIndex("address"));

                if(address == null){
                    address = "";
                }

                peoplelist.add(new AddressPeople(name,phone,address));
            }while (cursor.moveToNext());
        }
        cursor.close();
        jsonmap.put("json",peoplelist);
    }


    //将备份数据传至数据库
    private void sendCopyData(String data){

        //初始化请求对象
        RequestParams params = new RequestParams("http://111.230.230.236:8080/StudentPlanSystem/People/Copypeople");

        // 设置请求所需要的参数(请替换成实际的参数与值)
        params.addBodyParameter("data", data);
        //开始请求
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                try{
                    String beanJson = new JSONObject(result).toString();

                    //初始化Gson对象
                    Gson gson = new Gson();

                    //beanJson为请求后得到的JSON数据,JsonBean[].class为一个包含多个bean的数组.
                    JsonBean jsonBean = gson.fromJson(beanJson, JsonBean.class);

                    if ("1".equals(jsonBean.result)){
                        Toast.makeText(MainActivity.this,"一共备份了"+jsonBean.count+"条数据",Toast.LENGTH_SHORT).show();
                        layoutAlert.setVisibility(View.GONE);
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }

            //请求失败的回调，可以在这里面给用户提示网络错误
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(MainActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
            }

            //请求取消的回调，这个基本不用
            @Override
            public void onCancelled(CancelledException cex) {

            }

            //请求结束的回调
            @Override
            public void onFinished() {
                System.out.print("finished: jjjjjjjjjj");
            }
        });
    }

    //恢复数据
    private void recoveryData(){
        //初始化请求对象
        RequestParams params = new RequestParams("http://111.230.230.236:8080/StudentPlanSystem/People/selectPeople");

        //开始请求
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                try{
                    String beanJson = new JSONObject(result).getJSONArray("data").toString();
                    List<JsonBean> recoverylist = new ArrayList<>();
                    //初始化Gson对象
                    Gson gson = new Gson();

                    //beanJson为请求后得到的JSON数据,JsonBean[].class为一个包含多个bean的数组.
                    JsonBean[] jsonBeans = gson.fromJson(beanJson, JsonBean[].class);
                    initCopyData();
                    int count = 0;
                    for(JsonBean jsonBean : jsonBeans){
                        boolean flag = true;
                        for(int i=0; i<peoplelist.size(); i++){
                            if(peoplelist.get(i).getName().equals(jsonBean.name)&&peoplelist.get(i).getPhone().equals(jsonBean.phone)){
                                flag = false;
                            }
                        }
                        if(flag){
                            count ++;
                            recoverylist.add(jsonBean);
                        }
                    }
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    for (int i=0; i<recoverylist.size(); i++){
                        values.put("name",recoverylist.get(i).name);
                        values.put("phone",recoverylist.get(i).phone);
                        values.put("address",recoverylist.get(i).address);
                        db.insert("people",null,values);
                        values.clear();
                    }
                    initView();
                    Toast.makeText(MainActivity.this,"一共恢复了"+count+"条数据",Toast.LENGTH_SHORT).show();
                    layoutAlert.setVisibility(View.GONE);

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }

            //请求失败的回调，可以在这里面给用户提示网络错误
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(MainActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
            }

            //请求取消的回调，这个基本不用
            @Override
            public void onCancelled(CancelledException cex) {

            }

            //请求结束的回调
            @Override
            public void onFinished() {
                System.out.print("finished: jjjjjjjjjj");
            }
        });
    }

    private void initData(){
        fragmentList.add(new TuohuaFragment());
        fragmentList.add(new AddressFragment());
    }

    private void initView(){
        viewPager=(ViewPager)findViewById(R.id.viewpager_main);
        radioGroup=(RadioGroup)findViewById(R.id.rg_main);
        radioGroup.check(R.id.rb_lianxiren);
        adapter = new MyPagerAdapter(getSupportFragmentManager(),fragmentList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        radioGroup.check(R.id.rb_tonghua);
                        break;
                    case 1:
                        radioGroup.check(R.id.rb_lianxiren);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.rb_tonghua:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.rb_lianxiren:
                        viewPager.setCurrentItem(1);
                        break;
                }
            }
        });
    }

    public void setHandler(Handler handler){
        this.handler = handler;
    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mfragmentList;

        public MyPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.mfragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return mfragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mfragmentList.size();
        }
    }

}
