package alex.task_manager.models;

import android.database.Cursor;
import android.util.Log;

import java.sql.Timestamp;

import alex.task_manager.utils.TimestampUtils;

import static alex.task_manager.utils.TimestampUtils.timestampToString;

public class TaskModel {
    private int _id;
    private int author_id;
    private String name;
    private String about;
    private Timestamp deadline, notificationTime, lastChangeTime;
    private boolean checked;
    private boolean deleted;

    public static final class Builder extends DbModelBuilder<TaskModel> {

        @Override
        protected TaskModel mapper(Cursor cursor) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
            int authorId = cursor.getInt(cursor.getColumnIndexOrThrow("author_id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String about = cursor.getString(cursor.getColumnIndexOrThrow("about"));
            boolean complited = toBoolean(cursor.getInt(cursor.getColumnIndexOrThrow("completed")));
            boolean deleted = toBoolean(cursor.getInt(cursor.getColumnIndexOrThrow("deleted")));
            Timestamp deadline = TimestampUtils.stringToTimestamp(cursor.getString(cursor.getColumnIndexOrThrow("deadline")));
            Timestamp notificationTime = TimestampUtils.stringToTimestamp(cursor.getString(cursor.getColumnIndexOrThrow("notification_time")));

            Timestamp lastChangeTime = TimestampUtils.stringToTimestamp(cursor.getString(cursor.getColumnIndexOrThrow("last_update_time")));

            return new TaskModel(id, authorId, name, about, complited, deleted, deadline, notificationTime, lastChangeTime);
        }
    }

    public TaskModel(
            int id,
            int author_id,
            String caption,
            String about,
            boolean checked,
            boolean deleted,
            Timestamp deadline,
            Timestamp notificationTime,
            Timestamp lastChangeTime
    ) {
        this._id = id;
        this.author_id = author_id;
        this.name = caption;
        this.about = about;
        this.checked = checked;
        this.deleted = deleted;
        this.deadline = deadline;
        this.notificationTime = notificationTime;
        this.lastChangeTime = lastChangeTime;
    }

    public TaskModel(int author_id, String caption, String about, Timestamp deadline, Timestamp notificationTime) {
        this.author_id = author_id;
        this.name = caption;
        this.about = about;
        this.checked = false;
        this.deleted = false;
        this.deadline = deadline;
        this.notificationTime = notificationTime;
    }

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        this._id = id;
    }

    public int getAuthorId() {
        return author_id;
    }

    public void setAuthorId(int id) {
        this.author_id = id;
    }

    public String getName() {
        return name;
    }

    public void getName(String caption) {
        this.name = caption;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Timestamp getDeadline() {
        return deadline;
    }

    public String getStringDeadline() {
        return TimestampUtils.timestampToString(deadline, TimestampUtils.FULL_DATE_FORMAT);
    }

    public void setDeadline(Timestamp time) {
        this.deadline = time;
    }

    public void setNotificationTime(Timestamp notificationTime) {
        this.notificationTime = notificationTime;
    }

    public Timestamp getNotificationTime() {
        return notificationTime;
    }

    public String getStringNotificationTime() {
        return TimestampUtils.timestampToString(notificationTime, TimestampUtils.FULL_DATE_FORMAT);
    }

    public void setLastChangeTime(Timestamp lastChangeTime) {
        this.lastChangeTime = lastChangeTime;
    }

    public Timestamp getLastChangeTime() {
        return lastChangeTime;
    }
}
