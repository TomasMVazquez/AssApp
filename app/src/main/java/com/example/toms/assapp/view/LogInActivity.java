package com.example.toms.assapp.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LogInActivity extends AppCompatActivity {

    public static final int KEY_UIF = 102;
    public static final int RC_SIGN_IN = 103;

    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ActionCodeSettings actionCodeSettings;
    private GoogleSignInClient mGoogleSignInClient;

    private EditText etEmailSigIn;
    private EditText etPassSigIn;
    private ImageButton btnEmailSignIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        callbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();

        etEmailSigIn = findViewById(R.id.etEmailSigIn);
        btnEmailSignIn = findViewById(R.id.btnEmailSignIn);
        etPassSigIn = findViewById(R.id.etPassSigIn);

        //google

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        btnGoogleSignIn.setSize(SignInButton.SIZE_STANDARD);
        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        //Facebook
        final LoginButton loginButton = findViewById(R.id.login_button_facebook);
        loginButton.setReadPermissions("email", "public_profile");

        // Callback registration Facebook
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

        //Email registration

        btnEmailSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etEmailSigIn.getText().toString().equals("") && !etPassSigIn.getText().toString().equals("")){
                    Toast.makeText(LogInActivity.this, "Validando cuenta", Toast.LENGTH_SHORT).show();
                    createEmailAccess(etEmailSigIn.getText().toString(),etPassSigIn.getText().toString());
                }
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
                    Intent info = MainActivity.respuestaLogin(user.getDisplayName());
                    setResult(Activity.RESULT_OK,info);
                    finish();
                    break;

                case RC_SIGN_IN:
                    Toast.makeText(LogInActivity.this, "Validando cuenta", Toast.LENGTH_SHORT).show();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    handleSignInResult(task);
                    break;
            }
        }else {
            updateUI(null);
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                updateUI(null);
                            }
                        }
                    });
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            updateUI(null);
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
                            final FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            updateUI(null);
                        }
                    }
                });
    }

    private void createEmailAccess(final String email, final String pass){
        mAuth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            handleEmailAccess(email,pass);
                        }
                    }
                });
    }

    private void handleEmailAccess(String email, String pass){
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            updateUI(null);
                        }
                    }
                });
    }

    public void updateUI(final FirebaseUser user){
        if (user != null) {
            final Intent info = MainActivity.respuestaLogin(user.getDisplayName());
            //Progess dialog
            final ProgressDialog prog= new ProgressDialog(LogInActivity.this);
            prog.setTitle("Por favor espere");
            prog.setMessage("Estamos cargando su perfil");
            prog.setCancelable(false);
            prog.setIndeterminate(true);
            prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            prog.show();

            //User Data
            String email = user.getEmail();
            String phone = user.getPhoneNumber();
            String dataBaseName;
            if (email != null) {
                String mail = email.substring(0, email.indexOf("."));
                dataBaseName = mail;
            }else {
                dataBaseName = phone;
            }

            //Revisar Base De Datos
            DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
            DatabaseReference idProfile = mReference.child(dataBaseName).child(getResources().getString(R.string.uif_reference_child));
            idProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        prog.dismiss();
                        setResult(Activity.RESULT_OK,info);
                        finish();
                    }else {
                        Intent intent = new Intent( LogInActivity.this,UifDataActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(UifDataActivity.KEY_EMAIL,user.getEmail());
                        bundle.putString(UifDataActivity.KEY_PHONE,user.getPhoneNumber());
                        bundle.putString(UifDataActivity.KEY_FULL_NAME,user.getDisplayName());
                        intent.putExtras(bundle);
                        prog.dismiss();
                        startActivityForResult(intent, KEY_UIF);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    prog.dismiss();
                    Toast.makeText(LogInActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    LoginManager.getInstance().logOut();
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                }
            });

        }else {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }
}
