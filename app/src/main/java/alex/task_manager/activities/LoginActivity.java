package alex.task_manager.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import alex.task_manager.R;
import alex.task_manager.models.DefaultResponse;
import alex.task_manager.models.UserModel;
import alex.task_manager.requests.LoginForm;
import alex.task_manager.services.DbServices.CookieService;
import alex.task_manager.services.DbServices.UserDbService;
import alex.task_manager.services.NetworkServices.UserNetworkService;


public class LoginActivity extends BaseAuthenticationActivity implements View.OnClickListener {
    private EditText editTextEmail;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        findViewById(R.id.buttonLogin).setOnClickListener(this);
        findViewById(R.id.textViewRegister).setOnClickListener(this);

        userNetworkService = UserNetworkService.getInstance(this.getApplicationContext());
        userDbService = UserDbService.getInstance(this.getApplicationContext());
        cookieService = CookieService.getInstance(this.getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void signin() {

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password required");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 3) {
            editTextPassword.setError("Password should be atleast 6 character long");
            editTextPassword.requestFocus();
            return;
        }

        // sign in:
        userNetworkService.signin(new LoginForm(email, password), authentificateListener);
    }

    @Override
    protected void changeToMainActivity(){
        // Go to tasks page
        Intent intent = new Intent(LoginActivity.this, TasksActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void showErrorAboutUnavailable(Exception exception){
        Toast.makeText(
                LoginActivity.this, getResources().getText(R.string.LoginActivity__err__unavaibleErr),
                Toast.LENGTH_LONG
        ).show();
    }

    @Override
    protected void showMsgAboutOfflineStage(Exception exception) {
        UserModel user = userDbService.getCurrentUser();

        Toast.makeText(
                LoginActivity.this,
                String.format(
                        getResources().getText(R.string.LoginActivity__infoMsg__offlineMode) +
                                user.getLogin()
                ),
                Toast.LENGTH_LONG
        ).show();
    }

    @Override
    protected void showErrorAboutUncorrectValues(DefaultResponse responce) {
        Toast.makeText(
                LoginActivity.this,
                getResources().getText(R.string.LoginActivity__infoMsg__offlineMode),
                Toast.LENGTH_LONG
        ).show();
    }

    @Override
    protected void showSuccessMessage(UserModel user) {
        Toast.makeText(
                LoginActivity.this,
                String.format(
                        getResources().getText(R.string.LoginActivity__infoMsg__successLoggined) +
                                user.getLogin()
                ),
                Toast.LENGTH_LONG
        ).show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonLogin:
                signin();
                break;
            case R.id.textViewRegister:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
            default:
                hideKeyboard(view);
        }
    }
}
