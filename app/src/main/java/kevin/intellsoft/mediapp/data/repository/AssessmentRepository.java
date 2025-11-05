package kevin.intellsoft.mediapp.data.repository;

import android.content.Context;
import android.util.Log;
import com.google.gson.JsonObject;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import kevin.intellsoft.mediapp.data.local.AppDatabase;
import kevin.intellsoft.mediapp.data.local.dao.AssessmentDao;
import kevin.intellsoft.mediapp.data.local.entity.Assessment;
import kevin.intellsoft.mediapp.data.network.ApiClient;
import kevin.intellsoft.mediapp.data.network.ApiService;
import kevin.intellsoft.mediapp.util.AuthManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssessmentRepository {
    private final AssessmentDao dao;
    private final ApiService api;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Context context;

    public AssessmentRepository(Context ctx) {
        context = ctx.getApplicationContext();
        dao = AppDatabase.getInstance(ctx).assessmentDao();
        api = ApiClient.getClient(ctx).create(ApiService.class);
    }

    public void addAssessment(Assessment a, RepositoryCallback<Void> cb) {
        executor.execute(() -> {
            try {
                dao.insert(a);
                syncAssessment(a);
                if (cb != null) cb.onComplete(null);
            } catch (Exception e) {
                if (cb != null) cb.onError(e);
            }
        });
    }

    private void syncAssessment(Assessment a) {
        if (!AuthManager.isLoggedIn(context)) {
            Log.i("AssessmentRepo","No token, skip sync");
            return;
        }

        JsonObject body = new JsonObject();
        body.addProperty("patient_id", a.patientId);
        body.addProperty("visit_date", a.visitDate);
        body.addProperty("type", a.type);
        body.addProperty("general_health", a.generalHealth);
        body.addProperty(a.type.equals("general") ? "diet_history" : "drug_use", a.dietOrDrugs);
        body.addProperty("comments", a.comments);

        api.addAssessment(body).enqueue(new Callback<JsonObject>() {
            @Override public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) executor.execute(() -> dao.setSynced(a.id, true));
                else if (response.code() == 401) AuthManager.logout(context);
            }
            @Override public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("AssessmentRepo","Sync fail: "+t.getMessage());
            }
        });
    }

    public interface RepositoryCallback<T> {
        void onComplete(T result);
        void onError(Throwable t);
    }
}
