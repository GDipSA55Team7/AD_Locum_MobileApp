package sg.nus.iss.team7.locum.APICommunication;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sg.nus.iss.team7.locum.Model.FreeLancer;

public interface ApiMethods {


    @POST("/api/freeLancer/login")
    Call<FreeLancer> loginFreeLancer(@Body FreeLancer fl);

//    @POST("/api/customer/register")
//    Call<CustomerDTO> registerNewCustomer(@Body CustomerDTO customerDTO);

}
