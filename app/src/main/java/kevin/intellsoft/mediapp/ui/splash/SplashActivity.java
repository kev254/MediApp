package kevin.intellsoft.mediapp.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import kevin.intellsoft.mediapp.R;
import kevin.intellsoft.mediapp.ui.auth.LoginActivity;
import kevin.intellsoft.mediapp.ui.patient.PatientListingActivity;
import kevin.intellsoft.mediapp.util.AuthManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            if (AuthManager.isLoggedIn(this)) {
                startActivity(new Intent(this, PatientListingActivity.class));
//                startActivity(new Intent(this, LoginActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }, SPLASH_DELAY);
    }
}
