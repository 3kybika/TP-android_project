package alex.task_manager.services.DbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import static alex.task_manager.services.DbServices.Mappers.getCookieslList;

public class CookieService extends BaseDbService implements CookieJar {

    private static final CookieService mInstance = new CookieService();

    public static CookieService getInstance(Context context) {
        mInstance.context = context;
        return mInstance;
    }

    public void createDatabase(SQLiteDatabase db) {
        Log.d("Tasks Service", "onCreate database");
        // создаем таблицу с полями
        db.execSQL("CREATE TABLE Cookies (" +
               "name TEXT, " +
               "value TEXT, " +
               "expiresAt INTEGER, " + //long?
               "domain TEXT, " +
               "path TEXT, " +
               "secure TEXT, " +
               "httpOnly TEXT, " +
               "persistent TEXT, " +
               "hostOnly TEXT " +
           ");"
        );
    }

    @Override
    public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS Cookies;");
            createDatabase(db);
        }
    }

    @Override
    public void saveFromResponse(@NonNull HttpUrl url, List<Cookie> cookies) {
        if (cookies != null) {
            checkInitialized();

            SQLiteDatabase database = helper.getWritableDatabase();
            for (Cookie cookie : cookies) {

                ContentValues contentValues = new ContentValues();

                contentValues.put("name",cookie.name());
                contentValues.put("value",cookie.value());
                contentValues.put("expiresAt",cookie.expiresAt());
                contentValues.put("domain",cookie.domain());
                contentValues.put("path",cookie.path());
                contentValues.put("secure",cookie.secure());
                contentValues.put("httpOnly",cookie.httpOnly());
                contentValues.put("persistent",cookie.persistent());
                contentValues.put("hostOnly",cookie.hostOnly());

                database.insert("Cookies", null, contentValues);
            }
            database.close();
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        checkInitialized();

        String selectQuery ="SELECT * FROM Cookies";
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        return getCookieslList(cursor);
    }
}
