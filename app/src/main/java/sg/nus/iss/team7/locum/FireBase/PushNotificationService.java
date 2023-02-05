package sg.nus.iss.team7.locum.FireBase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sg.nus.iss.team7.locum.APICommunication.ApiMethods;
import sg.nus.iss.team7.locum.APICommunication.RetroFitClient;
import sg.nus.iss.team7.locum.MainActivity;
import sg.nus.iss.team7.locum.Model.FreeLancer;
import sg.nus.iss.team7.locum.R;

public class PushNotificationService extends FirebaseMessagingService {

    private static final String TAG = "PushNotificationService";

    @Override
    public void onCreate() {
        super.onCreate();
        //Retrieve the current registration token
//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        if (!task.isSuccessful()) {
//                            Log.e(TAG, "Fetching FCM registration token failed", task.getException());
//                            return;
//                        }
//                        // Get new FCM registration token
//                        String token = task.getResult();
//                        // Log and toast
//                        Log.e(TAG, "FCM registration token: " + token);
//                        sendUpdateDeviceTokenToServer(token);
//                    }
//                });
    }



    // Data messages are handled here in onMessageReceived whether the app is in the foreground or background.
    //Notification messages are only received here in onMessageReceived when the app is in the foreground.
    // When the app is in the background an automatically generated notification is displayed.
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            String title = data.get("title");
            String body = data.get("body");
            String jobid = data.get("jobid");
            Log.e(TAG, "Data Message - Notification Title: " + title);
            Log.e(TAG, "Data Message -Notification Body: " + body);
            Log.e(TAG, "Data Message -Message data payload: " + remoteMessage.getData());
            createNotification(data.get("click_action"),title,body,jobid);
        }
        else{
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            Log.e("title from firebase",title);
            Log.e("text from firebase",body);

            createNotification("sg.nus.iss.team7.locum.JobDetailActivity",title,body,"0");
        }
        super.onMessageReceived(remoteMessage);

    }

//    /**
//     * There are two scenarios when onNewToken is called:
//     * 1) When a new token is generated on initial app startup
//     * 2) Whenever an existing token is changed
//     * Under #2, there are three scenarios when the existing token is changed:
//     * A) App is restored to a new device
//     * B) User uninstalls/reinstalls the app
//     * C) User clears app data
//     */
//    @Override
//    public void onNewToken(@NonNull String token) {
//
//        Log.e("Refreshed token: " ,token);
//        sendUpdateDeviceTokenToServer(token);
//        super.onNewToken(token);
//    }

    private void sendUpdateDeviceTokenToServer(String token) {

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

    private void createNotification(String activityToDirectTo,String title,String body,String jobid){
        final String CHANNEL_ID = "HEADS_UP_NOTIFICATION_ID";

        try {
            Class<?> cls = Class.forName(activityToDirectTo);
            Intent intent = new Intent(this, cls);
            intent.putExtra("itemId", Integer.valueOf( jobid));
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent =PendingIntent.getActivity(this, jobid.hashCode(),intent,PendingIntent.FLAG_ONE_SHOT);

            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, "Heads Up Notification", NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            channel.setVibrationPattern(
                    new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, new AudioAttributes.Builder().build());

            getSystemService(NotificationManager.class).createNotificationChannel(channel);

            new Thread(() -> {
                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_notifications_status_change);

                Notification.Builder notification = new Notification
                        .Builder(this, CHANNEL_ID)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setSmallIcon(R.drawable.ic_baseline_notifications)
                        .setLargeIcon(largeIcon)
                        .setColor(ContextCompat.getColor(this, R.color.light_grey))
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

                NotificationManagerCompat.from(this).notify(1, notification.build());
            }).start();

        } catch (ClassNotFoundException e) {
            // Handle the exception if the class could not be found
            Log.e("createNotification","error with either converting string to class or with notification creation");
            e.printStackTrace();
        }
    }
}
