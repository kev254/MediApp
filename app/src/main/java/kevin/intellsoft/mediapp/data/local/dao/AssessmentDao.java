package kevin.intellsoft.mediapp.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;
import kevin.intellsoft.mediapp.data.local.entity.Assessment;

@Dao
public interface AssessmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Assessment a);

    @Query("SELECT * FROM assessments WHERE patientId = :patientId ORDER BY visitDate DESC")
    List<Assessment> getByPatient(String patientId);

    @Query("SELECT * FROM assessments WHERE synced = 0")
    List<Assessment> getUnsynced();

    @Query("UPDATE assessments SET synced = :synced WHERE id = :id")
    void setSynced(int id, boolean synced);
}
