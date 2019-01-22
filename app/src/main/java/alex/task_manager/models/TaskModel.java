package alex.task_manager.models;

import android.database.Cursor;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import alex.task_manager.services.DbServices.TasksDbService;
import alex.task_manager.utils.TimestampUtils;

public class TaskModel {
    private int _id;
    private int local_id;
    private int author_id;
    private String name;
    private String about;
    private Timestamp deadline, notificationTime, lastChangeTime;
    private boolean checked;
    private boolean deleted;

    public static final class Builder extends DbModelBuilder<TaskModel> {

        @Override
        protected TaskModel mapper(Cursor cursor) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(TasksDbService.GLOBAL_ID_COLUMN));
            int local_id = cursor.getInt(cursor.getColumnIndexOrThrow(TasksDbService.LOCAL_ID_COLUMN));
            int authorId = cursor.getInt(cursor.getColumnIndexOrThrow(TasksDbService.AUTHOR_ID_COLUMN));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(TasksDbService.TASK_NAME_COLUMN));
            String about = cursor.getString(cursor.getColumnIndexOrThrow(TasksDbService.TASK_ABOUT_COLUMN));
            boolean complited = toBoolean(cursor.getInt(cursor.getColumnIndexOrThrow(TasksDbService.COMPLITED_COLUMN)));
            boolean deleted = toBoolean(cursor.getInt(cursor.getColumnIndexOrThrow(TasksDbService.DELETED_COLUMN)));
            Timestamp deadline = TimestampUtils.stringToTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(TasksDbService.DEADLINE_COLUMN)));
            Timestamp notificationTime = TimestampUtils.stringToTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(TasksDbService.NOTIFICATION_TIME_COLUMN)));

            Timestamp lastChangeTime = TimestampUtils.stringToTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(TasksDbService.LAST_UPDATE_TIME_COLUMN)));

            return new TaskModel(id, local_id, authorId, name, about, complited, deleted, deadline, notificationTime, lastChangeTime);
        }
    }

    public TaskModel(
            int id,
            int local_id,
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
        this.local_id = local_id;
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

    public long getFullTime() {
        if (deadline != null) {
            Calendar calendarFull = Calendar.getInstance();
            Calendar calendarTime = Calendar.getInstance();

            calendarFull.setTime(deadline);
            calendarTime.setTime(notificationTime);

            calendarFull.set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY));
            calendarFull.set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE));

            return calendarFull.getTimeInMillis();
        }
        return 0;
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

    public int getLocalId() {
        return local_id;
    }

    public void setLocalId(int local_id) {
        this.local_id = local_id;
    }
}
