package alex.task_manager.services.DbServices;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.Timestamp;

import alex.task_manager.utils.TimestampUtils;

public class SyncDbService {

    public static final String USER_TABLE_NAME = "Users";

    Context context;
    DatabaseManager dbManager;
    private static final SyncDbService mInstance = new SyncDbService();

    public static final String
            //table name
            SYNC_TABLE_NAME = "Last_time_update",
    //table columns
    ID_COLUMN = "_id",
            LAST_UPDATE_TIME_COLUMN = "last_update_time";

    public static final int  LAST_UPDATE_TIME_ID = 0;

    public static SyncDbService getInstance(Context context) {
        mInstance.context = context;
        mInstance.dbManager = DatabaseManager.getInstance(context);
        return mInstance;
    }

    public static void createDatabase(SQLiteDatabase db) {
        Log.d("Sync Service", "onCreate database");
        // создаем таблицу с полями
        db.execSQL("CREATE TABLE " + SYNC_TABLE_NAME + " (" +
                ID_COLUMN + " INTEGER PRIMARY KEY," +
                LAST_UPDATE_TIME_COLUMN + " TIMESTAMP NOT NULL" +
                ")"
        );
        db.execSQL(String.format(
                "INSERT INTO " + SYNC_TABLE_NAME + " (" +
                        ID_COLUMN +  ", " +
                        LAST_UPDATE_TIME_COLUMN +
                        ") VALUES(%d, %s);",
                LAST_UPDATE_TIME_ID,
                TimestampUtils.getNowString(TimestampUtils.FULL_DATE_FORMAT)
        ));
    }

    public static void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + SYNC_TABLE_NAME + ";");
            createDatabase(db);
        }
    }

    public void setLastSyncTime(Timestamp time) {
        SQLiteDatabase database = dbManager.getWritableDatabase();
        database.execSQL(String.format("UPDATE " + SYNC_TABLE_NAME +
                        " SET " + LAST_UPDATE_TIME_COLUMN + " = %d " +
                        " WHERE _id=" + LAST_UPDATE_TIME_ID + ";",
                TimestampUtils.timestampToString(time,TimestampUtils.FULL_DATE_FORMAT))
        );
    }

    public Timestamp getLastSyncTime() {
        SQLiteDatabase database = dbManager.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT " + LAST_UPDATE_TIME_COLUMN +
                        " FROM " + SYNC_TABLE_NAME +
                        " WHERE " + LAST_UPDATE_TIME_ID + " = " + LAST_UPDATE_TIME_ID + ";",
                null);

        return (new Mappers.TimestampMapper()).buildOneInstance(cursor);
    }

}
