package zeolite.com.obd1.network;

import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by ChingYam on 2016/4/10.
 */
public class VolleyNetWork {

   public void send() {
       Log.i("-----------a","123456");
       StringRequest request = new StringRequest(
               "http://www.baidu.com/",
               new Response.Listener<String>() {
                   @Override
                   public void onResponse(String arg0) {  //收到成功应答后会触发这里

                   }
               },
               new Response.ErrorListener() {
                   @Override
                   public void onErrorResponse(VolleyError volleyError) { //出现连接错误会触发这里

                   }
               }
       );
   }
}
