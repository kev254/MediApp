package kevin.intellsoft.mediapp.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import kevin.intellsoft.mediapp.model.Vital;

@Dao
public interface VitalDao {
    @Insert
    long insert(Vital vital);

    @Query("SELECT * FROM vitals WHERE patientId = :patientId ORDER BY visitDate DESC")
    LiveData<List<Vital>> getForPatient(String patientId);

    @Query("SELECT bmi FROM vitals WHERE patientId = :patientId ORDER BY visitDate DESC LIMIT 1")
    Double getLastBmiSync(String patientId);

    // returns vitals on a specific date (date comparison by date only)
    @Query("SELECT * FROM vitals WHERE date(visitDate / 1000, 'unixepoch') = date(:visitDate / 1000, 'unixepoch')")
    List<Vital> getByVisitDate(long visitDate);

    @Query("SELECT * FROM vitals ORDER BY visitDate DESC")
    List<Vital> getAllSync();
}
