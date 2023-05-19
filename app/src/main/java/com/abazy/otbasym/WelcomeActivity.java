package com.abazy.otbasym;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ActionBar bar = getSupportActionBar();
        View treesBar = getLayoutInflater().inflate(R.layout.trees_bar_for_home, null);
        treesBar.findViewById(R.id.trees_settings).setOnClickListener(v ->{
                    Intent intent = new Intent(this, SettingsActivity.class);
                    intent.putExtra("from", "welcome");
                    startActivity(intent);
                }

        );
        assert bar != null;
        bar.setCustomView(treesBar);
        bar.setDisplayShowCustomEnabled(true);
        Button sign_in =findViewById(R.id.sign_in);
        Button register = findViewById(R.id.register);

        sign_in.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        register.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

}