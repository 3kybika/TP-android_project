package alex.task_manager.requests;

public class UserForm {


    private String email;

    private String login;

    private String password;

    public UserForm(
            String login,
            String email,
            String password
    ) {
        this.login = login;
        this.email = email;
        this.password = password ;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
