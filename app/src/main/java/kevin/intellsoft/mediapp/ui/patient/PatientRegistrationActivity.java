package kevin.intellsoft.mediapp.ui.patient;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executors;

import kevin.intellsoft.mediapp.R;
import kevin.intellsoft.mediapp.data.local.entity.Patient;
import kevin.intellsoft.mediapp.data.repository.PatientRepository;
import kevin.intellsoft.mediapp.util.ValidationUtils;

public class PatientRegistrationActivity extends AppCompatActivity {

    private EditText etPatientId, etFirstName, etLastName, etDobInput ;
    private DatePicker dpDob;
    private Spinner spGender;
    private Button btnClose, btnSave;
    private PatientRepository repository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_registration);

        etPatientId = findViewById(R.id.etPatientId);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        dpDob = findViewById(R.id.dpDob);
        spGender = findViewById(R.id.spGender);
        btnClose = findViewById(R.id.btnClose);
        btnSave = findViewById(R.id.btnSave);
        etDobInput = findViewById(R.id.etDobInput);
        dpDob = findViewById(R.id.dpDob);

        spGender.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Male","Female","Other"}));
        repository = new PatientRepository(getApplicationContext());

        btnClose.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> savePatient());
        etDobInput.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            DatePickerDialog picker = new DatePickerDialog(this, (view, year, month, day) -> {
                dpDob.updateDate(year, month, day);
                etDobInput.setText(day + "/" + (month + 1) + "/" + year);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            picker.show();
        });
    }

    private void savePatient() {
        String id = etPatientId.getText().toString().trim();
        String first = etFirstName.getText().toString().trim();
        String last = etLastName.getText().toString().trim();
        if (!ValidationUtils.isNotEmpty(id) || !ValidationUtils.isNotEmpty(first) || !ValidationUtils.isNotEmpty(last)) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int y = dpDob.getYear();
        int m = dpDob.getMonth();
        int d = dpDob.getDayOfMonth();
        long dobMs = new java.util.GregorianCalendar(y, m, d).getTimeInMillis();
        String gender = spGender.getSelectedItem().toString();

        Patient p = new Patient(id, System.currentTimeMillis(), first, last, dobMs, gender);

        // Save via repository
        repository.insertPatient(p, new PatientRepository.RepositoryCallback<Void>() {
            @Override public void onComplete(Void result) {
                runOnUiThread(() -> {
                    Toast.makeText(PatientRegistrationActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                    // open vitals for this patient
                    Intent i = new Intent(PatientRegistrationActivity.this, kevin.intellsoft.mediapp.ui.vitals.VitalsActivity.class);
                    i.putExtra("patientId", id);
                    startActivity(i);
                    finish();
                });
            }
            @Override public void onError(Throwable t) {
                runOnUiThread(() -> Toast.makeText(PatientRegistrationActivity.this, "Save failed: " + t.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }


}
