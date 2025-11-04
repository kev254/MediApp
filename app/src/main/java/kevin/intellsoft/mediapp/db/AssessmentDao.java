package kevin.intellsoft.mediapp.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import kevin.intellsoft.mediapp.model.AssessmentGeneral;
import kevin.intellsoft.mediapp.model.AssessmentOverweight;

@Dao
public interface AssessmentDao {
    @Insert
    long insertGeneral(AssessmentGeneral general);

    @Insert
    long insertOverweight(AssessmentOverweight over);

    @Query("SELECT * FROM assessment_general WHERE patientId = :patientId ORDER BY visitDate DESC")
    List<AssessmentGeneral> getGeneralForPatient(String patientId);

    @Query("SELECT * FROM assessment_overweight WHERE patientId = :patientId ORDER BY visitDate DESC")
    List<AssessmentOverweight> getOverweightForPatient(String patientId);
}