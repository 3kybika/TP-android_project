package alex.task_manager.services.DbServices;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class BaseDbService {

    protected static final int VERSION = 1;
    protected static final String DB_NAME = "TasksDatabase.db";

    Context context;
    SQLiteOpenHelper helper;

    void checkInitialized() {
        if (helper != null) {
            return;
        }
        helper = new SQLiteOpenHelper(context, DB_NAME, null, VERSION) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                createDatabase(db);
            }
            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                upgrade(db, oldVersion, newVersion);
            }
        };
    }

    public abstract void createDatabase(SQLiteDatabase db);
    public abstract void upgrade(SQLiteDatabase db, int oldVersion, int newVersion);

}
