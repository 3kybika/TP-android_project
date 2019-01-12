package alex.task_manager.services.DbServices;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager {

    protected static final int VERSION = 1;
    protected static final String DB_NAME = "TasksDatabase.db";

    private static DatabaseManager  mInstance = new DatabaseManager();
    Context context;
    SQLiteOpenHelper helper;

    public static DatabaseManager getInstance(Context context) {
        mInstance.context = context;
        checkInitialized(context);
        return mInstance;
    }

    private static void checkInitialized(Context context) {
        if (mInstance.helper != null) {
            return;
        }
        mInstance.helper = new SQLiteOpenHelper(context, DB_NAME, null, VERSION) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                initDatabase(db);
            }
            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                upgrade(db, oldVersion, newVersion);
            }
        };
    }

    private static void initDatabase(SQLiteDatabase db) {
        CookieService.createDatabase(db);
        TasksDbService.createDatabase(db);
        UserDbService.createDatabase(db);
    }

    private static void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        CookieService.upgrade(db, oldVersion, newVersion);
        TasksDbService.upgrade(db, oldVersion, newVersion);
        UserDbService.upgrade(db, oldVersion, newVersion);
    }

    public SQLiteDatabase getWritableDatabase() {
         return helper.getWritableDatabase();
    }

    public SQLiteDatabase getReadableDatabase() {
        return helper.getReadableDatabase();
    }
}
