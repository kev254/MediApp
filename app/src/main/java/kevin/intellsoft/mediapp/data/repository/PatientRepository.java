package kevin.intellsoft.mediapp.data.repository;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import kevin.intellsoft.mediapp.data.local.AppDatabase;
import kevin.intellsoft.mediapp.data.local.dao.PatientDao;
import kevin.intellsoft.mediapp.data.local.entity.Patient;
import kevin.intellsoft.mediapp.data.network.ApiClient;
import kevin.intellsoft.mediapp.data.network.ApiService;
import kevin.intellsoft.mediapp.util.AuthManager;
import kevin.intellsoft.mediapp.util.DateUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientRepository {
    private final PatientDao dao;
    private final ApiService api;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Context context;

    public PatientRepository(Context ctx) {
        this.context = ctx.getApplicationContext();
        AppDatabase db = AppDatabase.getInstance(ctx);
        this.dao = db.patientDao();
        this.api = ApiClient.getClient(ctx).create(ApiService.class);
    }

    // Insert patient locally and try immediate sync
    public void insertPatient(Patient p, RepositoryCallback<Void> cb) {
        executor.execute(() -> {
            try {
                dao.insert(p);
                trySyncPatient(p);
                if (cb != null) cb.onComplete(null);
            } catch (Exception e) {
                if (cb != null) cb.onError(e);
            }
        });
    }

    public List<Patient> getAllPatientsSync() {
        return dao.getAllSync();
    }

    public void trySyncPatient(Patient p) {
        if (!AuthManager.isLoggedIn(context)) {
            Log.i("PatientRepo", "Not logged in — will not sync now");
            return;
        }

        try {
            // ✅ Laravel expects these exact key names
            JsonObject body = new JsonObject();
            body.addProperty("firstname", safeString(p.firstName));
            body.addProperty("lastname", safeString(p.lastName));
            body.addProperty("unique", safeString(p.patientId));
            body.addProperty("dob", DateUtils.formatServerDate(p.dateOfBirth));
            body.addProperty("reg_date", DateUtils.formatServerDate(p.registrationDate));
            body.addProperty("gender", safeString(p.gender));

            Log.d("PatientRepo", "➡️ Syncing patient payload: " + body.toString());

            api.registerPatient(body).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        executor.execute(() -> dao.setPatientSynced(p.patientId, true));
                        Log.i("PatientRepo", "✅ Patient synced successfully: " + p.patientId);
                    } else if (response.code() == 401) {
                        AuthManager.logout(context);
                        Log.w("PatientRepo", "🔒 Unauthorized (401) — user logged out");
                    } else {
                        try {
                            String error = response.errorBody() != null ? response.errorBody().string() : "Unknown";
                            Log.w("PatientRepo", "⚠️ Sync failed (" + response.code() + "): " + error);
                        } catch (Exception e) {
                            Log.e("PatientRepo", "Error reading response", e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e("PatientRepo", "❌ Network sync failed: " + t.getMessage(), t);
                }
            });
        } catch (Exception e) {
            Log.e("PatientRepo", "💥 Exception during sync: " + e.getMessage(), e);
        }
    }

    /**
     * Helper: Avoid null pointer issues.
     */
    private String safeString(Object obj) {
        return obj != null ? obj.toString().trim() : "";
    }


    public interface RepositoryCallback<T> {
        void onComplete(T result);
        void onError(Throwable t);
    }
}
