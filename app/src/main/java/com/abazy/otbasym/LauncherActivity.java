package com.abazy.otbasym;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.abazy.otbasym.Registration.WelcomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.progress_bar);
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null){
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
        }
        else{

        Locale locale = AppCompatDelegate.getApplicationLocales().get(0);
        if (locale != null) {
            Configuration config = getResources().getConfiguration();
            config.setLocale(locale);
            getApplicationContext().getResources().updateConfiguration(config, null);
        }
            Intent treesIntent = new Intent(this, TreesActivity.class);
            if (Global.settings.loadTree) {
                treesIntent.putExtra("openTreeAutomatically", true);
                treesIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            }
            startActivity(treesIntent);
        }
    }

}


