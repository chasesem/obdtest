package zeolite.com.obd1.view.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.internal.http.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import zeolite.com.obd1.MainActivity;
import zeolite.com.obd1.R;
import zeolite.com.obd1.network.OkHttpNetWork;
import zeolite.com.obd1.network.VolleyNetWork;
import zeolite.com.obd1.view.register.RegisterActivity;


/**
 * Created by XUEZE on 1/11/2016.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOGIN_URL="http://139.129.117.26:9190/ajaxService/cms/UserHandler.asmx/UserLogin";

    private EditText uesenameET;
    private EditText passwordET;
    private TextView unregisterText;
    private Button loginBtn;
    private Handler mHandler;
    private String sUserName;
    private String sPassWord;
    private final int MSG_USERNAME = 0;
    private final int MSG_USERPWD = 1;
    private final int RESULT = 2;
    boolean result = false;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
//                    try {
//                        Log.i("111", "1111");
//                        doPost("", sUserName, sPassWord);
//                        Log.i("222", "2222");
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);


        initView();
        initData();
    }

    private void initData() {
        sUserName = "";
        sPassWord = "";
        unregisterText.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
    }

    private void initView() {
        uesenameET=(EditText)findViewById(R.id.et_userName);
        passwordET=(EditText)findViewById(R.id.et_password);
        unregisterText = (TextView) findViewById(R.id.unregisterText);
        loginBtn = (Button) findViewById(R.id.btn_login);
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.unregisterText:
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_login:
                sUserName = uesenameET.getText().toString();
                sPassWord = passwordET.getText().toString();



                JSONObject temp = new JSONObject();
                temp.put("Id", "123");
                temp.put("Code", "test");
                temp.put("Name", sUserName);
                temp.put("Pwd", sPassWord);


                final String url=LOGIN_URL+"?option="+temp.toJSONString();
                Log.i("----------a",url);



                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpURLConnection conn;
                        InputStream is;
                        try {
                            conn = (HttpURLConnection) new URL(url).openConnection();
                            conn.setRequestMethod("GET");
                            is = conn.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                            String line = "";
                            StringBuffer result = new StringBuffer();
                            while ( (line = reader.readLine()) != null ){
                                result.append(line);
                            }
                            Log.i("temp",result.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

//                VolleyNetWork volleyNetWork=new VolleyNetWork();
//                volleyNetWork.send();


//                try {
//                    okHttpNetWork.get("http://139.129.117.26:9190/ajaxService/cms/UserHandler.asmx/UserLogin?option={%22Code%22:%22test%22,%22Id%22:%22123%22,%22Name%22:%22test%22,%22Pwd%22:%22test%22}");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }



//                String url = "";
//                Toast.makeText(this, sUserName, Toast.LENGTH_LONG).show();
//
//                handler.sendEmptyMessage(1);
//
//
//                try {
//                    Handler mNetHandler = new Handler() {
//
//                        @Override
//                        public void handleMessage(Message msg) {
//                            // TODO Auto-generated method stub
//
//                            //开启网络线程
//                            CustomThread mNetThread = new CustomThread(sUserName, sPassWord);
//                            mNetThread.start();
//
//                        }
//
//                    };
//                    mHandler = new Handler() {
//
//                        @Override
//                        public void handleMessage(Message msg) {
//                            // TODO Auto-generated method stub
//                            super.handleMessage(msg);
//                            // 接收网络线程的消息
//                            if (msg.what == RESULT) {
//                                //address = (String) msg.obj;
//                                Toast.makeText(LoginActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
//                                if (msg.obj.toString() == "true") {
//                                    result = true;
//                                }
//                            }
//
//                        }
//                    };
//
//                    //result = doPost(url, sUserName, sPassWord);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if (result == true) {
//                    SharedPreferences mySP = getSharedPreferences("test", Activity.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = mySP.edit();
//                    editor.putString("UserName", sUserName);
//                    editor.commit();
//                    intent = new Intent(LoginActivity.this, MainActivity.class);
//                    startActivity(intent);
//                } else {
//
//                }
                break;
        }
    }


//    class CustomThread extends Thread {
//        String username;
//        String userpwd;
//
//        public CustomThread(String username, String userpwd) {
//            this.username = username;
//            this.userpwd = userpwd;
//        }
//
//        @Override
//        public void run() {
//            // TODO
//            // 在这里进行 http request.网络请求相关操作
//            try {
//                Log.i("---------", "aaaaaaaaaaa");
//                doPost("1", username, userpwd);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            Looper.loop();
//        }
//
//
//    }
//
//    ;
//
//    public boolean doPost(String url, String userName, String passWord) throws IOException {
//
//        JSONObject temp = new JSONObject();
//        temp.put("Id","123");
//        temp.put("Code","test");
//        temp.put("Name",userName);
//        temp.put("Pwd",passWord);
//
//
//        String userDateJson = temp.toJSONString();
//        OkHttpClient client = new OkHttpClient();
//        RequestBody formBody = new FormEncodingBuilder().add("option",userDateJson).build();
//        Request request = new Request.Builder().url("http://139.129.117.26:9190/ajaxService/cms/UserHandler.asmx/UserLogin").post(formBody).build();
//        Response response = client.newCall(request).execute();
//        if(response.isSuccessful()){
//            com.alibaba.fastjson.JSONObject jsonString = com.alibaba.fastjson.JSONObject.parseObject(response.body().toString());
//            Boolean result = jsonString.getBoolean("Result");
//            return result;
//        }
//
//        else{
//
//            throw new IOException("Unexpected code" + response);
//
//        }
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
