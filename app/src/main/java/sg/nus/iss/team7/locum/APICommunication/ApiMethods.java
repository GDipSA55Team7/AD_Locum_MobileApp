package sg.nus.iss.team7.locum.APICommunication;


import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import sg.nus.iss.team7.locum.Model.FreeLancer;
import sg.nus.iss.team7.locum.Model.JobPost;

public interface ApiMethods {


    @POST("/api/freelancer/register")
    Call<FreeLancer> registerFreeLancer(@Body FreeLancer fl);

//    @POST("/api/freelancer/login")
//    Call<FreeLancer> loginFreeLancer(@Body FreeLancer fl);
    @POST("/api/freelancer/login")
    Call<FreeLancer> loginFreeLancerAndUpdateToken(@Body FreeLancer fl);



    @POST("/api/freelancer/update")
    Call<FreeLancer> updateFreeLancer(@Body FreeLancer fl);

    @GET("/api/jobs/allopen")
    Call<ArrayList<JobPost>> getAllOpenJobs();

    @GET("/api/jobs/job?")
    Call<JobPost> getJobById(@Query("id") int jobId);

    @POST("/api/jobs/job?")
    Call<JobPost> setJobStatus(@Query("id") String id, @Query("status") String status, @Query("userId") String userId);

    @GET("/api/jobs/history?")
    Call<ArrayList<JobPost>> getJobHistory(@Query("id") int userId);

    @GET("/api/jobs/confirmed?")
    Call<ArrayList<JobPost>> getJobConfirmed(@Query("id") int userId);

    @GET("/api/jobs/applied?")
    Call<ArrayList<JobPost>> getJobApplied(@Query("id") int userId);


//    @GET("/api/onloginupdatetoken?")
//    Call<ResponseBody> onLoginUpdateServerToken(@Query("token") String token,@Query("username") String username);

    @GET("api/freelancer/logout?")
    Call<ResponseBody> onLogoutUpdateServer(@Query("username") String username);

    @GET("/api/jobs/recommended?")
    Call<ArrayList<JobPost>> getJobRecommended(@Query("id") int userId);

}
