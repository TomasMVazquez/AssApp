package com.example.toms.assapp.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.toms.assapp.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {

    public static final int KEY_UIF = 102;

    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        callbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();

        final LoginButton loginButton = findViewById(R.id.login_button_facebook);
        loginButton.setReadPermissions("email", "public_profile");

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case KEY_UIF:
                    FirebaseUser user = mAuth.getCurrentUser();
                    //Volver a la pantalla
                    Intent info = MainActivity.respuestaLogin(user.getDisplayName());
                    setResult(Activity.RESULT_OK,info);
                    finish();
                    break;
            }
        }else {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            Intent intent = new Intent( LogInActivity.this,UifDataActivity.class);
                            startActivityForResult(intent, KEY_UIF);
                        } else {
                            // If sign in fails, display a message to the user.
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }


    public void updateUI(FirebaseUser user){
        if (user != null) {
            String name = user.getDisplayName();
            Uri uri = user.getPhotoUrl();
//            Profile profile = Profile.getCurrentProfile();
//            if (profile != null) {
//                Uri uri2 = profile.getProfilePictureUri(500, 500);
//            collapsingToolbarLayout.setTitle(name);
//            Glide.with(this).load(uri).into(imageView);
//            }
        }
    }
}
