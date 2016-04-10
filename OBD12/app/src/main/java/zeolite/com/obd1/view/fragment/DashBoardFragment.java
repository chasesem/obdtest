package zeolite.com.obd1.view.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import zeolite.com.obd1.MainActivity;
import zeolite.com.obd1.R;

/**
 * Created by Zeolite on 16/1/20.
 */
public class DashBoardFragment extends Fragment {

    public static final int ARG_POSITION=1;

    private Context context;

    private WebView mWebView;

    private final Timer timer=new Timer();
    private TimerTask task;

    //***listView
    private ListView meListView;

    private  int speed=0;
    private  int rmp=0;
    private  int gas=0;
    private  int water=0;
    private  int fuelPressure=0;
    private  int oilLevel=0;
    private  int temperature=0;
    private  String faultCode=null;

    private TextView textView1,textView2,textView3,textView4,textView5,textView6,textView7,textView8;

    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
//                    int num= (int) (Math.random()*10);
                    if (mWebView==null){
                        Log.i("mWebView is nil","right");
                    }else {
                        mWebView.loadUrl("javascript: setData(" + speed +","+rmp/1000+","+gas+","+water+")");
                        Log.i("handler", "yoyoyo");

                        textView1.setText(speed + "km/h");
                        textView2.setText(rmp + "rmp");
                        textView3.setText(gas+"kPa");
                        textView4.setText(water+"°C");
                        textView5.setText(fuelPressure+"kPa");
                        textView6.setText(oilLevel+"%");
                        textView7.setText(temperature+"°C");
                        textView8.setText(faultCode);
                        textView8.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i("textView8","click");
                                Toast.makeText(getContext(),faultCode,Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    break;
            }
        }
    };

    protected WeakReference<View> mRootView;
    private View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        if (mRootView==null||mRootView.get()==null) {
             view=inflater.inflate(R.layout.fragment_dashboard,container, false);
            mRootView=new WeakReference<View>(view);
        }else{
            ViewGroup parent= (ViewGroup) mRootView.get().getParent();
            if (parent!=null){
                parent.removeView(mRootView.get());
            }
        }


        context=view.getContext();

        mWebView= (WebView) view.findViewById(R.id.webview);
        meListView=(ListView)view.findViewById(R.id.me_list);

        textView1=(TextView)view.findViewById(R.id.textView1);
        textView2=(TextView)view.findViewById(R.id.textView2);
        textView3=(TextView)view.findViewById(R.id.textView3);
        textView4=(TextView)view.findViewById(R.id.textView4);
        textView5=(TextView)view.findViewById(R.id.textView5);
        textView6=(TextView)view.findViewById(R.id.textView6);
        textView7=(TextView)view.findViewById(R.id.textView7);
        textView8=(TextView)view.findViewById(R.id.textView8);


        showWebView();
        return mRootView.get();
    }



    @SuppressLint("SetJavaScriptEnabled")
    private void showWebView(){
        try {

            mWebView.requestFocus();

            mWebView.setWebChromeClient(new WebChromeClient() {
                @JavascriptInterface
                @Override
                public void onProgressChanged(WebView view, int progress) {
//                    getActivity().setTitle("Loading...");
                    getActivity().setProgress(progress);

                    if (progress == 100) {
                        mWebView.loadUrl("javascript: setData(" + 0 +","+0+","+0+","+0+")");
                        Log.i("^^^","------");

                        task = new TimerTask() {
                            @Override
                            public void run() {
//                                handler.sendEmptyMessage(0);
                            }
                        };
                        timer.schedule(task, 1000, 1000);

                    }

                    if (progress >= 80) {
//                        getActivity().Title("JsAndroid Test");
                    }

                }

            });


            mWebView.setOnKeyListener(new View.OnKeyListener() {		// webview can go back
                @JavascriptInterface
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if(keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
                        mWebView.goBack();
                        return true;
                    }
                    return false;
                }
            });

            WebSettings webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDefaultTextEncodingName("utf-8");
            webSettings.setAllowUniversalAccessFromFileURLs(true);
            webSettings.setAllowFileAccess(true);
            webSettings.setAllowContentAccess(true);
            webSettings.setAllowFileAccessFromFileURLs(true);


            mWebView.addJavascriptInterface(getHtmlObject(), "jsObj");
            mWebView.loadUrl("file:///android_asset/test.html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    private Object getHtmlObject(){

        Object insertObj = new Object(){


            @JavascriptInterface
            public void setData(){
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        int num = (int) (Math.random() * 10);
                        mWebView.loadUrl("javascript: setData(" + 0 +","+0+","+0+","+0+")");
                        Toast.makeText(getContext(), "clickBtn2", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        return insertObj;
    }

    public void updateResultView(int speed, int rmp, int gas, int water,int fuelPressure,int oilLevel,int temperature,String faultCode){
        Log.i("****", speed + ","+rmp+","+gas+","+water+","+fuelPressure+","+oilLevel+","+temperature+","+faultCode);


        this.rmp=rmp;
        this.speed=speed;
        this.gas=gas;
        this.water=water;

        this.fuelPressure=fuelPressure;
        this.oilLevel=oilLevel;
        this.temperature=temperature;
        this.faultCode=faultCode;


        handler.sendEmptyMessage(0);

    }


}
