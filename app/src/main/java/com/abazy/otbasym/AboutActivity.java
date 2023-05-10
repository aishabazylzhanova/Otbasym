package com.abazy.otbasym;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.lapide);

        TextView version = findViewById(R.id.lapide_versione);
        version.setText(getString(R.string.version_name, com.abazy.otbasym.BuildConfig.VERSION_NAME));


    }
}
