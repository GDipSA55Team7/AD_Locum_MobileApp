package sg.nus.iss.team7.locum.APICommunication;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroFitClient {

    //public static final String BASE_URL = "https://c39c-103-252-200-108.ap.ngrok.io/"; // IPV4 ip address
    public static final String BASE_URL = "https://ef56-151-192-127-55.ap.ngrok.io/";
    private static Retrofit retrofit = null;

    public static synchronized Retrofit getClient(String url) {
        if (retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
