package kevin.intellsoft.mediapp.ui.assessment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import kevin.intellsoft.mediapp.R;
import kevin.intellsoft.mediapp.ui.listing.PatientListingActivity;

public class OverweightAssessmentActivity extends AppCompatActivity {

    Spinner spHealth, spDrugs;
    EditText etComments;
    Button btnSubmit;
    String patientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overweight_assessment);

        patientId = getIntent().getStringExtra("patientId");
        spHealth = findViewById(R.id.spHealth);
        spDrugs = findViewById(R.id.spDrugs);
        etComments = findViewById(R.id.etComments);
        btnSubmit = findViewById(R.id.btnSubmit);

        spHealth.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Good", "Poor"}));
        spDrugs.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Yes", "No"}));

        btnSubmit.setOnClickListener(v -> {
            if (etComments.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, PatientListingActivity.class));
            finish();
        });
    }
}
