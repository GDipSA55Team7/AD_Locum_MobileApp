package sg.nus.iss.team7.locum.APICommunication;


import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import sg.nus.iss.team7.locum.Model.FreeLancer;
import sg.nus.iss.team7.locum.Model.JobPost;

public interface ApiMethods {


    @POST("/api/freelancer/register")
    Call<FreeLancer> registerFreeLancer(@Body FreeLancer fl);

    @POST("/api/freelancer/login")
    Call<FreeLancer> loginFreeLancer(@Body FreeLancer fl);

    @POST("/api/freelancer/update")
    Call<ResponseBody> updateFreeLancer(@Body FreeLancer fl);

    @GET("/api/jobs/allopen")
    Call<ArrayList<JobPost>> getAllOpenJobs();

}
