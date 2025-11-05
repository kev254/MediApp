package kevin.intellsoft.mediapp.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import kevin.intellsoft.mediapp.data.local.entity.Vital;

@Dao
public interface VitalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Vital vital);

    @Query("SELECT * FROM vitals WHERE patientId = :patientId ORDER BY visitDate DESC LIMIT 1")
    Vital getLatestByPatient(String patientId);

    @Query("SELECT * FROM vitals WHERE visitDate BETWEEN :start AND :end ORDER BY visitDate DESC")
    List<Vital> getByVisitDateRange(long start, long end);

    @Query("SELECT * FROM vitals WHERE synced = 0")
    List<Vital> getUnsynced();

    @Query("UPDATE vitals SET synced = :synced WHERE id = :id")
    void setSynced(int id, boolean synced);
    @Query("SELECT bmi FROM vitals WHERE patientId = :patientId ORDER BY visitDate DESC LIMIT 1")
    Double getLatestBmiForPatient(String patientId);

}
