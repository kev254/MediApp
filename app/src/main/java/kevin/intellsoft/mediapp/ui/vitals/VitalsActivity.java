package kevin.intellsoft.mediapp.ui.vitals;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executors;

import kevin.intellsoft.mediapp.R;
import kevin.intellsoft.mediapp.data.local.AppDatabase;
import kevin.intellsoft.mediapp.data.local.entity.Patient;
import kevin.intellsoft.mediapp.data.local.entity.Vital;
import kevin.intellsoft.mediapp.data.repository.VitalRepository;
import kevin.intellsoft.mediapp.ui.assessment.GeneralAssessmentActivity;
import kevin.intellsoft.mediapp.ui.assessment.OverweightAssessmentActivity;
import kevin.intellsoft.mediapp.util.BMIUtils;

public class VitalsActivity extends AppCompatActivity {

    private TextView tvPatientName;
    private TextInputEditText etVisitDate;
    private TextInputEditText etHeight, etWeight;
    private TextView tvBmi;
    private MaterialButton btnClose, btnSave;

    private String patientId;
    private long visitDateMs;
    private VitalRepository vitalRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vitals);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setTitle("Patient Vitals");

        tvPatientName = findViewById(R.id.tvPatientName);
        etVisitDate = findViewById(R.id.etVisitDate);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        tvBmi = findViewById(R.id.tvBmi);
        btnClose = findViewById(R.id.btnClose);
        btnSave = findViewById(R.id.btnSave);

        vitalRepository = new VitalRepository(getApplicationContext());

        // patientId must be passed in intent
        patientId = getIntent().getStringExtra("patientId");
        if (patientId == null) {
            Toast.makeText(this, "No patient selected", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // load patient name on background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            Patient p = AppDatabase.getInstance(getApplicationContext()).patientDao().findById(patientId);
            runOnUiThread(() -> {
                if (p != null) tvPatientName.setText(p.getFullName());
                else tvPatientName.setText("Unknown patient");
            });
        });

        // default visit date = today
        visitDateMs = System.currentTimeMillis();
        etVisitDate.setText(android.text.format.DateFormat.format("yyyy-MM-dd", visitDateMs));
        etVisitDate.setOnClickListener(v -> showDatePicker());

        // auto-calc BMI when text changes
        TextWatcher calcWatcher = new SimpleTextWatcher() {
            @Override public void afterTextChanged(android.text.Editable s) { updateBmi(); }
        };
        etHeight.addTextChangedListener(calcWatcher);
        etWeight.addTextChangedListener(calcWatcher);

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

    private void updateBmi() {
        String hs = etHeight.getText().toString().trim();
        String ws = etWeight.getText().toString().trim();
        if (hs.isEmpty() || ws.isEmpty()) {
            tvBmi.setText("BMI: —");
            return;
        }
        try {
            float h = Float.parseFloat(hs);
            float w = Float.parseFloat(ws);
            if (h <= 0 || w <= 0) { tvBmi.setText("BMI: —"); return; }
            float bmi = BMIUtils.calculate(w, h);
            tvBmi.setText(String.format(Locale.getDefault(), "BMI: %.2f (%s)", bmi, BMIUtils.getStatus(bmi)));
        } catch (NumberFormatException ex) {
            tvBmi.setText("BMI: —");
        }
    }

    private void onSave() {
        String hs = etHeight.getText().toString().trim();
        String ws = etWeight.getText().toString().trim();
        if (hs.isEmpty() || ws.isEmpty()) {
            Toast.makeText(this, "Height and Weight are required", Toast.LENGTH_SHORT).show();
            return;
        }

        float heightCm, weightKg;
        try {
            heightCm = Float.parseFloat(hs);
            weightKg = Float.parseFloat(ws);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter valid numbers", Toast.LENGTH_SHORT).show();
            return;
        }
        float bmi = BMIUtils.calculate(weightKg, heightCm);

        Vital v = new Vital(patientId, visitDateMs, heightCm, weightKg, bmi);
        vitalRepository.addVital(v, new VitalRepository.RepositoryCallback<Void>() {
            @Override public void onComplete(Void result) {
                runOnUiThread(() -> {
                    Toast.makeText(VitalsActivity.this, "Vitals saved", Toast.LENGTH_SHORT).show();
                    // route based on BMI: < 25 -> general, >=25 -> overweight
                    Intent next = (bmi < 25f)
                            ? new Intent(VitalsActivity.this, GeneralAssessmentActivity.class)
                            : new Intent(VitalsActivity.this, OverweightAssessmentActivity.class);
                    next.putExtra("patientId", patientId);
                    next.putExtra("visitDate", visitDateMs);
                    startActivity(next);
                    finish();
                });
            }
            @Override public void onError(Throwable t) {
                runOnUiThread(() -> Toast.makeText(VitalsActivity.this, "Save failed: " + t.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    // SimpleTextWatcher inner class
    public abstract static class SimpleTextWatcher implements android.text.TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
        @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
        @Override public abstract void afterTextChanged(android.text.Editable s);
    }
}
