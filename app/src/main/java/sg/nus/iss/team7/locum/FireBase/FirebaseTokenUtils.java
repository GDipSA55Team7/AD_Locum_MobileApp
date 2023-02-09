package sg.nus.iss.team7.locum.FireBase;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sg.nus.iss.team7.locum.APICommunication.ApiMethods;
import sg.nus.iss.team7.locum.APICommunication.RetroFitClient;

public class FirebaseTokenUtils {

    private static final String LogIn = "Login Update Token";
    private static final String LogOut = "Logout Update ";


    public static void sendTokenToServerOnLogin(String loginUserName, Context context) {
        FirebaseApp.initializeApp(context);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.e("Fetching FCM registration token failed", String.valueOf(task.getException()));
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.e("UserName: " , loginUserName);
                        Log.e("FCM registration token: " , token);
                        sendDeviceTokenAPICall(token,loginUserName);
                    }
                });
    }

    private static void sendDeviceTokenAPICall(String token,String loginUserName ) {

        Retrofit firebaseAPI = RetroFitClient.getClient(RetroFitClient.BASE_URL);
        ApiMethods api = firebaseAPI.create(ApiMethods.class);

        Call<ResponseBody> updateTokenOnLogin = api.onLoginUpdateServerToken(token,loginUserName);
        updateTokenOnLogin.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.e(LogIn, "Token sent to server successfully");
                }
                else {
                    int statusCode = response.code();
                    if (statusCode == 500) {
                        Log.e(LogIn,"Internal Server Error,failed to update token to server");
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(LogIn, "Error sending token to server: " + t.getMessage());
            }
        });
    }

    public static void updateServerOnLogout(String logoutUserName) {

        Retrofit retrofit = RetroFitClient.getClient(RetroFitClient.BASE_URL);
        ApiMethods api = retrofit.create(ApiMethods.class);

        Call<ResponseBody> logoutFLCall = api.onLogoutUpdateServer(logoutUserName);
        logoutFLCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if(response.isSuccessful() && response.code() == 200){
                    Log.e(LogOut,"Server is updated : " + logoutUserName);
                }
                else {
                    int statusCode = response.code();
                    if (statusCode == 500) {
                        Log.e(LogOut,"Internal Server Error,failed to update server of logout");
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(LogOut ,t.getMessage());
            }
        });
    }
}
