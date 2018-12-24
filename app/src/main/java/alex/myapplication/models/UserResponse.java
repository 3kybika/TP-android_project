package alex.myapplication.models;

public class UserResponse {
    private int id;
    private String email, login;

    public UserResponse(int id, String email, String login) {
        this.id = id;
        this.email = email;
        this.login = login;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return login;
    }
}

