package kevin.intellsoft.mediapp.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import kevin.intellsoft.mediapp.model.AssessmentGeneral;
import kevin.intellsoft.mediapp.model.AssessmentOverweight;
import kevin.intellsoft.mediapp.model.Patient;
import kevin.intellsoft.mediapp.model.Vital;

@Database(entities = {Patient.class, Vital.class, AssessmentGeneral.class, AssessmentOverweight.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PatientDao patientDao();
    public abstract VitalDao vitalDao();
    public abstract AssessmentDao assessmentDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "patient_db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
