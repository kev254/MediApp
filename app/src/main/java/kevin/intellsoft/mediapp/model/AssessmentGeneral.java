package kevin.intellsoft.mediapp.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Index;
import androidx.annotation.NonNull;

@Entity(tableName = "assessment_general",
        foreignKeys = @ForeignKey(entity = Patient.class, parentColumns = "patientId", childColumns = "patientId", onDelete = ForeignKey.CASCADE),
        indices = {@Index("patientId")})
public class AssessmentGeneral {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String patientId;
    public long visitDate;
    public String generalHealth; // Good/Poor
    public String onDiet; // Yes/No
    public String comments;
    public boolean synced;

    public AssessmentGeneral(@NonNull String patientId, long visitDate, String generalHealth, String onDiet, String comments) {
        this.patientId = patientId;
        this.visitDate = visitDate;
        this.generalHealth = generalHealth;
        this.onDiet = onDiet;
        this.comments = comments;
        this.synced = false;
    }
}