package kz.adilet.kazakhlearn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

public class SplashScreenActivity extends AppCompatActivity {

    ImageView logo, prod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        logo = findViewById(R.id.logo);
//        prod = findViewById(R.id.prod);

        logo.setAlpha(0f);
        logo.setVisibility(View.VISIBLE);
        logo.animate()
                .alpha(1.0f)
                .setDuration(1500)
                .setListener(null);

//        prod.setAlpha(0f);
//        prod.setVisibility(View.VISIBLE);
//        prod.animate()
//                .alpha(1.0f)
//                .setDuration(1500)
//                .setListener(null);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);
    }
}