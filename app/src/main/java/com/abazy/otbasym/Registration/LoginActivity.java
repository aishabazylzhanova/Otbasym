package com.abazy.otbasym.Registration;

import static android.content.ContentValues.TAG;


import android.content.Intent;

import android.os.Bundle;

import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

import com.abazy.otbasym.BaseActivity;
import com.abazy.otbasym.LauncherActivity;
import com.abazy.otbasym.R;
import com.abazy.otbasym.SettingsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends BaseActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar bar = getSupportActionBar();
        View treesBar = getLayoutInflater().inflate(R.layout.trees_bar_for_home, null);
        treesBar.findViewById(R.id.trees_settings).setOnClickListener(v ->{
                    Intent intent = new Intent(this, SettingsActivity.class);
                    intent.putExtra("from", "login");
                    startActivity(intent);
                }

        );

        bar.setCustomView(treesBar);
        bar.setDisplayShowCustomEnabled(true);
        EditText email = findViewById(R.id.editTextTextEmailAddress);
        EditText password = findViewById(R.id.editTextTextPassword);

        mAuth = FirebaseAuth.getInstance();
        Button sign_in = findViewById(R.id.sign_in);
        sign_in.setOnClickListener(v ->{
            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(LoginActivity.this, LauncherActivity.class);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    });
        });

        TextView register_here = findViewById(R.id.no_account);
        register_here.setOnClickListener(v ->{
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
    }

}