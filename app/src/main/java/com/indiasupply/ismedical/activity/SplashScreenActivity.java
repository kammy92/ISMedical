package com.indiasupply.ismedical.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.indiasupply.ismedical.R;


public class SplashScreenActivity extends Activity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    
    ImageView ivLogo;
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
    
    
        requestWindowFeature (Window.FEATURE_NO_TITLE);
        getWindow ().setFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView (R.layout.activity_splash_screen);
        
        new Handler ().postDelayed (new Runnable () {
            @Override
            public void run () {
                Intent intent = new Intent (SplashScreenActivity.this, MainActivity.class);
                intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity (intent);
                overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }, SPLASH_TIME_OUT);
    
        final Animation animShake = AnimationUtils.loadAnimation (this, R.anim.splash_anim);
        ivLogo = (ImageView) findViewById (R.id.ivLogo);
        ivLogo.startAnimation (animShake);
    }
    
}