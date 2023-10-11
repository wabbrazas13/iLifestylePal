package com.example.ilifestylepal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private TextView tvRegisterHere;
    private EditText etLoginEmail, etLoginPass;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    private TextView tvGoogleSignin;
    private ImageView imgGoogleIcon;

    private GoogleSignInClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Google Sign in Views
        tvGoogleSignin = findViewById(R.id.GoogleSignin);
        imgGoogleIcon = findViewById(R.id.img_GoogleIcon);

        mAuth = FirebaseAuth.getInstance();

        tvRegisterHere = findViewById(R.id.tvRegisterHere);
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPass = findViewById(R.id.etLoginPass);
        btnLogin = findViewById(R.id.btnLogin);
        loadingBar = new ProgressDialog(this);

        tvRegisterHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToRegisterActivity();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllowingUserToLogin();
            }
        });

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        client = GoogleSignIn.getClient(this, options);

        tvGoogleSignin.setOnClickListener(view ->{
            try{
                GoogleSignIn();
            }
            catch(Exception e){
                String title = "Error?";
                String msg = e.getMessage();
                String posButton = "Confirm";
                String negButton = "Cancel";

                DialogBox_Message dialogBox = new DialogBox_Message(getApplicationContext(), title, msg, posButton, negButton);
                dialogBox.setPositiveButtonListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialogBox.displayDialogBox();
            }
        });
        imgGoogleIcon.setOnClickListener(view ->{
            try{
                GoogleSignIn();
            }
            catch(Exception e){
                String title = "Error?";
                String msg = e.getMessage();
                String posButton = "Confirm";
                String negButton = "Cancel";

                DialogBox_Message dialogBox = new DialogBox_Message(getApplicationContext(), title, msg, posButton, negButton);
                dialogBox.setPositiveButtonListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialogBox.displayDialogBox();
            }
        });

    }


    private void GoogleSignIn(){
        Intent intent = client.getSignInIntent();
        startActivityForResult(intent, 1234);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1234){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    SendUserToMainActivity();
//                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                                    startActivity(intent);

                                }else{
                                    Toast.makeText(LoginActivity.this,
                                            task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }catch (ApiException e){
                e.printStackTrace();
            }


        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            if(currentUser.isEmailVerified())
            {
                SendUserToMainActivity();
            }
        }
    }

    private void SendUserToMainActivity() {
        Intent MainIntent = new Intent(LoginActivity.this, MainActivity.class);
        MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MainIntent);
        finish();
    }

    private void SendUserToRegisterActivity(){
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        registerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(registerIntent);
        finish();
    }


    private void AllowingUserToLogin() {
        String email = etLoginEmail.getText().toString();
        String password = etLoginPass.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please write your email...", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password...", Toast.LENGTH_SHORT).show();
        }else{
            loadingBar.setTitle("Logging In");
            loadingBar.setMessage("Please wait...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                if (mAuth.getCurrentUser().isEmailVerified()) {
                                    Toast.makeText(LoginActivity.this, "Login Successful.", Toast.LENGTH_SHORT).show();
                                    SendUserToMainActivity();
                                    loadingBar.dismiss();
                                }
                                else
                                {
                                    mAuth.getCurrentUser().sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(LoginActivity.this, "Account Unverified: Please check your email for verification.", Toast.LENGTH_LONG).show();
                                                        loadingBar.dismiss();
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                        loadingBar.dismiss();
                                                    }
                                                }
                                            });
                                }
                            }else{
                                Toast.makeText(LoginActivity.this, "Error Occurred: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }
}