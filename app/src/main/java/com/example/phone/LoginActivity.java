package com.example.phone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText nameText, passwordText;
    private Button loginBtn;
    private TextView GoRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);  //透明导航栏
        //检查是否获得权限
        //通过 ContextCompat.checkSelfPermission(context,permission)方法,方法返回值为 PackageManager.PERMISSION_GRANTED or PackageManager.PERMISSION_DENIED
        if(ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(LoginActivity.this,new String[]{Manifest.permission.CALL_PHONE},1);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }

        nameText =(EditText) findViewById(R.id.name);
        passwordText =(EditText) findViewById(R.id.password);
        loginBtn = (Button)findViewById(R.id.login);
        GoRegister = (TextView)findViewById(R.id.goRegister);

        GoRegister.setOnClickListener(this);
        loginBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login:
                String name = nameText.getText().toString();
                String password = passwordText.getText().toString();
                if(name.equals("") || name == null){
                    Toast.makeText(LoginActivity.this,"账号不能为空",Toast.LENGTH_SHORT).show();
                }else if (password.equals("") || password == null){
                    Toast.makeText(LoginActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                }else{
                    getDataFromServer(name,password);
                }
                break;
            case R.id.goRegister:
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void getDataFromServer(String name,String password){

        //初始化请求对象
        RequestParams params = new RequestParams("http://111.230.230.236:8080/StudentPlanSystem/Login/CheckLoginUser");

        // 设置请求所需要的参数(请替换成实际的参数与值)
        params.addBodyParameter("usercode", name);
        params.addBodyParameter("password", password);
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

                    if ("0".equals(jsonBean.result)){
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(LoginActivity.this,jsonBean.msg,Toast.LENGTH_SHORT).show();
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }

            //请求失败的回调，可以在这里面给用户提示网络错误
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(LoginActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
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
}

