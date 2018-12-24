package alex.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.net.HttpURLConnection;

import alex.myapplication.R;
import alex.myapplication.api.RetrofitClient;
import alex.myapplication.models.LoginForm;
import alex.myapplication.models.SignUpForm;
import alex.myapplication.models.UserModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
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
        Call<UserModel> call = RetrofitClient
                .getInstance()
                .getApi()
                .signin(new LoginForm(email, password));

        //ToDo: error's texts hardcode
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    // loggined
                    UserModel dr = response.body();
                    Log.d("Login activity", "Was signed in as:" + dr.getLogin());
                    Toast.makeText(LoginActivity.this, "signed in as" + dr.getLogin(), Toast.LENGTH_LONG).show();

                    // Go to tasks page
                    Intent intent = new Intent(LoginActivity.this, TasksActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else if (response.code() == HttpURLConnection.HTTP_NOT_FOUND ||
                        response.code() == HttpURLConnection.HTTP_FORBIDDEN
                ) {
                    // User not found
                    Toast.makeText(LoginActivity.this, "User not found! Please check login and password", Toast.LENGTH_LONG).show();
                } else if (response.code() == HttpURLConnection.HTTP_BAD_REQUEST) {
                    Toast.makeText(LoginActivity.this, "Some fields are incorrect! Please check login and password fields", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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
