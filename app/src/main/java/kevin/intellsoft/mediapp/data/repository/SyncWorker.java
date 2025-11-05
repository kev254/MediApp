package kevin.intellsoft.mediapp.data.repository;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import kevin.intellsoft.mediapp.util.AuthManager;
import android.util.Log;

/**
 * WorkManager worker that executes a sync via SyncManager.
 * Declared as a Worker (not CoroutineWorker) so it's simple and compatible.
 */
public class SyncWorker extends Worker {

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context ctx = getApplicationContext();
        if (!AuthManager.isLoggedIn(ctx)) {
            Log.i("SyncWorker", "User not logged in — aborting sync");
            return Result.success(); // treat as success; will run again later
        }
        try {
            SyncManager sm = new SyncManager(ctx);
            sm.syncAll();
            return Result.success();
        } catch (Exception e) {
            Log.e("SyncWorker", "doWork exception: " + e.getMessage(), e);
            // Return retry to allow WorkManager to retry later with backoff
            return Result.retry();
        }
    }
}
