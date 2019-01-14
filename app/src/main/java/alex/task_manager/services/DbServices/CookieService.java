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

import alex.task_manager.models.DbModelBuilder;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class CookieService implements CookieJar {

    Context context;
    DatabaseManager dbManager;
    private static final CookieService mInstance = new CookieService();

    public static CookieService getInstance(Context context) {
        mInstance.context = context;
        mInstance.dbManager = DatabaseManager.getInstance(context);
        return mInstance;
    }

    public static final class Builder extends DbModelBuilder<Cookie> {

        @Override
        protected Cookie mapper(Cursor cursor) {
            String name = cursor.getString(0); // name,
            String  value = cursor.getString(1); // value,
            Long expiresAt = cursor.getLong(2); // expiresAt,
            String domain = cursor.getString(3); // domain,
            String path = cursor.getString(4); // path,
            Boolean secure = (cursor.getString(5).equals("True")); //secure,
            Boolean httpOnly = (cursor.getString(6).equals("True")); //httpOnly,
            Boolean hostOnly = (cursor.getString(7).equals("True")); //hostOnly,
            Boolean persistent = (cursor.getString(8).equals("True")); //persistent


            Cookie.Builder builder =  new Cookie.Builder();
            builder.name(name); // name,
            builder.value(value); // value,
            builder.expiresAt(expiresAt); // expiresAt
            builder.path(path); // path,

            if (hostOnly) {  //hostonly
                builder.hostOnlyDomain(domain); // domain,
            }
            else {
                builder.domain(domain); // domain,
            }

            return builder.build();
        }
    }

    public static void createDatabase(SQLiteDatabase db) {
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

    public static void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS Cookies;");
            createDatabase(db);
        }
    }

    @Override
    public void saveFromResponse(@NonNull HttpUrl url, List<Cookie> cookies) {
        if (cookies != null) {

            SQLiteDatabase database = dbManager.getWritableDatabase();
            for (Cookie cookie : cookies) {

                Log.d(
                        "CookieService",
                        String.format("cookie name = %s, cookie domain = %s, cookie path = %s",
                                cookie.name(), cookie.domain(), cookie.path()
                        )
                );

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
        Log.d("CookieService", String.format("cookie domain = %s, cookie host = %s", url.topPrivateDomain(), url.host()));
        String selectQuery ="SELECT * FROM Cookies;";
        SQLiteDatabase database = dbManager.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        return (new CookieService.Builder()).buildListInstance(cursor);
    }

    public Boolean hasCookie(String url) {
        String selectQuery ="SELECT COUNT(*) FROM Cookies;";
        SQLiteDatabase database = dbManager.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int count = (new Mappers.IntegerMapper()).buildOneInstance(cursor);

        return (count > 0);
    }
}
