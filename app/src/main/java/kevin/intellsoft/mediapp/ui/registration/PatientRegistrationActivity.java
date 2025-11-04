package kevin.intellsoft.mediapp.ui.registration;

import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import kevin.intellsoft.mediapp.R;
public class PatientRegistrationActivity extends AppCompatActivity {

    EditText etPatientId, etFirstName, etLastName;
    DatePicker dpDob;
    Spinner spGender;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_registration);

        etPatientId = findViewById(R.id.etPatientId);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        dpDob = findViewById(R.id.dpDob);
        spGender = findViewById(R.id.spGender);
        btnRegister = findViewById(R.id.btnRegister);

        String[] genders = {"Male", "Female", "Other"};
        spGender.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, genders));

        btnRegister.setOnClickListener(v -> {
            String id = etPatientId.getText().toString().trim();
            String first = etFirstName.getText().toString().trim();
            String last = etLastName.getText().toString().trim();
            String gender = spGender.getSelectedItem().toString();

            if (id.isEmpty() || first.isEmpty() || last.isEmpty()) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
                return;
            }

            long dob = new java.util.GregorianCalendar(dpDob.getYear(), dpDob.getMonth(), dpDob.getDayOfMonth()).getTimeInMillis();
            Patient patient = new Patient(id, System.currentTimeMillis(), first, last, dob, gender);

            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    AppDatabase.getInstance(getApplicationContext()).patientDao().insert(patient);
                    runOnUiThread(() -> {
                        Intent i = new Intent(this, VitalsActivity.class);
                        i.putExtra("patientId", id);
                        startActivity(i);
                        finish();
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(this, "Patient ID already exists", Toast.LENGTH_SHORT).show());
                }
            });
        });
    }
}