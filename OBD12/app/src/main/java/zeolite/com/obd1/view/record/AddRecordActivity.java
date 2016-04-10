package zeolite.com.obd1.view.record;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import zeolite.com.obd1.MainActivity;
import zeolite.com.obd1.R;
import zeolite.com.obd1.adapter.record.RecordGridAdapter;
import zeolite.com.obd1.db.RecordCRUB;
import zeolite.com.obd1.entity.record.RecordEntity;
import zeolite.com.obd1.view.fragment.RecordFragment;


/**
 * Created by Zeolite on 16/1/21.
 */
public class AddRecordActivity extends Activity implements View.OnClickListener{

    private TextView timeChoiceText;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private RelativeLayout timeChoiceLayout;

    private String selectDate="";
    private String selectTime="";


//    private ArrayList<Drawable> mDrawableList;

    private GridView gridView;

    private EditText mileageEt;
    private EditText costEt;

    private Button saveRecordBtn;

    private RadioGroup radioGroup;
    private RadioButton maintainBtn;
    private RadioButton repairBtn;
    private RadioButton insuranceBtn;


    private RecordGridAdapter recordGridAdapter;

    private String time;
    private String currentmeil;
    private String fixtype;
    private String cost;
    private Map<Integer,String> fixitemMap;
    private String fixitem="";
    private String save;

    private ArrayList<String> nameList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_record_layout);

        AddRecordActivity.this.setTitle(R.string.add_record);

        initView();
        initData();

    }

    private void initData() {
        timeChoiceLayout.setVisibility(View.INVISIBLE);
        timeChoiceText.setOnClickListener(this);
        datePicker.setCalendarViewShown(false);
        timePicker.setIs24HourView(true);

        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date curDate =  new  Date(System.currentTimeMillis());
        timeChoiceText.setText(formatter.format(curDate));

        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int monthOfYear=calendar.get(Calendar.MONTH);
        int dayOfMonth=calendar.get(Calendar.DAY_OF_MONTH);
        datePicker.init(year, monthOfYear, dayOfMonth, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                selectDate = year + "-" + monthOfYear + "-" + dayOfMonth;
            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                selectTime = hourOfDay + ":" + minute;
            }
        });


        maintainBtn.setOnClickListener(this);
        insuranceBtn.setOnClickListener(this);
        repairBtn.setOnClickListener(this);

        maintainBtn.performClick();

        saveRecordBtn.setOnClickListener(this);

        fixitemMap=new HashMap<Integer,String>();


    }

    private void initView() {

        timeChoiceText=(TextView)findViewById(R.id.tv_time_choice);
        datePicker= (DatePicker) findViewById(R.id.datePicker);
        timePicker=(TimePicker)findViewById(R.id.timePicker);
        timeChoiceLayout=(RelativeLayout)findViewById(R.id.time_choice_layout);

        gridView=(GridView)findViewById(R.id.gridView);

        mileageEt =(EditText)findViewById(R.id.ed_mileage);
        costEt=(EditText)findViewById(R.id.ed_cost);

        saveRecordBtn=(Button)findViewById(R.id.save_recode_btn);

        radioGroup=(RadioGroup)findViewById(R.id.radio_group);
        maintainBtn=(RadioButton)findViewById(R.id.cb_maintain);
        insuranceBtn=(RadioButton)findViewById(R.id.cb_insurance);
        repairBtn=(RadioButton)findViewById(R.id.cb_repair);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_time_choice:
                timeChoiceLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.save_recode_btn:

                RecordCRUB recordCRUB =new RecordCRUB(this);
                time=timeChoiceText.getText().toString();
                currentmeil= mileageEt.getText().toString();
                //fixtype
                cost=costEt.getText().toString();
                //fixItemMap
                for (Map.Entry<Integer, String> entry : fixitemMap.entrySet()) {

                    fixitem+=entry.getValue()+"\n";

                }
                save="1";

                RecordEntity recordEntity=new RecordEntity(time,currentmeil,fixtype,cost,fixitem,save);
                Log.i("kjhjkjk", recordEntity.toString());
                recordCRUB.saveRecord(recordEntity);
//                recordCRUB.findAllRecord();

                Intent intent=new Intent(AddRecordActivity.this,RecordFragment.class);
                setResult(RESULT_OK,intent);
                finish();
                break;
            case R.id.cb_maintain:
                fixtype=getResources().getString(R.string.maintain);

                setUpGridView(R.array.maintenance_item);

                Log.i("cb_maintain", "cb_maintain");
                break;
            case R.id.cb_repair:
                fixtype=getResources().getString(R.string.repair);
                setUpGridView(R.array.repair_item);
                Log.i("cb_repair", "cb_repair");
                break;
            case R.id.cb_insurance:
                fixtype=getResources().getString(R.string.insurance);

                setUpGridView(R.array.insurance_item);

                Log.i("cb_insurance", "cb_insurance");
                break;
            case R.id.cb_other:
                fixtype=getResources().getString(R.string.other);
                setUpGridView(R.array.other_item);
                break;

        }
    }

    private void setUpGridView(int item) {
        nameList = new ArrayList<String>();

        final String[] maintainItems = getResources().getStringArray(item);
        for (int i = 0; i < maintainItems.length; i++) {
            nameList.add(maintainItems[i]);
        }
        recordGridAdapter = new RecordGridAdapter(this, nameList);
        gridView.setAdapter(recordGridAdapter);
        recordGridAdapter.notifyDataSetChanged();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                recordGridAdapter.setSeclection(position);
                recordGridAdapter.notifyDataSetChanged();
//                Log.i("positon", position + "@@@");
                fixitemMap.put(position, maintainItems[position]);

            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){
                timeChoiceLayout.setVisibility(View.INVISIBLE);
                if (selectDate.equals("")||selectTime.equals("")){
                }else {
                    timeChoiceText.setText(selectDate + " " + selectTime);
                }

                ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(AddRecordActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }

        return super.onTouchEvent(event);
    }
}
