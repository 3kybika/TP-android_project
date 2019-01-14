package alex.task_manager.models;

import java.sql.Timestamp;

import alex.task_manager.utils.TimestampUtils;

import static alex.task_manager.utils.TimestampUtils.timestampToString;

public class TaskViewModel {
    private int id;
    private String author;
    private String caption;
    private String about;
    private boolean checked;
    private Timestamp time;

    public TaskViewModel(int id, String author, String caption, String about, boolean checked, Timestamp time) {
        this.id = id;
        this.author = author;
        this.caption = caption;
        this.about = about;
        this.checked = checked;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthorId(String author) {
        this.author = author;
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
