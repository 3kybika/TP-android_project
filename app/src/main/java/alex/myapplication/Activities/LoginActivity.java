package alex.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import alex.myapplication.R;
import alex.myapplication.models.LoginForm;
import alex.myapplication.models.UserModel;
import alex.myapplication.services.NetworkService;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextEmail;
    private EditText editTextPassword;

    private NetworkService networkService = NetworkService.getInstance();

    private NetworkService.OnUserGetListener  userListener = new NetworkService.OnUserGetListener() {
        @Override
        public void onUserSuccess(final UserModel user) {
            //  sign in
            Log.d("Task activity", "loggined as:" + user.getLogin());
            Toast.makeText(LoginActivity.this, "signed in as" + user.getLogin(), Toast.LENGTH_LONG).show();

            // Go to tasks page
            Intent intent = new Intent(LoginActivity.this, TasksActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        @Override
        public void onUserError(final Exception error) {
            //ToDo : network disabled! - offline work
            Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        findViewById(R.id.buttonLogin).setOnClickListener(this);
        findViewById(R.id.textViewRegister).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // ToDo: Must it inspect: are already loggined in?
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
        networkService.signin(new LoginForm(email, password), userListener);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonLogin:
                signin();
                break;
            case R.id.textViewRegister:
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }
}
