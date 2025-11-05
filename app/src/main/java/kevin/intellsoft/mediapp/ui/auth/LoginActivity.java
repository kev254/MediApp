package kevin.intellsoft.mediapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import java.util.concurrent.TimeUnit;

import kevin.intellsoft.mediapp.R;
import kevin.intellsoft.mediapp.data.network.ApiClient;
import kevin.intellsoft.mediapp.data.network.ApiService;
import kevin.intellsoft.mediapp.data.repository.SyncManager;
import kevin.intellsoft.mediapp.data.repository.SyncWorker;
import kevin.intellsoft.mediapp.ui.patient.PatientListingActivity;
import kevin.intellsoft.mediapp.util.AuthManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    TextView btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);

        btnLogin.setOnClickListener(v -> performLogin());
        btnSignup.setOnClickListener(v -> startActivity(new Intent(this, SignupActivity.class)));
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Signing in...");

        ApiService api = ApiClient.getClient(this).create(ApiService.class);
        JsonObject body = new JsonObject();
        body.addProperty("email", email);
        body.addProperty("password", password);

        api.login(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Login");

                if (response.isSuccessful() && response.body() != null) {
                    JsonObject json = response.body();

                    boolean success = json.has("success") && json.get("success").getAsBoolean();
                    if (success && json.has("data")) {
                        JsonObject data = json.getAsJsonObject("data");
                        String token = data.get("access_token").getAsString();
                        String name = data.get("name").getAsString();
                        // Save token
                        AuthManager.saveToken(LoginActivity.this, token);
                        AuthManager.saveUserName(LoginActivity.this, name);

                        SyncManager sm = new SyncManager(getApplicationContext());
                        sm.syncAll(); // immediate foreground-triggered sync

// schedule periodic background sync (unique work so duplicates aren't created)
                        Constraints constraints = new Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build();

                        PeriodicWorkRequest periodicSync = new PeriodicWorkRequest.Builder(SyncWorker.class, 15, TimeUnit.MINUTES)
                                .setConstraints(constraints)
                                .build();

                        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork(
                                "mediapp_sync_work",
                                ExistingPeriodicWorkPolicy.KEEP,
                                periodicSync
                        );

// also enqueue an immediate one-off job via WorkManager (optional)
                        OneTimeWorkRequest immediate = new OneTimeWorkRequest.Builder(SyncWorker.class)
                                .setConstraints(constraints)
                                .build();
                        WorkManager.getInstance(getApplicationContext()).enqueue(immediate);

                        Toast.makeText(LoginActivity.this, "Welcome, " + name + "!", Toast.LENGTH_SHORT).show();

                        // Navigate to patient list
                        startActivity(new Intent(LoginActivity.this, PatientListingActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed. Try again.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Login");
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
