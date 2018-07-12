package com.safaorhan.reunion.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.safaorhan.reunion.R;

public class LoginActivity extends AppCompatActivity {

    EditText emailEdit;
    EditText passwordEdit;
    Button loginButton;

    boolean isTryingToLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEdit = findViewById(R.id.emailEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTryingToLogin && connected()) {
                    tryToLogIn();
                } else {
                    return;
                }
            }
        });
    }

    private void tryToLogIn() {
        String email = emailEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        if (!TextUtils.isEmpty(email.trim()) && !TextUtils.isEmpty(password.trim())) {
            isTryingToLogin = true;
            FirebaseAuth
                    .getInstance()
                    .signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(LoginActivity.this, ConversationsActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Bad credentials", Toast.LENGTH_SHORT).show();
                            }

                            isTryingToLogin = false;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            isTryingToLogin = false;
                        }
                    });
        } else if (TextUtils.isEmpty(email)) {
            emailEdit.setError("Email required");
        } else {
            passwordEdit.setError("Password required");
        }
    }

    private boolean connected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null
                && networkInfo.isConnected()) {
            return true;
        } else {
            Toast.makeText(this, "Connection problem..  Please control your connection!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
