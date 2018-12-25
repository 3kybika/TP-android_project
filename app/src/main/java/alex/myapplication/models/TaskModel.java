package alex.myapplication.models;

public class TaskModel {
    private int id;
    private int author_id;
    private String caption;
    private String about;
    private boolean checked;

    public TaskModel(int id, int author_id, String caption, String about, boolean checked) {
        this.id = id;
        this.author_id = author_id;
        this.caption = caption;
        this.about = about;
        this.checked = checked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
