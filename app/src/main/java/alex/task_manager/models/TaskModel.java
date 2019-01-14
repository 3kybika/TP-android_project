package alex.task_manager.models;

import java.sql.Timestamp;

import alex.task_manager.utils.TimestampUtils;

import static alex.task_manager.utils.TimestampUtils.timestampToString;

public class TaskModel {
    private int _id;
    private int author_id;
    private String caption;
    private String about;

    private Timestamp time;
    private boolean checked;

    public TaskModel(int id, int author_id, String caption, String about, boolean checked, Timestamp time) {
        this._id = id;
        this.author_id = author_id;
        this.caption = caption;
        this.about = about;
        this.checked = checked;
        this.time = time;
    }

    public TaskModel(int author_id, String caption, String about, Timestamp time) {
        this.author_id = author_id;
        this.caption = caption;
        this.about = about;
        this.checked = false;
        this.time = time;
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

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
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

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getStringTime() {
        return timestampToString(this.time, TimestampUtils.FULL_DATE_FORMAT);
    }
}
