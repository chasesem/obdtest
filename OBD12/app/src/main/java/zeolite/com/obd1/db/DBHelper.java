package zeolite.com.obd1.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Zeolite on 16/1/26.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASENAME="obd.db";
    private static final int DATABASEVERSION=1;

    public DBHelper(Context context) {
        super(context, DATABASENAME, null, DATABASEVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS record");
        db.execSQL("CREATE TABLE record (recordid integer primary key autoincrement, time varchar(20), " +
                "currentmeil varchar(20)," +
                "fixtype varchar(10)," +
                "cost varchar(20)," +
                "fixitem varchar(50)," +
                "save varchar(5))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS person");
        onCreate(db);
    }
}
