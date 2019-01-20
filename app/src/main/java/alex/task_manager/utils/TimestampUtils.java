package alex.task_manager.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.TimeZone;

public class TimestampUtils {

   public static String FULL_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
   public static String USER_FRIENDLY_DATE_FORMAT = "dd EEE MMM, yyyy";
   public static String  USER_FRIENDLY_TIME_FORMAT = "h:mm a";
   public static String DATE_FORMAT = "yyyy-MM-dd";

    public static Timestamp stringToTimestamp(String time) {

        if (time == null || time.equals("null")){
            return  null;
        }
        String str = ZonedDateTime.parse(time).format(DateTimeFormatter.ISO_INSTANT);
        return new Timestamp(ZonedDateTime.parse(str).toLocalDateTime().toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    public static String timestampToString(Timestamp timestamp, String format) {
        if (timestamp == null ){
            return  null;
        }
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(timestamp.getTime());
    }

    public static String calendarToString(Calendar calendar, String format) {
        if (calendar == null ){
            return  null;
        }
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(calendar.getTime());
    }

    public static String getNowString(String format ) {
        return TimestampUtils.timestampToString(new Timestamp(System.currentTimeMillis()), format);
    }

    public static Timestamp getNow() {
        return new Timestamp(System.currentTimeMillis());
    }

}
