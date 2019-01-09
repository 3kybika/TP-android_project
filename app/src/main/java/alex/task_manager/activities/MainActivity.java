package alex.task_manager.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import alex.task_manager.R;
import alex.task_manager.models.DefaultResponse;
import alex.task_manager.models.UserModel;
import alex.task_manager.requests.SignUpForm;
import alex.task_manager.services.NetworkServices.UserNetworkService;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextEmail, editTextPassword, editTextName;
    private UserNetworkService networkService = UserNetworkService.getInstance(this);

    private UserNetworkService.OnUserGetListener  userListener = new UserNetworkService.OnUserGetListener() {
        @Override
        public void onUserSuccess(final UserModel user) {
            // Already Loggined or signed up
            Log.d("Task activity", "loggined as:" + user.getLogin());
            Toast.makeText(MainActivity.this, "Loggined as" + user.getLogin(), Toast.LENGTH_LONG).show();

            // Go to tasks page
            Intent intent = new Intent(MainActivity.this, TasksActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        @Override
        public void onUserError(final Exception error) {
            //ToDo : network disabled! - offline work
            Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onForbidden(final DefaultResponse response){
            //ToDo Unhardcode
            Toast.makeText(MainActivity.this, "", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onNotFound(final DefaultResponse response){

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        // ToDo: Must it inspect: are already loggined in?
    }

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
        // ToDo: storage service!
        networkService.getme(userListener);
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

        networkService.signup(new SignUpForm(name, email, password) , userListener);
    }

    private static void hideKeyboard(final View input) {
        final InputMethodManager inputMethodManager = (InputMethodManager) input.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
        }
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
}