package kevin.intellsoft.mediapp.ui.vitals;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executors;

import kevin.intellsoft.mediapp.R;

public class VitalsActivity extends AppCompatActivity {

    EditText etHeight, etWeight;
    TextView tvBmi;
    Button btnSave;
    String patientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vitals);

        patientId = getIntent().getStringExtra("patientId");
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        tvBmi = findViewById(R.id.tvBmi);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> {
            double height = Double.parseDouble(etHeight.getText().toString());
            double weight = Double.parseDouble(etWeight.getText().toString());
            double bmi = BMIUtils.calcBmi(weight, height);
            tvBmi.setText(String.format("%.2f", bmi));

            Vital vital = new Vital(patientId, System.currentTimeMillis(), height, weight, bmi);
            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase.getInstance(getApplicationContext()).vitalDao().insert(vital);
                runOnUiThread(() -> {
                    if (bmi < 25)
                        startActivity(new Intent(this, GeneralAssessmentActivity.class).putExtra("patientId", patientId));
                    else
                        startActivity(new Intent(this, OverweightAssessmentActivity.class).putExtra("patientId", patientId));
                    finish();
                });
            });
        });
    }
}