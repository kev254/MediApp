package kevin.intellsoft.mediapp.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

import kevin.intellsoft.mediapp.model.Patient;

@Dao
public interface PatientDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(Patient patient);

    @Query("SELECT * FROM patients ORDER BY lastName, firstName")
    LiveData<List<Patient>> getAll();

    // synchronous variant used in some places
    @Query("SELECT * FROM patients ORDER BY lastName, firstName")
    List<Patient> getAllSync();

    @Query("SELECT * FROM patients WHERE patientId = :id LIMIT 1")
    Patient findById(String id);
}
