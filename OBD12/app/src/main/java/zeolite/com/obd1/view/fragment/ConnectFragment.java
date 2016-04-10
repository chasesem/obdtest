package zeolite.com.obd1.view.fragment;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.okhttp.FormEncodingBuilder;
import com.avos.avoscloud.okhttp.OkHttpClient;
import com.avos.avoscloud.okhttp.Request;
import com.avos.avoscloud.okhttp.RequestBody;
import com.avos.avoscloud.okhttp.Response;


import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import zeolite.com.obd1.R;
import zeolite.com.obd1.entity.upload.Data;
import zeolite.com.obd1.entity.upload.Engine;
import zeolite.com.obd1.entity.upload.Oil;
import zeolite.com.obd1.entity.upload.UploadData;
import zeolite.com.obd1.view.bluetooth.OBD2MonitorDevicesActivity;
import zeolite.com.obd1.view.bluetooth.OBD2MonitorHelpActivity;
import zeolite.com.obd1.view.bluetooth.OBD2MonitorService;

/**
 * Created by Zeolite on 16/1/20.
 */
public class ConnectFragment extends Fragment {

    private static View view=null;

    private SensorManager sensorManager;

    // Message types sent from the OBD2MonitorService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_RESULT=6;
    public static final int MESSAGE_NO_DATA=7;
    public static final int MESSAGE_TIMER=8;


    protected final static char[] dtcLetters = {'P', 'C', 'B', 'U'};

    protected final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    protected StringBuilder codes = null;



    public static int num=0;

    // LogFile
    public static final String DIR_NAME_OBD2_MONITOR = "OBDIIMonitorLog";
    public static final String FILE_NAME_OBD2_MONITOR_LOG = "obd2_monitor_log.txt";

    // Key names received from the OBD2MonitorService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    public enum AUTO_RES {
        AUTO_RES_NONE,
        AUTO_RES_OK,
        AUTO_RES_NORMAL,
        AUTO_RES_ERROR
    };

    private AUTO_RES autoRes = null;

    private String mConnectedDeviceName = null;
    // Bluetooth
    BluetoothAdapter mBluetoothAdapter = null;
    OBD2MonitorService mOBD2MonitorService = null;

    // widgets defination
//    private TextView mConnectedStatusTxt = null;
    private TextView mResponseMessageTxt = null;
    private TextView mSupportedPidsTxt = null;
    private EditText mInputOBD2CMDEditTxt = null;
    private Button mSendOBD2CMDBtn = null;
    private Button mTimerSendCMDBtn=null;
    private Button mCancelTimerBtn=null;

    private Button mSelectBtDevicesBtn = null;
    private Button mDisconnectDeviceBtn = null;

    private static StringBuilder mCmdAndRes = null;
    private String jsonString;
    // menu items
    private MenuItem mItemSetting = null;
    private MenuItem mItemQuit = null;
    private MenuItem mItemHelp = null;
    private MenuItem mItemAutoResOK = null;
    private MenuItem mItemAutoResNormal = null;
    private MenuItem mItemAutoResError = null;
    private static final String[] PIDS = {
            "01","02","03","04","05","06","07","08",
            "09","0A","0B","0C","0D","0E","0F","10",
            "11","12","13","14","15","16","17","18",
            "19","1A","1B","1C","1D","1E","1F","20"};

    int result=0;
    private TextView resultText;
    private Timer timer=null;
    private TimerTask task;

    private Map<String,Integer> pidMap=new HashMap<String,Integer>();
    private List<Map<String,Integer>> pidList=new ArrayList<Map<String, Integer>>();

    String[] cmdArray={"010C","010D","010B","0105","010A","012F","0146","0101","0104","0106",
            "0107","0108","0109","010E","010F","0110","0111","011F","0121","0122",
            "0123","012C","012D","012E","0130","0131","013C","013D","013E",
            "013F","0142","0143","0144","0145","0147","0148","0149","014A","014B",
            "014C","014D","014E","0152","0153","0154","0159","015A","015B","015C",
            "015E","0161","0162","0163"};

    private Engine engine;
    private Oil oil;
    private zeolite.com.obd1.entity.upload.Sensor sensorZ;
    private UploadData uploadData;
    private Data data;
    private OkHttpClient mHttpClient;
    private final Handler mMsgHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case MESSAGE_STATE_CHANGE:
                    switch(msg.arg1){
                        case OBD2MonitorService.STATE_CONNECTING:
                            setConnectedStatusTitle(R.string.device_connecting);
                            break;
                        case OBD2MonitorService.STATE_CONNECTED:
                            mSendOBD2CMDBtn.setEnabled(true);
                            mTimerSendCMDBtn.setEnabled(true);
                            mCancelTimerBtn.setEnabled(true);
                            mDisconnectDeviceBtn.setEnabled(true);
                            setConnectedStatusTitle(mConnectedDeviceName);
                            break;
                        case OBD2MonitorService.STATE_LISTEN:
                        case OBD2MonitorService.STATE_NONE:
                            mSendOBD2CMDBtn.setEnabled(false);
                            mTimerSendCMDBtn.setEnabled(false);
                            mCancelTimerBtn.setEnabled(false);
                            mDisconnectDeviceBtn.setEnabled(false);
//                            mConnectedStatusTxt.setText("");
                            break;
                        default:
                            break;
                    }
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    if(autoRes == AUTO_RES.AUTO_RES_NONE){
                        setPidsSupported(readMessage);
                        mCmdAndRes.append(" > Receive: " + readMessage);
                        mCmdAndRes.append('\n');
                        mResponseMessageTxt.setText(mCmdAndRes.toString());

                        Log.i("mmmmm", readMessage);
                        hexToDec(readMessage,mCmdAndRes.toString().trim());

                        //                    writeOBD2MonitorLog("Receive: "+ readMessage);
                    }else{
                        autoResponse(readMessage);
                    }
                    break;
                case MESSAGE_WRITE:
//                byte[] writeBuf = (byte[]) msg.obj;
//                // construct a string from the buffer
//                String writeMessage = new String(writeBuf);
//                sendCMDMessage(writeMessage);
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    break;

                case MESSAGE_RESULT:
//                    Toast.makeText(OBD2MonitorMainActivity.this,result,Toast.LENGTH_SHORT).show();
                    resultText.setText(result + "");

                    if (num==cmdArray.length){
                        num=0;
                    }

                    pidMap.put(cmdArray[num], result);

//                    Log.i("pidMap",pidMap.size()+"");

                    if (pidMap.size()==52){
                        pidList.add(pidMap);

                        Log.i("pidList", pidList.size() + "");

                        uploadPid(pidMap);

//                        AVObject testObject = new AVObject("pidTest");
//                        for (Map.Entry<String, Integer> entry : pidMap.entrySet()) {
//                            testObject.put(entry.getKey(), entry.getValue());
//                        }
//                        testObject.saveInBackground();
//                        pidMap.clear();
                    }



                    break;
                case MESSAGE_NO_DATA:
                    resultText.setText("no data");

                    break;

                case MESSAGE_TIMER:
                    if(mOBD2MonitorService.getState() != OBD2MonitorService.STATE_CONNECTED){
                        return;
                    }


                    if (num==cmdArray.length){
                        num=0;
                    }
                    String strCMD=cmdArray[num];
                    strCMD += '\r';
                    mCmdAndRes.append(" > Send: " + strCMD);
                    mCmdAndRes.append('\n');
                    mResponseMessageTxt.setText(mCmdAndRes.toString());
                    byte[] byteCMD = strCMD.getBytes();
                    mOBD2MonitorService.write(byteCMD);
                    num++;

                    break;
                default:
                    break;
            }
        }

    };

    //uploadData
    private void uploadPid(Map<String, Integer> pidMap) {
        uploadData.setEngines(engine);
        uploadData.setOils(oil);
        uploadData.setSensor(sensorZ);

        data.setData(uploadData);

        this.jsonString = JSON.toJSONString(data);
        final String url = "url/ajaxService/sys/OBDHandler.asmx/OBDCheck";
        this.jsonString=jsonString.replace("data","Data");
        this.jsonString=jsonString.replace("engines","Engine");
        this.jsonString=jsonString.replace("oils","Oil");
        this.jsonString=jsonString.replace("sensors","Sensor");
        this.jsonString=jsonString.replace("sensor","Sensor");

        //System.out.println(jsonString);//json data
        Thread thread = new MyThread1(url,this.jsonString,this);
        thread.start();

    }
   public String dopost(String url , String json) throws IOException{
        RequestBody formBody = new FormEncodingBuilder().add("option", json).build();
        Request request = new Request.Builder().url(url).post(formBody).build();
        Response response = this.mHttpClient.newCall(request).execute();
        if(response.isSuccessful()){
            return response.body().toString();
        }else{
            throw new IOException("Unexpected code" + response);

        }
    }

    private void hexToDec(String readMessage ,String res){

        if(readMessage.contains("SEARCHING")){
            return;
        }
        if (readMessage.contains("NO DATA")){
            return;
        }

        if (res.length()>6&&readMessage.length()>4) {
            readMessage = readMessage.replaceAll("\r", "").trim();
            String pid=readMessage.replace(" ","").substring(0, 4);
            if (readMessage.startsWith("01")){
                readMessage=readMessage.substring(4,readMessage.length());
            }

            switch (pid) {
                case "4101":
                case "0101":

                    faultCode=performCalute(readMessage.replaceAll(" ",""));

                    if (faultCode!=null){
                        Map<String, String> dtcVals = getDict(R.array.dtc_keys, R.array.dtc_values);

                        if (faultCode.equals("P000")){
                            faultCode="0";
                        }else {
                            Set entrySet = dtcVals.entrySet();
                            Iterator it2 = entrySet.iterator();
                            while(it2.hasNext())
                            {
                                Map.Entry entry = (Map.Entry)it2.next();
                                if (faultCode.equals(entry.getKey())){
                                    faultCode= (String) entry.getValue();
                                }else{
                                    faultCode="unknow";
                                }

                            }
                        }
                    }else {
                        faultCode = "0";
                    }
                    mCallback.onArticleSelected(speed,rmp,gas,water,fuelPressure,oilLevel, temperature,faultCode);
                    Log.i("faultCode", faultCode);
                    break;
                case "4104":
                case "0104":
                    if (length7(readMessage)!=null) {
                        result = length7(readMessage) * 100 / 255;
                        engine.set_0104(result);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                        Log.i("aaaaa", length7(readMessage) + "-->" + result);
                    }else{
                        engine.set_0104(null);
                    }
                    break;
                case "4105":
                case "0105":
                    if (length7(readMessage)!=null) {
                        result = length7(readMessage) - 40;
                        water = result;
                        engine.set_0105(result);
                        mCallback.onArticleSelected(speed, rmp, gas, water, fuelPressure, oilLevel, temperature, faultCode);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                        Log.i("aaaaa", length7(readMessage) + "-->" + result);
                    }else{
                        engine.set_0105(null);
                    }
                    break;
                case "4106":
                case "0106":
                    if (length7(readMessage)!=null) {
                        result = (length7(readMessage) - 128) * 100 / 128;
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                        Log.i("aaaaa", length7(readMessage) + "-->" + result);
                    }
                    break;
                case "4107":
                case "0107":
                    if (length7(readMessage)!=null) {
                        result = (length7(readMessage) - 128) * 100 / 128;
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }
                    break;
                case "4108":
                case "0108":
                    if (length7(readMessage)!=null) {
                        result = (length7(readMessage) - 128) * 100 / 128;
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }
                    break;
                case "4109":
                case "0109":
                    if (length7(readMessage)!=null) {
                        result = (length7(readMessage) - 128) * 100 / 128;
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }
                    break;
                case "410A":
                case "010A":
                    if (length7(readMessage)!=null) {
                        result = length7(readMessage) * 3;
                        fuelPressure = result;
                        oil.set_010A(result);
                        mCallback.onArticleSelected(speed, rmp, gas, water, fuelPressure, oilLevel, temperature, faultCode);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else{
                        oil.set_010A(null);
                    }
                    break;
                case "410B":
                case "010B":
                    if (length7(readMessage)!=null) {
                        result = length7(readMessage);
                        gas = result;
                        oil.set_010B(gas);
                        mCallback.onArticleSelected(speed, rmp, gas, water, fuelPressure, oilLevel, temperature, faultCode);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else{
                        oil.set_010B(null);
                    }
                    break;
                case "410C":
                case "010C":
                    if (length7(readMessage)!=null&&length9(readMessage)!=null) {
                        result = (length7(readMessage) * 256 + length9(readMessage)) / 4;
                        rmp = result;
                        engine.set_010C(result);
                        mCallback.onArticleSelected(speed, rmp, gas, water, fuelPressure, oilLevel, temperature, faultCode);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else{
                        engine.set_010C(null);
                    }
                    break;
                case "410D":
                case "010D":
                    if (length7(readMessage)!=null) {
                        result = length7(readMessage);
                        speed = result;
                        mCallback.onArticleSelected(speed, rmp, gas, water, fuelPressure, oilLevel, temperature, faultCode);
                        Log.i("aaaaa", length7(readMessage) + "-->" + result);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }
                    break;
                case "410E":
                case "010E":
                    if (length7(readMessage)!=null) {
                        result = (length7(readMessage) - 128) / 2;
                        engine.set_010E(result);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else{
                        engine.set_010E(null);
                    }
                    break;
                case "410F":
                case "010F":
                    if (length7(readMessage)!=null) {
                        result = length7(readMessage) - 40;
                        oil.set_010F(result);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else{
                        oil.set_010F(null);
                    }
                    break;
                case "4110":
                case "0110":
                    if (length7(readMessage)!=null&&length9(readMessage)!=null) {
                        result = (length7(readMessage) * 256 + length9(readMessage)) / 100;
                        sensorZ.set_0110(result);
                        Log.i("aaaaa", length7(readMessage) + "-->" + result);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else{
                        sensorZ.set_0110(null);
                    }
                    break;
                case "4111":
                case "0111":
                    if (length7(readMessage)!=null) {
                        result = length7(readMessage) * 100 / 255;
                        sensorZ.set_0111(result);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else{
                        sensorZ.set_0111(null);
                    }
                    break;
                case "411F":
                case "011F":
                    if (length7(readMessage)!=null&&length9(readMessage)!=null) {
                        result = length7(readMessage) * 256 + length9(readMessage);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }
                    break;
                case "4121":
                case "0121":
                    if (length7(readMessage)!=null) {
                        result = length7(readMessage) * 256 + length9(readMessage);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }
                    break;
                case "4122":
                case "0122":
                    if (length7(readMessage)!=null&&length9(readMessage)!=null) {
                        result = (int) ((length7(readMessage) * 256 + length9(readMessage)) * 0.079);
                        oil.set_0122(result);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else{
                        oil.set_0122(null);
                    }
                    break;
                case "4123":
                case "0123":
                    if (length7(readMessage)!=null&&length9(readMessage)!=null) {
                        result = ((length7(readMessage) * 256 + length9(readMessage)) * 10);
                        oil.set_0123(result);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else{
                        oil.set_0123(null);
                    }
                    break;
                case "412C":
                case "012C":
                    if (length7(readMessage)!=null) {
                        result = length7(readMessage) * 100 / 255;
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }
                    break;
                case "412D":
                case "012D":
                    if (length7(readMessage)!=null) {
                        result = (length7(readMessage) - 128) * 100 / 128;
                        engine.set_012D(result);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else{
                        engine.set_012D(null);
                    }
                    break;
                case "412E":
                case "012E":
                    if (length7(readMessage)!=null) {
                        result = length7(readMessage) * 100 / 255;
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }
                    break;
                case "412F":
                case "012F":
                    if (length7(readMessage)!=null) {
                        result = length7(readMessage) * 100 / 255;
                        oilLevel = result;
                        oil.set_012F(result);
                        mCallback.onArticleSelected(speed, rmp, gas, water, fuelPressure, oilLevel, temperature, faultCode);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else{
                        oil.set_012F(null);
                    }
                    break;
                case "4130":
                case "0130":
                    if (length7(readMessage)!=null) {
                        result = length7(readMessage);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }
                    break;
                case "4131":
                case "0131":
                    if (length7(readMessage)!=null&&length9(readMessage)!=null) {
                        result = length7(readMessage) * 256 + length9(readMessage);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }
                    break;
                case "4133":
                case "0133":
                    if (length7(readMessage)!=null) {
                        result = length7(readMessage);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }
                    break;
                case "413C":
                case "013C":
                    if (length7(readMessage)!=null&&length9(readMessage)!=null) {
                        result = (length7(readMessage) * 256 + length9(readMessage)) / 10 - 40;
                        sensorZ.set_013C(result);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else{
                        sensorZ.set_013C(null);
                    }
                    break;
                case "413D":
                case "013D":
                    if (length7(readMessage)!=null&&length9(readMessage)!=null) {
                        result = (length7(readMessage) * 256 + length9(readMessage)) / 10 - 40;
                        sensorZ.set_013D(result);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else{
                        sensorZ.set_013D(null);
                    }
                    break;
                case "413E":
                case "013E":
                    if (length7(readMessage)!=null&&length9(readMessage)!=null) {
                        result = (length7(readMessage) * 256 + length9(readMessage)) / 10 - 40;
                        sensorZ.set_013E(result);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else{
                        sensorZ.set_013E(null);
                    }
                    break;
                case "413F":
                case "013F":
                    if (length7(readMessage)!=null&&length9(readMessage)!=null) {
                        result = (length7(readMessage) * 256 + length9(readMessage)) / 10 - 40;
                        sensorZ.set_013F(result);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else{
                        sensorZ.set_013F(null);
                    }
                    break;
                case "4142":
                case "0142":
                    if (length7(readMessage)!=null&&length9(readMessage)!=null) {
                        result = (length7(readMessage) * 256 + length9(readMessage)) / 1000;
                        engine.set_0142(result);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else{
                        engine.set_0142(null);
                    }
                    break;
                case "4143":
                case "0143":
                    if (length7(readMessage)!=null&&length9(readMessage)!=null) {
                        result = (length7(readMessage) * 256 + length9(readMessage)) * 100 / 255;
                        engine.set_0143(result);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else{
                        engine.set_0143(null);
                    }
                    break;
                case "4144":
                case "0144":
                    if (length7(readMessage)!=null&&length9(readMessage)!=null) {
                        result = (length7(readMessage) * 256 + length9(readMessage)) / 32765;
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }
                    break;
                case "4145":
                case "0145":
                    if (length7(readMessage)!=null) {
                        result = length7(readMessage) * 100 / 255;
                        sensorZ.set_0145(result);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else{
                        sensorZ.set_0145(null);
                    }
                    break;
                case "4146":
                case "0146":
                    if (length7(readMessage)!=null) {
                        result = length7(readMessage) - 40;
                        temperature = result;
                        sensorZ.set_0146(result);
                        mCallback.onArticleSelected(speed, rmp, gas, water, fuelPressure, oilLevel, temperature, faultCode);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else{
                        sensorZ.set_0146(null);
                    }
                    break;
                case "4147":
                case "0147":
                    if (length7(readMessage)!=null) {
                        result = length7(readMessage) * 100 / 255;
                        sensorZ.set_0147(result);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else{
                        sensorZ.set_0147(null);
                    }
                    break;
                case "4148":
                case "0148":
                    if (length7(readMessage)!=null) {
                        result = length7(readMessage) * 100 / 255;
                        sensorZ.set_0148(result);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else{
                        sensorZ.set_0148(null);
                    }
                    break;
                case "4149":
                case "0149":
                    if (length7(readMessage)!=null) {
                        result = length7(readMessage) * 100 / 255;
                        sensorZ.set_0149(result);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else{
                        sensorZ.set_0149(null);
                    }
                    break;
                case "414A":
                case "014A":
                    if (length7(readMessage)!=null) {
                        result = length7(readMessage) * 100 / 255;
                        sensorZ.set_014A(result);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else{
                        sensorZ.set_014A(null);
                    }
                    break;
                case "414B":
                case "014B":
                    if (length7(readMessage)!=null) {
                        result = length7(readMessage) * 100 / 255;
                        sensorZ.set_014B(result);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else {
                        sensorZ.set_014B(null);
                    }
                    break;
                case "414C":
                case "014C":
                    if (length7(readMessage)!=null) {
                        result = length7(readMessage) * 100 / 255;
                        oil.set_014C(result);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else{
                        oil.set_014C(null);
                    }
                    break;
                case "414D":
                case "014D":
                    if (length7(readMessage)!=null&&length9(readMessage)!=null) {
                        result = length7(readMessage) * 256 + length9(readMessage);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }
                    break;
                case "414E":
                case "014E":
                    if (length7(readMessage)!=null&&length9(readMessage)!=null) {
                        result = length7(readMessage) * 256 + length9(readMessage);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }
                    break;
                case "4152":
                case "0152":
                    if (length7(readMessage)!=null) {
                        result = length7(readMessage) * 100 / 255;
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }
                    break;
                case "4153":
                case "0153":
                    if (length7(readMessage)!=null&&length9(readMessage)!=null) {
                        result = (length7(readMessage) * 256 + length9(readMessage)) / 200;
                        sensorZ.set_0153(result);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else{
                        sensorZ.set_0153(null);
                    }
                    break;
                case "4154":
                case "0154":
                    if (length7(readMessage)!=null&&length9(readMessage)!=null) {
                        result = (length7(readMessage) * 256 + length9(readMessage)) - 32767;
                        sensorZ.set_0154(result);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else{
                        sensorZ.set_0154(null);
                    }
                    break;
                case "4159":
                case "0159":
                    if (length7(readMessage)!=null&&length9(readMessage)!=null) {
                        result = (length7(readMessage) * 256 + length9(readMessage)) * 10;
                        oil.set_0159(result);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else{
                        oil.set_0159(null);
                    }
                    break;
                case "415A":
                case "015A":
                    if (length7(readMessage)!=null) {
                        result = length7(readMessage) * 100 / 255;
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }
                    break;
                case "415B":
                case "015B":
                    if (length7(readMessage)!=null) {
                        result = length7(readMessage) * 100 / 255;
                        Log.i("015B", result + "");
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }
                    break;
                case "415C":
                case "015C":
                    if (length7(readMessage)!=null) {
                        result = length7(readMessage) - 40;
                        oil.set_015C(result);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else{
                        oil.set_015C(null);
                    }
                    break;

                case "015E":
                    Log.i("415E",readMessage);
                    Integer l7=Integer.valueOf((readMessage.substring(readMessage.length()-5,readMessage.length()-3)).toString(),16);
                    Integer l9=Integer.valueOf((readMessage.substring(readMessage.length()-2,readMessage.length())).toString(),16);

                    result= (int) ((l7*256+l9)*0.05);
                    oil.set_015E(result);
                    Log.i("415E",result+"");
                    mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    break;
                case "4161":
                case "0161":
                    if (length7(readMessage)!=null) {
                        result = length7(readMessage) - 125;
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }
                    break;
                case "4162":
                case "0162":
                    if (length7(readMessage)!=null) {
                        result = length7(readMessage) - 125;
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }
                    break;
                case "4163":
                case "0163":
                    if (length7(readMessage)!=null&&length9(readMessage)!=null) {
                        result = length7(readMessage) * 256 + length9(readMessage);
                        engine.set_0163(result);
                        mMsgHandler.sendEmptyMessage(MESSAGE_RESULT);
                    }else{
                        engine.set_0163(null);
                    }
                    break;
                default:
                    mMsgHandler.sendEmptyMessage(MESSAGE_NO_DATA);
                    break;
            }
        }else {
//           Toast.makeText(OBD2MonitorMainActivity.this, "error", Toast.LENGTH_SHORT).show();
        }
    }



    Map<String, String> getDict(int keyId, int valId) {
        String[] keys = getResources().getStringArray(keyId);
        String[] vals = getResources().getStringArray(valId);

        Map<String, String> dict = new HashMap<String, String>();
        for (int i = 0, l = keys.length; i < l; i++) {
            dict.put(keys[i], vals[i]);
        }

        return dict;
    }

    protected String performCalute(String result) {
        String workingData;
        String dtc = "";
        int startIndex = 0;
        if (result.length() % 4 == 0) {
            workingData = result;
            startIndex = 4;
        } else if (result.contains(":")) {
            workingData = result.replaceAll("[\r\n].:", "");
            startIndex = 7;
        } else {
            workingData = result.replaceAll("[\r\n]?43", "");
        }
        for (int begin = startIndex; begin < workingData.length(); begin += 4) {

            byte b1 = hexStringToByteArray(workingData.charAt(begin));
            int ch1 = ((b1 & 0xC0) >> 6);
            int ch2 = ((b1 & 0x30) >> 4);
            dtc += dtcLetters[ch1];
            dtc += hexArray[ch2];
            dtc += workingData.substring(begin+1, begin + 4);
            if (dtc.equals("P0000")) {
                dtc="0";
            }
//            codes.append(dtc);
//            codes.append('\n');
        }
        return dtc.substring(0,4);

    }

    private byte hexStringToByteArray(char s) {
        return (byte) ((Character.digit(s, 16) << 4));
    }


    //A B
    private Integer length9(String readMessage) {
        Log.i("readMessage", readMessage);
        if (readMessage.length() >= 11) {

            return Integer.valueOf((readMessage.substring(9, 11)).toString(), 16);
        }
        return null;
    }

    //A
    private Integer length7(String readMessage){
        if (readMessage.length()>=8){
            return Integer.valueOf((readMessage.substring(6, 8)).toString(), 16);
        }
        return null;

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    protected WeakReference<View> mRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mRootView==null||mRootView.get()==null) {

            view = inflater.inflate(R.layout.activity_obd2_monitor_main, container, false);
            mRootView=new WeakReference<View>(view);
        }else{
            ViewGroup parent= (ViewGroup) mRootView.get().getParent();
            if (parent!=null){
                parent.removeView(mRootView.get());
            }
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null){
            Toast.makeText(getContext(), R.string.bt_not_available,
                    Toast.LENGTH_LONG).show();
            getActivity().finish();
        }

        mHttpClient = new OkHttpClient();
        engine=new Engine();
        oil=new Oil();
        sensorZ=new zeolite.com.obd1.entity.upload.Sensor();
        uploadData=new UploadData();
        data=new Data();

        resultText=(TextView)view.findViewById(R.id.result_text);

//        mConnectedStatusTxt  = (TextView)view.findViewById(R.id.connected_status_text);
        mResponseMessageTxt  = (TextView)view.findViewById(R.id.response_msg_text);
        mResponseMessageTxt.setMovementMethod(ScrollingMovementMethod.getInstance());
        mSupportedPidsTxt = (TextView)view.findViewById(R.id.supported_pids_text);
        mSupportedPidsTxt.setMovementMethod(ScrollingMovementMethod.getInstance());
        mInputOBD2CMDEditTxt = (EditText)view.findViewById(R.id.input_cmd_edit);

        mSelectBtDevicesBtn  = (Button)view.findViewById(R.id.select_device_btn);
        mSelectBtDevicesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonOnClick(view);
            }
        });


        String[] mItems = getResources().getStringArray(R.array.ATandOBD2Commands);
        // 建立Adapter并且绑定数据源
        ArrayAdapter<String> _Adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, mItems);
        //绑定 Adapter到控件



        mCmdAndRes = new StringBuilder();
        autoRes = AUTO_RES.AUTO_RES_NONE;

        return mRootView.get();
    }



    // 当sensor事件发生时候调用
    public void onSensorChanged(SensorEvent event){

        // check sensor type
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){

            // assign directions
            float x=event.values[0];
            float y=event.values[1];
            float z=event.values[2];

            Log.i("sensor",x+","+y+","+z);
        }
    }



    private void buttonOnClick(View v){
        switch (v.getId()){
            case R.id.send_cmd_btn:
                sendOBD2CMD();
//                sendOBD2CMDTimer();


                break;
            case R.id.select_device_btn:
                selectDevice();
                break;
            case R.id.disconnect_device_btn:
                disconncetDevice();
                break;

            case R.id.send_timer_btn:

                timer=new Timer();
                task = new TimerTask() {
                    @Override
                    public void run() {
                        mMsgHandler.sendEmptyMessage(MESSAGE_TIMER);
                    }
                };
                timer.schedule(task, 1000, 1000);

                break;

            case R.id.timer_cancel_btn:
                timer.cancel();
                break;


            default:
                break;
        }
    }

    private void setupOBDMonitor(){
        mSendOBD2CMDBtn = (Button)view.findViewById(R.id.send_cmd_btn);
        mSendOBD2CMDBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonOnClick(view);
            }
        });

        mTimerSendCMDBtn=(Button)view.findViewById(R.id.send_timer_btn);
        mTimerSendCMDBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonOnClick(view);
            }
        });

        mCancelTimerBtn=(Button)view.findViewById(R.id.timer_cancel_btn);
        mCancelTimerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonOnClick(view);
            }
        });



        mDisconnectDeviceBtn = (Button)view.findViewById(R.id.disconnect_device_btn);
        mDisconnectDeviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonOnClick(view);
            }
        });

        mOBD2MonitorService = new OBD2MonitorService(getContext(), mMsgHandler);
    }

    private void setConnectedStatusTitle(CharSequence title){
//        mConnectedStatusTxt.setText(title);
    }

    private void setConnectedStatusTitle(int resID){
//        mConnectedStatusTxt.setText(resID);
    }

    private void autoResponse(String resMsg){
        try{
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        String res = resMsg.replace("\r", "");
        res = res.trim();
        if(res.equals("atz"))
            sendOBD2CMD("ELM327 1.5 >");
        if(res.equals("atws"))
            sendOBD2CMD("ELM327 1.5 warm start >");
        if(res.equals("ate0"))
            sendOBD2CMD("ok >");
        if(res.equals("atl0"))
            sendOBD2CMD("ok >");
        if(res.equals("atsp0"))
            sendOBD2CMD("ok >");
        if(res.equals("0100")){
            sendOBD2CMD("SEARCHING..." + "41 00 BE 1F A8 13 >");
        }
        if(res.equals("0105")){
            if(autoRes == AUTO_RES.AUTO_RES_OK || autoRes == AUTO_RES.AUTO_RES_NORMAL)
                sendOBD2CMD("41 05 7B  >");
            if(autoRes == AUTO_RES.AUTO_RES_ERROR)
                sendOBD2CMD("CAN ERROR >");
        }
        if(res.equals("010B")){
            if(autoRes == AUTO_RES.AUTO_RES_OK || autoRes == AUTO_RES.AUTO_RES_NORMAL)
                sendOBD2CMD("41 0B 1A >");
            if(autoRes == AUTO_RES.AUTO_RES_ERROR)
                sendOBD2CMD("CAN ERROR >");
        }
        if(res.equals("010C")){
            if(autoRes == AUTO_RES.AUTO_RES_OK )
                sendOBD2CMD("41 0C 1A F8 >");
            if(autoRes == AUTO_RES.AUTO_RES_NORMAL)
                sendOBD2CMD("NO DATA >");
            if(autoRes == AUTO_RES.AUTO_RES_ERROR)
                sendOBD2CMD("CAN ERROR >");
        }
        if(res.equals("0101")){
            if(autoRes == AUTO_RES.AUTO_RES_OK || autoRes == AUTO_RES.AUTO_RES_NORMAL)
                sendOBD2CMD("41 01 82 07 65 04 >");
            if(autoRes == AUTO_RES.AUTO_RES_ERROR)
                sendOBD2CMD("CAN ERROR >");
        }
        if(res.equals("03")){
            if(autoRes == AUTO_RES.AUTO_RES_OK || autoRes == AUTO_RES.AUTO_RES_NORMAL)
                sendOBD2CMD("43 00 43 01 33 00 00 >");
            if(autoRes == AUTO_RES.AUTO_RES_ERROR)
                sendOBD2CMD("CAN ERROR >");
        }
    }
    private void setPidsSupported(String buffer){
        byte[] pidSupported = null;
        StringBuilder flags = new StringBuilder();
        String buf = buffer.toString();
        buf = buf.trim();
        buf = buf.replace("\t", "");
        buf = buf.replace(" ", "");
        buf = buf.replace(">", "");
        pidSupported = buf.getBytes();
        if(buf.indexOf("4100") == 0){
            for(int i = 0; i < 8; i++ ){
                String tmp = buf.substring(i+4, i+5);
                int data = Integer.valueOf(tmp, 16).intValue();
//                String retStr = Integer.toBinaryString(data);
                if ((data & 0x08) == 0x08){
                    flags.append("1");
                }else{
                    flags.append( "0");
                }

                if ((data  & 0x04) == 0x04){
                    flags.append("1");
                }else{
                    flags.append( "0");
                }

                if ((data  & 0x02) == 0x02){
                    flags.append("1");
                }else{
                    flags.append( "0");
                }

                if ((data  & 0x01) == 0x01){
                    flags.append("1");
                }else{
                    flags.append( "0");
                }
            }

            StringBuilder supportedPID = new StringBuilder();
            supportedPID.append("支持PID: ");
            StringBuilder unSupportedPID = new StringBuilder();
            unSupportedPID.append("不支持PID: ");
            for(int j = 0; j < flags.length(); j++){
                if(flags.charAt(j) == '1'){
                    supportedPID.append(" "+ PIDS[j] + " ");
                }else{
                    unSupportedPID.append(" "+ PIDS[j] + " ");
                }
            }
            supportedPID.append("\n");

            mSupportedPidsTxt.setText(supportedPID.toString() + unSupportedPID.toString());
        }else{
            return;
        }
    }

    private void sendOBD2CMD(){
        if(mOBD2MonitorService.getState() != OBD2MonitorService.STATE_CONNECTED){
            Toast.makeText(getContext(),R.string.bt_not_available,
                    Toast.LENGTH_LONG).show();
        }
        String strCMD = mInputOBD2CMDEditTxt.getText().toString();
        if(strCMD.equals("")){
            Toast.makeText(getContext(),R.string.please_input_cmd,
                    Toast.LENGTH_LONG).show();
            return;
        }
        strCMD += '\r';
        mCmdAndRes.append(" > Send: "+ strCMD);
        mCmdAndRes.append('\n');
        mResponseMessageTxt.setText(mCmdAndRes.toString());
        byte[] byteCMD = strCMD.getBytes();
        mOBD2MonitorService.write(byteCMD);
//        writeOBD2MonitorLog("Send: "+ strCMD);
    }

    private void sendOBD2CMDTimer(){


//        writeOBD2MonitorLog("Send: "+ strCMD);
    }

    private void sendOBD2CMD(String sendMsg){
        if(mOBD2MonitorService.getState() != OBD2MonitorService.STATE_CONNECTED){
            Toast.makeText(getContext(),R.string.bt_not_available,
                    Toast.LENGTH_LONG).show();
        }
        String strCMD = sendMsg;
        strCMD += '\r';
        mCmdAndRes.append(" > Send: "+ strCMD);
        mCmdAndRes.append('\n');
        mResponseMessageTxt.setText(mCmdAndRes.toString());
        byte[] byteCMD = strCMD.getBytes();
        mOBD2MonitorService.write(byteCMD);
//        writeOBD2MonitorLog("Send: "+ strCMD);
    }

    private void selectDevice(){
        Intent devicesIntent = new Intent(getActivity(), OBD2MonitorDevicesActivity.class);
        startActivityForResult(devicesIntent, REQUEST_CONNECT_DEVICE_SECURE);
    }

    private void connectDevice(Intent data, boolean secure){
        // get bluetooth mac address
        String address = data.getExtras().getString(OBD2MonitorDevicesActivity.EXTRA_DEVICE_ADDRESS);
        // Get the bluetooth Device object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mOBD2MonitorService.connect(device, secure);
    }

    private void disconncetDevice(){
        if(mOBD2MonitorService != null){
            mOBD2MonitorService.stop();
        }
    }

    // Create LogFile
    public static void writeOBD2MonitorLog(String content) {
        try {
            File rooDir = new File(Environment.getExternalStorageDirectory(), DIR_NAME_OBD2_MONITOR);
            if (!rooDir.exists()) {
                rooDir.mkdirs();
            }
            File logFile = new File(rooDir, FILE_NAME_OBD2_MONITOR_LOG);
            if(!logFile.exists()){
                logFile.createNewFile();
            }
            if(logFile.canWrite()){
                SimpleDateFormat stime = new SimpleDateFormat(
                        "yyyy-MM-dd hh:mm:ss ");
                RandomAccessFile Dfile = new RandomAccessFile(logFile, "rw");
                String contents = stime.format("==" + new Date()) + "->" + content
                        + "\r\n";
                Dfile.seek(Dfile.length());
                Dfile.write(contents.getBytes("UTF-8"));
                Dfile.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    @Override
    public void onActivityResult(int requstCode, int resultCode, Intent data){
        switch(requstCode){
            case REQUEST_CONNECT_DEVICE_SECURE:
                if(resultCode == Activity.RESULT_OK){
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                if(resultCode == Activity.RESULT_OK){
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                if(resultCode == Activity.RESULT_OK){
                    setupOBDMonitor();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (mOBD2MonitorService == null)
                setupOBDMonitor();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public synchronized void onResume(){
        super.onResume();
        if (mOBD2MonitorService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mOBD2MonitorService.getState() == OBD2MonitorService.STATE_NONE) {
                // Start the Bluetooth chat services
                mOBD2MonitorService.start();
            }
        }
        if(mCmdAndRes.length() > 0){
            mCmdAndRes.delete(0, mCmdAndRes.length());
        }
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mOBD2MonitorService != null){
            mOBD2MonitorService.stop();
        }
        if(mCmdAndRes.length() > 0){
            mCmdAndRes.delete(0, mCmdAndRes.length());
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.activity_obd2_monitor_main, menu);
        mItemSetting = menu.findItem(R.id.menu_settings);
        mItemQuit = menu.findItem(R.id.menu_quit);
        mItemHelp = menu.findItem(R.id.menu_help);
        mItemAutoResOK = menu.findItem(R.id.menu_res_ok);
        mItemAutoResNormal = menu.findItem(R.id.menu_res_normal);
        mItemAutoResError = menu.findItem(R.id.menu_res_error);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_settings:
                Toast.makeText(getContext(), "Menu_Setting_Clicked", Toast.LENGTH_LONG).show();
                break;
            case R.id.menu_quit:
                getActivity().finish();
                break;
            case R.id.menu_help:
                Intent helpIntent = new Intent();
                helpIntent.setClass(getActivity(),OBD2MonitorHelpActivity.class);
                startActivity(helpIntent);
                break;
            case R.id.menu_res_ok:
                autoRes = AUTO_RES.AUTO_RES_OK;
                break;
            case R.id.menu_res_normal:
                autoRes = AUTO_RES.AUTO_RES_NORMAL;
                break;
            case R.id.menu_res_error:
                autoRes = AUTO_RES.AUTO_RES_ERROR;
                break;
        }
//        return super.onOptionsItemSelected(item);
        return true;
    }


    //fragment to activity

    OnHeadlineSelectedListener mCallback;


    private static int rmp=0;
    private static int speed=0;
    private static int gas=0;
    private static int water=0;
    private static int fuelPressure=0;
    private static int oilLevel=0;
    private static int temperature =0;
    private static String faultCode=null;

//    "010C","010D","010B","0105","010A","012F","0146"

    public interface OnHeadlineSelectedListener {
        public void onArticleSelected(int speed, int rmp,  int gas, int water,int fuelPressure,int oilLevel,int temperature,String faultCode);
    }
}
class MyThread1 extends Thread
{
    private String url;
    private String jsonString;
    private ConnectFragment mConnectFragment;
    public MyThread1(String url , String jsonString , ConnectFragment mConnectFragment){
        this.url = url;
        this.jsonString = jsonString;
        this.mConnectFragment = mConnectFragment;
    }
    public void run(){
        try {
            mConnectFragment.dopost(this.url, this.jsonString);
            sleep(300000);
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}

