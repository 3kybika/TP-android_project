package alex.task_manager.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import alex.task_manager.models.DefaultResponse;
import alex.task_manager.models.UserModel;
import alex.task_manager.services.DbServices.CookieService;
import alex.task_manager.services.DbServices.UserDbService;
import alex.task_manager.services.NetworkServices.UserNetworkService;
import okhttp3.HttpUrl;

public abstract class BaseAuthenticationActivity extends AppCompatActivity {

    protected UserNetworkService userNetworkService;
    protected UserDbService userDbService;
    protected CookieService cookieService;

    protected  UserNetworkService.OnUserGetListener getMeListener = new UserNetworkService.OnUserGetListener() {
        @Override
        public void onUserSuccess(final UserModel user){
            //we successfully loggined in!
            userDbService.setCurrentUser(user);
            changeToMainActivity();
        }

        @Override
        public void onUserError(final Exception error){
            // catched the error: server is not available
            if (cookieService.hasCookie(userNetworkService.BASE_URL)) {
                //cookie exist - go in offline
                showMsgAboutOfflineStage(error);
                changeToMainActivity();
            } else {
                showErrorAboutUnavailable(error);
            }
        }

        public void onForbidden(final DefaultResponse response){
            showErrorAboutUncorrectValues(response);
        }

        public void onNotFound(final DefaultResponse response){
            showErrorAboutUncorrectValues(response);
        }
    };

    protected UserNetworkService.OnUserGetListener  authentificateListener = new UserNetworkService.OnUserGetListener() {
        @Override
        public void onUserSuccess(final UserModel user) {
            userDbService.setCurrentUser(user);
            showSuccessMessage(user);
            changeToMainActivity();
        }

        @Override
        public void onUserError(final Exception error) {
            showErrorAboutUnavailable(error);
        }

        @Override
        public void onForbidden(final DefaultResponse response) {
            showErrorAboutUncorrectValues(response);
        }

        @Override
        public void onNotFound(final DefaultResponse response) {
            showErrorAboutUncorrectValues(response);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userDbService = UserDbService.getInstance(this.getApplicationContext());
        userNetworkService = UserNetworkService.getInstance(this.getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        // if we have cookies and last user record - may be we are already loggine!
        UserModel user =  userDbService.getCurrentUser();
        if ( user!= null && user.getId()!= -1 || cookieService.hasCookie(userNetworkService.BASE_URL)) {
            userNetworkService.getme(getMeListener);
        }
    }

    protected static void hideKeyboard(final View input) {
        final InputMethodManager inputMethodManager = (InputMethodManager) input.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
        }
    }

    protected abstract void changeToMainActivity();
    protected abstract void showErrorAboutUnavailable(Exception exception);
    protected abstract void showMsgAboutOfflineStage(Exception exception);
    protected abstract void showErrorAboutUncorrectValues(DefaultResponse responce);
    protected abstract void showSuccessMessage(UserModel user);

}
