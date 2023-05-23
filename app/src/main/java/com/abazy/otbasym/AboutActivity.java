package com.abazy.otbasym;

import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.about);

        TextView version = findViewById(R.id.tombstone_version);
        version.setText(getString(R.string.version_name, com.abazy.otbasym.BuildConfig.VERSION_NAME));


    }
}
