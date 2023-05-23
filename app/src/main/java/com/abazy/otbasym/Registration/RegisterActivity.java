package com.abazy.otbasym.Registration;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.abazy.otbasym.BaseActivity;
import com.abazy.otbasym.LauncherActivity;
import com.abazy.otbasym.R;
import com.abazy.otbasym.SettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends BaseActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText email = findViewById(R.id.editTextTextEmailAddress);
        EditText password = findViewById(R.id.editTextTextPassword);
        mAuth = FirebaseAuth.getInstance();
        Button register = findViewById(R.id.register);


        ActionBar bar = getSupportActionBar();
        View treesBar = getLayoutInflater().inflate(R.layout.trees_bar_for_home, null);
        treesBar.findViewById(R.id.trees_settings).setOnClickListener(v ->{
                    Intent intent = new Intent(this, SettingsActivity.class);
                    intent.putExtra("from", "register");
                    startActivity(intent);
                }
        );
        assert bar != null;
        bar.setCustomView(treesBar);
        bar.setDisplayShowCustomEnabled(true);

        register.setOnClickListener(v ->{
            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                Intent intent = new Intent(RegisterActivity.this, LauncherActivity.class);
                                startActivity(intent);

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

        });


        TextView already_have_account = findViewById(R.id.already_have_account);
        already_have_account.setOnClickListener(v ->{
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RegisterActivity.this, WelcomeActivity.class));
    }
}