package kevin.intellsoft.mediapp.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import kevin.intellsoft.mediapp.data.local.entity.Patient;

@Dao
public interface PatientDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(Patient patient);

    @Query("SELECT * FROM patients ORDER BY lastName, firstName")
    LiveData<List<Patient>> getAllLive();

    @Query("SELECT * FROM patients ORDER BY lastName, firstName")
    List<Patient> getAllSync();

    @Query("SELECT * FROM patients WHERE patientId = :id LIMIT 1")
    Patient findById(String id);

    @Query("UPDATE patients SET synced = :synced WHERE patientId = :patientId")
    void setPatientSynced(String patientId, boolean synced);

    @Update
    void update(Patient patient);

    @Query("SELECT * FROM patients WHERE synced = 0")
    List<Patient> getUnsynced();
}
