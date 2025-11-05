package kevin.intellsoft.mediapp.data.repository;

import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import kevin.intellsoft.mediapp.data.local.AppDatabase;
import kevin.intellsoft.mediapp.data.local.entity.Assessment;
import kevin.intellsoft.mediapp.data.local.entity.Patient;
import kevin.intellsoft.mediapp.data.local.entity.Vital;
import kevin.intellsoft.mediapp.util.AuthManager;

/**
 * SyncManager: coordinates scanning Room for unsynced records and triggering per-record sync
 * using the respective repositories. Safe to call from background threads.
 */
public class SyncManager {
    private final Context context;
    private final PatientRepository patientRepo;
    private final VitalRepository vitalRepo;
    private final AssessmentRepository assessmentRepo;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public SyncManager(Context context) {
        this.context = context.getApplicationContext();
        this.patientRepo = new PatientRepository(this.context);
        this.vitalRepo = new VitalRepository(this.context);
        this.assessmentRepo = new AssessmentRepository(this.context);
    }

    /**
     * Kick off a background sync of all unsynced records.
     * This returns immediately; work runs on a background executor.
     */
    public void syncAll() {
        executor.execute(() -> {
            String token = AuthManager.getToken(context);
            if (token == null || token.isEmpty()) {
                Log.w("SyncManager", "User not logged in or token missing — skipping sync");
                return;
            }
            try {
                syncPatients();
//                syncVitals();
//                syncAssessments();
            } catch (Exception e) {
                Log.e("SyncManager", "syncAll error: " + e.getMessage(), e);
            }
        });
    }

    private void syncPatients() {
        try {
            List<Patient> unsynced = AppDatabase.getInstance(context).patientDao().getUnsynced();
            if (unsynced == null || unsynced.isEmpty()) return;
            Log.i("SyncManager", "Found " + unsynced.size() + " unsynced patients");
            for (Patient p : unsynced) {
                patientRepo.trySyncPatient(p);
            }
        } catch (Exception e) {
            Log.e("SyncManager", "syncPatients error: " + e.getMessage(), e);
        }
    }

    private void syncVitals() {
        try {
            List<Vital> unsynced = AppDatabase.getInstance(context).vitalDao().getUnsynced();
            if (unsynced == null || unsynced.isEmpty()) return;
            Log.i("SyncManager", "Found " + unsynced.size() + " unsynced vitals");
            for (Vital v : unsynced) {
                vitalRepo.syncVital(v);
            }
        } catch (Exception e) {
            Log.e("SyncManager", "syncVitals error: " + e.getMessage(), e);
        }
    }

    private void syncAssessments() {
        try {
            List<Assessment> unsynced = AppDatabase.getInstance(context).assessmentDao().getUnsynced();
            if (unsynced == null || unsynced.isEmpty()) return;
            Log.i("SyncManager", "Found " + unsynced.size() + " unsynced assessments");
            for (Assessment a : unsynced) {
                assessmentRepo.addAssessment(a, null);
            }
        } catch (Exception e) {
            Log.e("SyncManager", "syncAssessments error: " + e.getMessage(), e);
        }
    }
}
