package zeolite.com.obd1.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;




import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zeolite on 15/3/30.
 */
public class HttpUtils {


    public static void getNewsJSON(final String url, final Handler handler){

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
                    Message msg = new Message();
                    msg.obj = result.toString();
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

//
//
//
//    public static void setPicBitmap(final ImageView ivPic,final String pic_url ,final Handler handler){
//
//        final LoadImage loadImage=new LoadImage();
//        InputStream is;
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//
//                        HttpURLConnection conn = (HttpURLConnection) new URL(pic_url).openConnection();
//                        conn.connect();
//                        InputStream is = conn.getInputStream();
//
//                        Bitmap bitmap = BitmapFactory.decodeStream(is);
//
//                        Message msg = new Message();
//                        msg.obj = bitmap;
//                        handler.sendMessage(msg);
//                        is.close();
//
//
//                //    ivPic.setImageBitmap(bitmap);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//
//    }





}
