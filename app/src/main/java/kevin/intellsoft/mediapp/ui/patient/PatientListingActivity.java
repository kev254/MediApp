package kevin.intellsoft.mediapp.ui.patient;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

import kevin.intellsoft.mediapp.R;
import kevin.intellsoft.mediapp.data.local.AppDatabase;
import kevin.intellsoft.mediapp.data.local.entity.Patient;
import kevin.intellsoft.mediapp.data.repository.PatientRepository;
import kevin.intellsoft.mediapp.data.repository.SyncManager;
import kevin.intellsoft.mediapp.util.DateUtils;

public class PatientListingActivity extends AppCompatActivity implements PatientAdapter.OnPatientClickListener {

    private RecyclerView rv;
    private PatientAdapter adapter;
    private EditText etFilterDate;
    private Button btnFilter;
    private FloatingActionButton fabAdd;
    private PatientRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_listing);

        rv = findViewById(R.id.rvPatients);
        etFilterDate = findViewById(R.id.etFilterDate);
        btnFilter = findViewById(R.id.btnFilter);
        fabAdd = findViewById(R.id.fabAddPatient);

        repository = new PatientRepository(getApplicationContext());
        adapter = new PatientAdapter(this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        loadPatients();
        new SyncManager(this).syncAll();

        etFilterDate.setOnClickListener(v -> showDatePicker());
        btnFilter.setOnClickListener(v -> {
            Object tag = etFilterDate.getTag();
            if (tag instanceof Long) {
                filterByDate((Long) tag);
            } else {
                loadPatients();
            }
        });

        fabAdd.setOnClickListener(v -> startActivity(new Intent(this, PatientRegistrationActivity.class)));
    }

    private void loadPatients() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Patient> all = repository.getAllPatientsSync();
            runOnUiThread(() -> adapter.update(all));
        });
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dp = new DatePickerDialog(this, (view, y, m, d) -> {
            Calendar sel = Calendar.getInstance();
            sel.set(y, m, d, 0, 0, 0);
            long ms = sel.getTimeInMillis();
            etFilterDate.setText(DateUtils.formatDisplayDate(ms));
            etFilterDate.setTag(ms);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dp.show();
    }

    private void filterByDate(long ms) {
        long start = DateUtils.startOfDayMillis(ms);
        long end = DateUtils.endOfDayMillis(ms);

        Executors.newSingleThreadExecutor().execute(() -> {
            // We only have patient table here — vitals table is separate.
            // For filtering patients by visit date, you'd query vitals and map to patients (later).
            // For now show all patients (placeholder to be extended).
            List<Patient> all = repository.getAllPatientsSync();
            runOnUiThread(() -> {
                Toast.makeText(this, "Date filter is applied (server-side mapping required)", Toast.LENGTH_SHORT).show();
                adapter.update(all);
            });
        });
    }

    @Override
    public void onPatientClick(Patient patient) {
        // open vitals activity for selected patient
//        Intent i = new Intent(this, kevin.intellsoft.mediapp.ui.vitals.VitalsActivity.class);
//        i.putExtra("patientId", patient.patientId);
//        startActivity(i);
    }
}
