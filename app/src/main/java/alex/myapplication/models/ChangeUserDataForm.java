package alex.myapplication.models;

public class ChangeUserDataForm {
    private String newEmail;
    private String newLogin;
    private String password;
    private String newPassword;

    public ChangeUserDataForm(
          String newLogin,
          String newEmail,
          String password,
          String newPassword
    )

    {
        this.newLogin = newLogin;
        this.newEmail = newEmail;
        this.password = password ;
        this.newPassword = newPassword;
    }

    public String getLogin() {
        return newLogin;
    }

    public void setLogin(String login) {
        this.newLogin = login;
    }


    public String getEmail() {
        return newEmail;
    }

    public void setEmail(String email) {
        this.newEmail = email;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return getNewPassword();
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
