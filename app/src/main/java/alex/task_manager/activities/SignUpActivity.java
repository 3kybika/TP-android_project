package alex.task_manager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import alex.task_manager.R;
import alex.task_manager.models.DefaultResponse;
import alex.task_manager.models.UserModel;
import alex.task_manager.requests.SignUpForm;
import alex.task_manager.services.DbServices.CookieService;
import alex.task_manager.services.DbServices.UserDbService;
import alex.task_manager.services.NetworkServices.UserNetworkService;


public class SignUpActivity extends BaseAuthenticationActivity implements View.OnClickListener {

    private EditText editTextEmail, editTextPassword, editTextName;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // seaching for inputs
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextName = findViewById(R.id.editTextName);

        // adding event listeners
        findViewById(R.id.buttonSignUp).setOnClickListener(this);
        findViewById(R.id.textViewLogin).setOnClickListener(this);

        userNetworkService = UserNetworkService.getInstance(this.getApplicationContext());
        userDbService = UserDbService.getInstance(this.getApplicationContext());
        cookieService = CookieService.getInstance(this.getApplicationContext());
    }

    private void signUp() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String name = editTextName.getText().toString().trim();

        //ToDo errors must replaced to resource string, not hardcode!
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

        if (name.isEmpty()) {
            editTextName.setError("Name required");
            editTextName.requestFocus();
            return;
        }

        userNetworkService.signup(new SignUpForm(name, email, password), authentificateListener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSignUp:
                signUp();
                break;
            case R.id.textViewLogin:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            default:
                hideKeyboard(view);
        }
    }

    @Override
    protected void changeToMainActivity(){
        // Go to tasks page
        Intent intent = new Intent(SignUpActivity.this, TasksActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void showErrorAboutUnavailable(Exception exception){
        Toast.makeText(
                SignUpActivity.this, getResources().getText(R.string.SignUpActivity__err__unavaibleErr),
                Toast.LENGTH_LONG
        ).show();
    }

    @Override
    protected void showMsgAboutOfflineStage(Exception exception) {
        UserModel user = userDbService.getCurrentUser();

        Toast.makeText(
                SignUpActivity.this,
                String.format(
                        getResources().getText(R.string.SignUpActivity__infoMsg__offlineMode) +
                        user.getLogin()
                 ),
                Toast.LENGTH_LONG
        ).show();
    }

    @Override
    protected void showErrorAboutUncorrectValues(DefaultResponse responce) {
        Toast.makeText(
                SignUpActivity.this,
                getResources().getText(R.string.SignUpActivity__infoMsg__offlineMode),
                Toast.LENGTH_LONG
        ).show();
    }

    @Override
    protected void showSuccessMessage(UserModel user) {
        Toast.makeText(
                SignUpActivity.this,
                String.format(
                        getResources().getText(R.string.SignUpActivity__infoMsg__successLoggined) +
                        user.getLogin()
                ),
                Toast.LENGTH_LONG
        ).show();
    }
}