package kevin.intellsoft.mediapp.repository;

import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import kevin.intellsoft.mediapp.db.AppDatabase;
import kevin.intellsoft.mediapp.model.Patient;
import kevin.intellsoft.mediapp.model.Vital;

/**
 * Simple SyncManager that can be invoked to push unsynced records.
 * For production, move this into WorkManager for scheduled retries.
 */
public class SyncManager {
    private final AppDatabase db;
    private final PatientRepository repository;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public SyncManager(Context ctx, String token) {
        db = AppDatabase.getInstance(ctx);
        repository = new PatientRepository(ctx, token);
    }

    public void syncAll() {
        executor.execute(() -> {
            // sync patients
            List<Patient> patients = db.patientDao().getAllSync();
            for (Patient p : patients) {
                if (!p.synced) {
                    repository.insertPatient(p, new PatientRepository.RepositoryCallback<Void>() {
                        @Override
                        public void onComplete(Void result) { /* ok */ }
                        @Override
                        public void onError(Throwable t) { Log.d("Sync", "patient sync err: " + t.getMessage()); }
                    });
                }
            }

            // sync vitals
            List<Vital> vitals = db.vitalDao().getAllSync();
            for (Vital v : vitals) {
                if (!v.synced) {
                    repository.insertVital(v, new PatientRepository.RepositoryCallback<Void>() {
                        @Override public void onComplete(Void result) {}
                        @Override public void onError(Throwable t) { Log.d("Sync", "vital sync err: " + t.getMessage()); }
                    });
                }
            }
        });
    }
}