package kevin.intellsoft.mediapp.data.repository;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import kevin.intellsoft.mediapp.data.local.AppDatabase;
import kevin.intellsoft.mediapp.data.local.dao.VitalDao;
import kevin.intellsoft.mediapp.data.local.entity.Vital;
import kevin.intellsoft.mediapp.data.network.ApiClient;
import kevin.intellsoft.mediapp.data.network.ApiService;
import kevin.intellsoft.mediapp.util.AuthManager;
import kevin.intellsoft.mediapp.util.DateUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VitalRepository {

    private final VitalDao dao;
    private final ApiService api;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Context context;

    public VitalRepository(Context ctx) {
        this.context = ctx.getApplicationContext();
        this.dao = AppDatabase.getInstance(ctx).vitalDao();
        this.api = ApiClient.getClient(ctx).create(ApiService.class);
    }

    public void addVital(Vital v, RepositoryCallback<Void> cb) {
        executor.execute(() -> {
            try {
                dao.insert(v);
                syncVital(v);
                if (cb != null) cb.onComplete(null);
            } catch (Exception e) {
                if (cb != null) cb.onError(e);
            }
        });
    }

    /**
     * Sync a single vital record to the server.
     * Sends all fields as strings, formatted correctly for Laravel.
     */
    public void syncVital(Vital v) {
        if (!AuthManager.isLoggedIn(context)) {
            Log.i("VitalRepo", "No login token, skip sync");
            return;
        }

        try {
            JsonObject body = new JsonObject();
            body.addProperty("patient_id", safeString(v.patientId));
            body.addProperty("visit_date", DateUtils.formatServerDate(v.visitDate));
            body.addProperty("height", safeString(v.heightCm));
            body.addProperty("weight", safeString(v.weightKg));
            body.addProperty("bmi", safeString(v.bmi));

            Log.d("VitalRepo", "➡️ Syncing vital payload: " + body.toString());

            api.addVitals(body).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        executor.execute(() -> dao.setSynced(v.id, true));
                        Log.i("VitalRepo", "✅ Vital synced successfully: " + v.id);
                    } else if (response.code() == 401) {
                        AuthManager.logout(context);
                        Log.w("VitalRepo", "🔒 Unauthorized (401) — user logged out");
                    } else {
                        try {
                            String error = response.errorBody() != null
                                    ? response.errorBody().string()
                                    : "Unknown error";
                            Log.w("VitalRepo", "⚠️ Sync failed (" + response.code() + "): " + error);
                        } catch (Exception e) {
                            Log.e("VitalRepo", "Error reading response", e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e("VitalRepo", "❌ Network error: " + t.getMessage(), t);
                }
            });

        } catch (Exception e) {
            Log.e("VitalRepo", "💥 Exception during sync: " + e.getMessage(), e);
        }
    }

    private String safeString(Object o) {
        return o != null ? o.toString().trim() : "";
    }

    public interface RepositoryCallback<T> {
        void onComplete(T result);
        void onError(Throwable t);
    }
}
