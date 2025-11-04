package kevin.intellsoft.mediapp.network;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("user/signup")
    Call<JsonObject> signup(@Body JsonObject body);

    @POST("user/login")
    Call<JsonObject> login(@Body JsonObject body);

    @POST("patients/register")
    Call<JsonObject> registerPatient(@Body JsonObject body);

    @GET("patients/list")
    Call<JsonObject> listPatients();

    @GET("patients/show/{id}")
    Call<JsonObject> showPatient(@Path("id") String id);

    @POST("vitals/add")
    Call<JsonObject> addVitals(@Body JsonObject body);

    @POST("visits/view")
    Call<JsonObject> visitsView(@Body JsonObject body);

    @GET("visits/add")
    Call<JsonObject> visitsAdd(@Query("patient_id") String patientId, @Query("type") String type);
}