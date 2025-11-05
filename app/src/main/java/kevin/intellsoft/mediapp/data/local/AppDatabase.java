package kevin.intellsoft.mediapp.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import kevin.intellsoft.mediapp.data.local.dao.AssessmentDao;
import kevin.intellsoft.mediapp.data.local.dao.PatientDao;
import kevin.intellsoft.mediapp.data.local.dao.VitalDao;
import kevin.intellsoft.mediapp.data.local.entity.Assessment;
import kevin.intellsoft.mediapp.data.local.entity.Patient;
import kevin.intellsoft.mediapp.data.local.entity.Vital;

@Database(entities = {Patient.class, Vital.class, Assessment.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PatientDao patientDao();
    public abstract VitalDao vitalDao();
    public abstract AssessmentDao assessmentDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context ctx) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(ctx.getApplicationContext(),
                                    AppDatabase.class, "mediapp_db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    public Double getLatestBmi(String patientId) {
        return vitalDao().getLatestBmiForPatient(patientId);
    }
}
