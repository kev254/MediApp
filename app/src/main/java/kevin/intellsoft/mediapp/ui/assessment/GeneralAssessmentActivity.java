package kevin.intellsoft.mediapp.ui.assessment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.concurrent.Executors;

import kevin.intellsoft.mediapp.R;
import kevin.intellsoft.mediapp.data.local.AppDatabase;
import kevin.intellsoft.mediapp.data.local.entity.Assessment;
import kevin.intellsoft.mediapp.data.local.entity.Patient;
import kevin.intellsoft.mediapp.data.repository.AssessmentRepository;
import kevin.intellsoft.mediapp.ui.patient.PatientListingActivity;

public class GeneralAssessmentActivity extends AppCompatActivity {

    private TextView tvPatientName;
    private TextInputEditText etVisitDate;
    private RadioGroup rgHealth, rgDiet;
    private EditText etComments;
    private MaterialButton btnClose, btnSave;

    private String patientId;
    private long visitDateMs;
    private AssessmentRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_assessment);

        tvPatientName = findViewById(R.id.tvPatientName);
        etVisitDate = findViewById(R.id.etVisitDate);
        rgHealth = findViewById(R.id.rgHealth);
        rgDiet = findViewById(R.id.rgDiet);
        etComments = findViewById(R.id.etComments);
        btnClose = findViewById(R.id.btnClose);
        btnSave = findViewById(R.id.btnSubmit);

        repo = new AssessmentRepository(getApplicationContext());

        patientId = getIntent().getStringExtra("patientId");
        long passedVisit = getIntent().getLongExtra("visitDate", -1L);
        visitDateMs = (passedVisit > 0) ? passedVisit : System.currentTimeMillis();

        etVisitDate.setText(android.text.format.DateFormat.format("yyyy-MM-dd", visitDateMs));
        etVisitDate.setOnClickListener(v -> showDatePicker());

        // load patient name
        Executors.newSingleThreadExecutor().execute(() -> {
            Patient p = AppDatabase.getInstance(getApplicationContext()).patientDao().findById(patientId);
            runOnUiThread(() -> tvPatientName.setText(p != null ? p.getFullName() : "Unknown"));
        });

        btnClose.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> onSave());
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dp = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    Calendar sel = Calendar.getInstance();
                    sel.set(year, month, dayOfMonth, 0, 0, 0);
                    visitDateMs = sel.getTimeInMillis();
                    etVisitDate.setText(android.text.format.DateFormat.format("yyyy-MM-dd", visitDateMs));
                },
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dp.show();
    }

    private void onSave() {
        // validate radio groups
        int healthId = rgHealth.getCheckedRadioButtonId();
        int dietId = rgDiet.getCheckedRadioButtonId();
        if (healthId == -1 || dietId == -1) {
            Toast.makeText(this, "Please answer all questions", Toast.LENGTH_SHORT).show();
            return;
        }
        String health = ((RadioButton) findViewById(healthId)).getText().toString();
        String diet = ((RadioButton) findViewById(dietId)).getText().toString();
        String comments = etComments.getText().toString().trim();

        Assessment a = new Assessment(patientId, visitDateMs, "general", health, diet, comments);
        repo.addAssessment(a, new AssessmentRepository.RepositoryCallback<Void>() {
            @Override public void onComplete(Void result) {
                runOnUiThread(() -> {
                    Toast.makeText(GeneralAssessmentActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(GeneralAssessmentActivity.this, PatientListingActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                });
            }
            @Override public void onError(Throwable t) {
                runOnUiThread(() -> Toast.makeText(GeneralAssessmentActivity.this, "Save failed: " + t.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }
}
