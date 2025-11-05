package kevin.intellsoft.mediapp.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "assessments")
public class Assessment {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull public String patientId;
    public long visitDate;
    public String type;         // "general" or "overweight"
    public String generalHealth;
    public String dietOrDrugs;  // diet = yes/no for general, drugs = yes/no for overweight
    public String comments;
    public boolean synced;

    public Assessment(@NonNull String patientId, long visitDate, String type,
                      String generalHealth, String dietOrDrugs, String comments) {
        this.patientId = patientId;
        this.visitDate = visitDate;
        this.type = type;
        this.generalHealth = generalHealth;
        this.dietOrDrugs = dietOrDrugs;
        this.comments = comments;
        this.synced = false;
    }
}
