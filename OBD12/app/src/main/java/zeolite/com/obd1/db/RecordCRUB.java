package zeolite.com.obd1.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import zeolite.com.obd1.entity.record.RecordEntity;


/**
 * Created by Zeolite on 16/1/26.
 */
public class RecordCRUB {
    private DBHelper dbHelper;

    public RecordCRUB(Context context){
        this.dbHelper=new DBHelper(context);
    }


//    private String time;
//    private String currentmeil;
//    private String fixtype;
//    private String cost;
//    private String fixitem;
//    private String save;



    public void saveRecord(RecordEntity record){
        SQLiteDatabase db=dbHelper.getWritableDatabase();

        ContentValues contentValues=new ContentValues();
        contentValues.put("time",record.getTime());
        contentValues.put("currentmeil",record.getCurrentmeil());
        contentValues.put("fixtype",record.getFixtype());
        contentValues.put("cost",record.getCost());
        contentValues.put("fixitem",record.getFixitem());
        contentValues.put("save",record.getSave());
        db.insert("record", null, contentValues);
        db.close();
    }

    public void deleteRecord(Integer recordid){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.execSQL("delete from record where recordid=?", new Object[]{recordid.toString()});
    }

    public List<RecordEntity> findAllRecord(){
        SQLiteDatabase db=dbHelper.getReadableDatabase();

        List<RecordEntity> recordEntities=new ArrayList<RecordEntity>();

        Cursor cursor=db.query("record",null,null,null,null,null,null);

        while (cursor.moveToNext()){
            String time=cursor.getString(cursor.getColumnIndex("time"));
            String currentmeil=cursor.getString(cursor.getColumnIndex("currentmeil"));
            String fixtype=cursor.getString(cursor.getColumnIndex("fixtype"));
            String cost=cursor.getString(cursor.getColumnIndex("cost"));
            String fixitem=cursor.getString(cursor.getColumnIndex("fixitem"));
            String save=cursor.getString(cursor.getColumnIndex("save"));
//            Log.i("findAll",time+"--"+currentmeil+"--"+fixtype+"--"+cost+"--"+fixitem+"--"+save);
            RecordEntity recordEntity=new RecordEntity(time,currentmeil,fixtype,cost,fixitem,save);
            recordEntities.add(recordEntity);
        }
        cursor.close();
        db.close();

        return recordEntities;
    }


}
