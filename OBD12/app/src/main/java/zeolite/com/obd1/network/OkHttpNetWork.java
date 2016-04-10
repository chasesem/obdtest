package zeolite.com.obd1.network;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by Zeolite on 16/3/16.
 */
public class OkHttpNetWork {



    public InputStream sendPost(String url,String params) throws IOException
    {
        URL realurl = null;
        InputStream in = null;
        HttpURLConnection conn = null;
        try{
            realurl = new URL(url);
            conn = (HttpURLConnection)realurl.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            PrintWriter pw = new PrintWriter(conn.getOutputStream());
            pw.print(params);
            pw.flush();
            pw.close();
            in = conn.getInputStream();

            System.out.println(in.toString());
        }catch(MalformedURLException eio){

        }
        return in;
    }


    private final OkHttpClient client = new OkHttpClient();

    public void get(String url) throws Exception {
        Request request = new Request.Builder()
                .url(" http://139.129.117.26:9190/ajaxService/cms/UserHandler.asmx/UserLogin?option={\"Code\":\"test\",\"Id\":\"123\",\"Name\":\"test\",\"Pwd\":\"test\"}")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                Headers responseHeaders = response.headers();
                for (int i = 0; i < responseHeaders.size(); i++) {
                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                System.out.println(response.body().string());
            }
        });
    }


    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public String post(String url, String json) throws IOException {

        RequestBody formBody = new FormEncodingBuilder()
                .add("option", json)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

}
