package zeolite.com.obd1.view.register;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.okhttp.FormEncodingBuilder;
import com.avos.avoscloud.okhttp.OkHttpClient;
import com.avos.avoscloud.okhttp.Request;
import com.avos.avoscloud.okhttp.RequestBody;
import com.avos.avoscloud.okhttp.Response;

import java.io.IOException;

import zeolite.com.obd1.MainActivity;
import zeolite.com.obd1.R;

/**
 * Created by Zeolite on 16/1/11.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private Handler mHandler;
    private EditText userNameET;
    private EditText passwordET;
    private EditText repectPasswordET;
    private Button registerBtn;
    private String sUserName;
    private String sPassWord;
    private final int MSG_USERNAME = 0;
    private final int MSG_USERPWD = 1;
    private final int RESULT = 2;
    boolean result = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        initView();

    }



    private void initView() {
        userNameET = (EditText) findViewById(R.id.et_userName);
        passwordET = (EditText) findViewById(R.id.et_password);
        repectPasswordET = (EditText) findViewById(R.id.et_repect_password);
        registerBtn = (Button) findViewById(R.id.btn_register);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_register:

                String url = "";
                sUserName = userNameET.getText().toString();
                sPassWord =passwordET.getText().toString();
                String repectPassword = repectPasswordET.getText().toString();
                if (sPassWord != repectPassword) {
                    //Toast.makeText()
                    Toast.makeText(this, "密码与确认密码不一致", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        Handler mNetHandler = new Handler() {

                            @Override
                            public void handleMessage(Message msg) {
                                // TODO Auto-generated method stub

                                //开启网络线程
                                CustomThread mNetThread = new CustomThread(sUserName, sPassWord);
                                mNetThread.start();

                            }

                        };
                        mHandler = new Handler() {

                            @Override
                            public void handleMessage(Message msg) {
                                // TODO Auto-generated method stub
                                super.handleMessage(msg);
                                // 接收网络线程的消息
                                if (msg.what == RESULT) {
                                    //address = (String) msg.obj;
                                    Toast.makeText(RegisterActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
                                    if (msg.obj.toString() == "true") {
                                        result = true;
                                    }
                                }

                            }
                        };

                        //result = doPost(url, sUserName, sPassWord);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;
        }
    }

    class CustomThread extends Thread {
        String username;
        String userpwd;

        public CustomThread(String username, String userpwd) {
            this.username = username;
            this.userpwd = userpwd;
        }

        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            try {
                Log.i("---------", "aaaaaaaaaaa");
                doPost("1", username, userpwd);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Looper.loop();
        }

        public boolean doPost(String url, String userName, String passWord) throws IOException {
            OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormEncodingBuilder().add("Code", userName).add("Password", passWord).build();
            Request request = new Request.Builder().url(url).post(formBody).build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                com.alibaba.fastjson.JSONObject jsonString = com.alibaba.fastjson.JSONObject.parseObject(response.body().toString());
                Boolean result = jsonString.getBoolean("Result");
                return result;
            } else {

                throw new IOException("Unexpected code" + response);

            }

        }
    }
}