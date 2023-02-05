package sg.nus.iss.team7.locum.FireBase;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sg.nus.iss.team7.locum.APICommunication.ApiMethods;
import sg.nus.iss.team7.locum.APICommunication.RetroFitClient;

public class FirebaseTokenUtils {

    private static final String TAG = "PushNotificationService";


    public static void retrieveDeviceTokenAndSendToServer() {
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
                        Log.e("FCM registration token: " , token);
                        sendUpdateDeviceTokenToServer( token);
                    }
                });
    }

    private static void sendUpdateDeviceTokenToServer( String token) {
        // Send the device token to server
        System.out.println("Sending Token" + token);
        Retrofit firebaseAPI = RetroFitClient.getClient(RetroFitClient.BASE_URL);
        ApiMethods api = firebaseAPI.create(ApiMethods.class);

        Call<ResponseBody> sendDeviceTokenCall = api.sendDeviceToken(token);

        sendDeviceTokenCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "Token sent to server successfully");
                } else {
                    Log.e(TAG, "Failed to send token to server");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Error sending token to server: " + t.getMessage());
            }
        });
    }
}
