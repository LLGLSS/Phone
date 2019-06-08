package com.example.phone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private Button backBtn,sureBtn,registerBtn;
    private TextView title;
    private EditText register_nameEdit,register_passwordEdit,password_plusEdit;

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.register:
                String register_name = register_nameEdit.getText().toString();
                String register_password = register_passwordEdit.getText().toString();
                String password_plus = password_plusEdit.getText().toString();
                if(register_name.equals("") || register_name == null){
                    Toast.makeText(RegisterActivity.this,"账号不能为空",Toast.LENGTH_SHORT).show();
                }else if (register_password.equals("") || register_password == null){
                    Toast.makeText(RegisterActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                }else if (password_plus.equals("") || password_plus == null){
                    Toast.makeText(RegisterActivity.this,"确认密码不能为空",Toast.LENGTH_SHORT).show();
                }else if (!password_plus.equals(register_password)){
                    Toast.makeText(RegisterActivity.this,"两次密码不一致",Toast.LENGTH_SHORT).show();
                }else{
                    getDataFromServer(register_name,register_password);
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);  //透明导航栏
        backBtn = (Button)findViewById(R.id.back);
        sureBtn = (Button)findViewById(R.id.sure);
        registerBtn = (Button)findViewById(R.id.register);
        register_nameEdit = (EditText) findViewById(R.id.register_name);
        register_passwordEdit = (EditText) findViewById(R.id.register_password);
        password_plusEdit = (EditText) findViewById(R.id.password_plus);

        title = (TextView)findViewById(R.id.title);

        title.setText("用户注册");
        sureBtn.setVisibility(View.GONE);
        backBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);

    }

    private void getDataFromServer(String name,String password){

        //初始化请求对象
        RequestParams params = new RequestParams("http://111.230.230.236:8080/StudentPlanSystem/Login/register");

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
                        Toast.makeText(RegisterActivity.this,jsonBean.msg,Toast.LENGTH_SHORT).show();
                        registerBtn.setText("点击返回登录页面");
                        registerBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }else{
                        Toast.makeText(RegisterActivity.this,jsonBean.msg,Toast.LENGTH_SHORT).show();
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }

            //请求失败的回调，可以在这里面给用户提示网络错误
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                System.out.print("error: jjjjjjjjjj");
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
