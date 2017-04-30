package com.nasaspaceapps.codebird.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.nasaspaceapps.codebird.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.pixplicity.easyprefs.library.Prefs;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        ImageView imageView = (ImageView) findViewById(R.id.background);

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
        String imageUri = "drawable://" + R.drawable.codebird;
        imageLoader.displayImage(imageUri, imageView);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Prefs.getBoolean("login_status", false))
                    startActivity(new Intent(getApplicationContext(), MainActivtiy.class));

                else {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }

                finish();
            }
        }, 3000);


    }
}
