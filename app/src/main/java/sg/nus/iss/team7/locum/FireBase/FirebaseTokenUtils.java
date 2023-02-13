package sg.nus.iss.team7.locum.FireBase;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;


import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sg.nus.iss.team7.locum.APICommunication.ApiMethods;
import sg.nus.iss.team7.locum.APICommunication.RetroFitClient;
import sg.nus.iss.team7.locum.R;

public class FirebaseTokenUtils {

    private static final String LogIn = "Login Update Token";
    private static final String LogOut = "Logout Update ";
    AlertDialog dialog;

    //takes in interface as parameter
    public static void getDeviceToken(Context context, OnTokenReceivedListener listener) {
        //listener for device token retrieval result
        FirebaseApp.initializeApp(context);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    //task unsuccessful, device token retrieval failed
                    if (!task.isSuccessful()) {
                        Log.e(LogIn , String.valueOf(task.getException()));
                    }
                    //task successful, device token token retrieval success
                    String token = task.getResult();
                    Log.e(LogIn , "FCM registration token: " + token);
                    //Async interface method to pass received token to caller
                    listener.onTokenReceived(token);
                });
    }

    public interface OnTokenReceivedListener {
        void onTokenReceived(String token);
    }


    public static void updateServerOnLogout(Context context,String logoutUserName) {

        Retrofit retrofit = RetroFitClient.getClient(RetroFitClient.BASE_URL);
        ApiMethods api = retrofit.create(ApiMethods.class);

        Call<ResponseBody> logoutFLCall = api.onLogoutUpdateServer(logoutUserName);
        logoutFLCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if(response.isSuccessful() && response.code() == 200){
                    Log.e(LogOut, logoutUserName + "has logged out successfully");
                }
                else {
                    int statusCode = response.code();
                    if (statusCode == 500) {
                        Log.e(LogOut,"Internal Server Error,failed to logout username :" + logoutUserName);
                        FirebaseTokenUtils firebaseTokenUtils = new FirebaseTokenUtils();
                        firebaseTokenUtils.createDialogForLoginFailed( context,"Internal Server Error,failed to logout");
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(LogOut ,t.getMessage());
            }
        });
    }

    protected void createDialogForLoginFailed(Context context,String msg){
        dialog = new AlertDialog.Builder(context)
                .setIcon(R.drawable.ic_exit_application)
                .setTitle(context.getResources().getString(R.string.LogOutFailed))
                .setMessage(msg )
                .setCancelable(true)
                .setPositiveButton( context.getResources().getString(R.string.Ok), (dialog, id) -> dialog.dismiss())
                .show();
    }
}
