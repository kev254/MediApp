package kevin.intellsoft.mediapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.Set;

import kevin.intellsoft.mediapp.R;
import kevin.intellsoft.mediapp.data.network.ApiClient;
import kevin.intellsoft.mediapp.data.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etFirstname, etLastname, etPassword;
    private MaterialButton btnSignup;
    TextView btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etEmail = findViewById(R.id.etEmail);
        etFirstname = findViewById(R.id.etFirstname);
        etLastname = findViewById(R.id.etLastname);
        etPassword = findViewById(R.id.etPassword);
        btnSignup = findViewById(R.id.btnSignup);
        btnLogin = findViewById(R.id.btnLogin);

        btnSignup.setOnClickListener(v -> performSignup());
        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void performSignup() {
        String email = etEmail.getText().toString().trim();
        String firstname = etFirstname.getText().toString().trim();
        String lastname = etLastname.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || firstname.isEmpty() || lastname.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSignup.setEnabled(false);
        btnSignup.setText("Creating account...");

        ApiService api = ApiClient.getClient(this).create(ApiService.class);
        JsonObject body = new JsonObject();
        body.addProperty("email", email);
        body.addProperty("firstname", firstname);
        body.addProperty("lastname", lastname);
        body.addProperty("password", password);

        api.signup(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                btnSignup.setEnabled(true);
                btnSignup.setText("Sign Up");

                try {
                    if (response.isSuccessful() && response.body() != null) {
                        JsonObject json = response.body();

                        // ✅ Success flow
                        if (json.has("success") && json.get("success").getAsBoolean()) {
                            JsonObject data = json.has("data") ? json.getAsJsonObject("data") : null;
                            String successMsg = (data != null && data.has("message"))
                                    ? data.get("message").getAsString()
                                    : "Account created successfully.";
                            Toast.makeText(SignupActivity.this, successMsg, Toast.LENGTH_LONG).show();

                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            finish();
                            return;
                        }

                        // ⚠️ Logical or validation error
                        if (json.has("errors")) {
                            JsonObject errors = json.getAsJsonObject("errors");
                            StringBuilder errorBuilder = new StringBuilder();

                            Set<Map.Entry<String, JsonElement>> entries = errors.entrySet();
                            for (Map.Entry<String, JsonElement> entry : entries) {
                                JsonElement val = entry.getValue();
                                if (val.isJsonArray() && val.getAsJsonArray().size() > 0) {
                                    errorBuilder.append(val.getAsJsonArray().get(0).getAsString()).append("\n");
                                }
                            }

                            String errorMsg = errorBuilder.length() > 0
                                    ? errorBuilder.toString().trim()
                                    : "Validation failed. Please check your inputs.";

                            Toast.makeText(SignupActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Fallback to top-level message
                        String fallbackMsg = json.has("message") ? json.get("message").getAsString() : "Signup failed.";
                        Toast.makeText(SignupActivity.this, fallbackMsg, Toast.LENGTH_LONG).show();

                    } else {
                        // HTTP error with possible body
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Toast.makeText(SignupActivity.this, "Signup failed: " + errorBody, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(SignupActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                btnSignup.setEnabled(true);
                btnSignup.setText("Sign Up");
                Toast.makeText(SignupActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
