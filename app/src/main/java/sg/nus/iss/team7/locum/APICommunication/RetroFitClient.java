package sg.nus.iss.team7.locum.APICommunication;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroFitClient {

    //public static final String BASE_URL = "http://locumapp-env.eba-p3mejsxm.ap-northeast-1.elasticbeanstalk.com";
    public static final String BASE_URL = "https://8aa1-103-252-200-84.ap.ngrok.io";
    //public static final String BASE_URL = "http://192.168.1.6:8080";
    private static Retrofit retrofit = null;

    //synchronized keyword ensures that the retrofit object is only created once
    public static synchronized Retrofit getClient(String url) {
        if (retrofit == null) {

            //log the network requests and responses
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

            //GsonConverterFactory to parse the data received from the API into a Java object.
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
