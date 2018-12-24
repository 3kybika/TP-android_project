package alex.myapplication.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.net.HttpURLConnection;

import alex.myapplication.R;
import alex.myapplication.api.RetrofitClient;
import alex.myapplication.models.IdForm;
import alex.myapplication.models.SignUpForm;
import alex.myapplication.models.UserModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextEmail, editTextPassword, editTextName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // seaching for inputs
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextName = findViewById(R.id.editTextName);

        // adding event listeners
        findViewById(R.id.buttonSignUp).setOnClickListener(this);
        findViewById(R.id.textViewLogin).setOnClickListener(this);

        // is already signed in?
        Log.d("Main activity", "try to sign up...");

        Call<UserModel> call = RetrofitClient
                .getInstance()
                .getApi()
                //ToDo real user id
                .getUser();

        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    // Already loggined
                    UserModel dr = response.body();
                    Log.d("Task activity", "Already loggined as:" + dr.getLogin());
                    Toast.makeText(MainActivity.this, "Loggined as" + dr.getLogin(), Toast.LENGTH_LONG).show();

                    // Go to tasks page
                    Intent intent = new Intent(MainActivity.this, TasksActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else if (response.code() == HttpURLConnection.HTTP_NOT_FOUND ||
                        response.code() == HttpURLConnection.HTTP_FORBIDDEN
                ) {
                    // User not found
                    Toast.makeText(MainActivity.this, "User already exist", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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

        // sign up:
        Call<UserModel> call = RetrofitClient
                .getInstance()
                .getApi()
                .signup(new SignUpForm(name, email, password));

        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    // Already loggined
                    UserModel dr = response.body();
                    Log.d("Sign up activity", "Was registerd as:" + dr.getLogin());
                    Toast.makeText(MainActivity.this, "Loggined as" + dr.getLogin(), Toast.LENGTH_LONG).show();

                    // Go to tasks page
                    Intent intent = new Intent(MainActivity.this, TasksActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else if (response.code() == HttpURLConnection.HTTP_NOT_FOUND ||
                        response.code() == HttpURLConnection.HTTP_FORBIDDEN
                        ) {
                    // User not found
                    Toast.makeText(MainActivity.this, "User already exist", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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
        }
    }
}
