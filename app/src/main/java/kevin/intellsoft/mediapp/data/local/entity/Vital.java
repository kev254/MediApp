package kevin.intellsoft.mediapp.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "vitals")
public class Vital {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String patientId;
    public long visitDate;
    public float heightCm;
    public float weightKg;
    public float bmi;
    public boolean synced;

    public Vital(@NonNull String patientId, long visitDate, float heightCm, float weightKg, float bmi) {
        this.patientId = patientId;
        this.visitDate = visitDate;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.bmi = bmi;
        this.synced = false;
    }
}
