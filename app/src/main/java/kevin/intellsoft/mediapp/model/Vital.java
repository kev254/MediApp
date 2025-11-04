package kevin.intellsoft.mediapp.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Vital entity for Room
 */
@Entity(tableName = "vitals",
        foreignKeys = @ForeignKey(entity = Patient.class,
                parentColumns = "patientId",
                childColumns = "patientId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("patientId")})
public class Vital {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String patientId;
    public long visitDate; // epoch millis
    public double heightCm;
    public double weightKg;
    public double bmi;

    public boolean synced;

    public Vital(@NonNull String patientId, long visitDate, double heightCm, double weightKg, double bmi) {
        this.patientId = patientId;
        this.visitDate = visitDate;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.bmi = bmi;
        this.synced = false;
    }
}
