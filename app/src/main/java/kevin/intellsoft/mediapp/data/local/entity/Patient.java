package kevin.intellsoft.mediapp.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "patients")
public class Patient {
    @PrimaryKey
    @NonNull
    public String patientId;

    public long registrationDate; // epoch millis
    public String firstName;
    public String lastName;
    public long dateOfBirth; // epoch millis
    public String gender;

    public boolean synced; // whether pushed to server

    public Patient(@NonNull String patientId, long registrationDate, String firstName, String lastName, long dateOfBirth, String gender) {
        this.patientId = patientId;
        this.registrationDate = registrationDate;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.synced = false;
    }

    // convenience methods
    public String getFullName() {
        return (firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName);
    }
}
