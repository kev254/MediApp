package kevin.intellsoft.mediapp.ui.listing;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import kevin.intellsoft.mediapp.R;

public class PatientListingActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_listing);
        listView = findViewById(R.id.listPatients);

        Executors.newSingleThreadExecutor().execute(() -> {
            List<Patient> patients = AppDatabase.getInstance(getApplicationContext()).patientDao().getAllSync();
            List<String> display = new ArrayList<>();
            for (Patient p : patients) {
                display.add(p.firstName + " " + p.lastName + " - " + BMIUtils.bmiCategory(22.5)); // placeholder
            }
            runOnUiThread(() -> {
                listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, display));
            });
        });
    }
}