package kevin.intellsoft.mediapp.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Index;
import androidx.annotation.NonNull;

@Entity(tableName = "assessment_overweight",
        foreignKeys = @ForeignKey(entity = Patient.class, parentColumns = "patientId", childColumns = "patientId", onDelete = ForeignKey.CASCADE),
        indices = {@Index("patientId")})
public class AssessmentOverweight {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String patientId;
    public long visitDate;
    public String generalHealth; // Good/Poor
    public String usingDrugs; // Yes/No
    public String comments;
    public boolean synced;

    public AssessmentOverweight(@NonNull String patientId, long visitDate, String generalHealth, String usingDrugs, String comments) {
        this.patientId = patientId;
        this.visitDate = visitDate;
        this.generalHealth = generalHealth;
        this.usingDrugs = usingDrugs;
        this.comments = comments;
        this.synced = false;
    }
}
