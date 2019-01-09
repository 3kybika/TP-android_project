package alex.task_manager.models;

public class TaskViewModel {
    private int id;
    private String author;
    private String caption;
    private String about;
    private boolean checked;

    public TaskViewModel(int id, String author, String caption, String about, boolean checked) {
        this.id = id;
        this.author = author;
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
}
