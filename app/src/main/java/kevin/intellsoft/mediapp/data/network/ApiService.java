package kevin.intellsoft.mediapp.data.network;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    // User endpoints
    @POST("user/signup")
    Call<JsonObject> signup(@Body JsonObject body);

    @POST("user/signin")
    Call<JsonObject> login(@Body JsonObject body);

    // Patient endpoints (future use)
    @POST("patients/register")
    Call<JsonObject> registerPatient(@Body JsonObject body);

    @GET("patients/list")
    Call<JsonObject> getPatients(@Header("Authorization") String token);

    @GET("patients/show/{id}")
    Call<JsonObject> showPatient(@Header("Authorization") String token, @Path("id") String id);

    @POST("vital/add")
    Call<JsonObject> addVitals(@Body JsonObject body);
    @POST("visits/add")
    Call<JsonObject> addAssessment(@Body JsonObject body);


}
