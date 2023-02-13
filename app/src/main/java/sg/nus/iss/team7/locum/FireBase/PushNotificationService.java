package sg.nus.iss.team7.locum.FireBase;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;


import sg.nus.iss.team7.locum.R;

public class PushNotificationService extends FirebaseMessagingService {

    final String CHANNEL_ID = "CHANNEL_NOTIFICATION_ID";
    final String CHANNEL = "CHANNEL_NOTIFICATION";

    @Override
    public void onCreate() {
        super.onCreate();
        //Channel is created once
        createNotificationChannel();
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
            String username = data.get("username");
            String redirectToTargetActivity = data.get("click_action");
            Log.e("Data Message Received : " , String.valueOf(remoteMessage.getData()));
            if (jobid != null) {
                createNotification(redirectToTargetActivity,title,body,jobid,username);
            }
        }
        super.onMessageReceived(remoteMessage);
    }

//     * There are two scenarios when onNewToken is called:
//     * 1) When a new token is generated on initial app startup
//     * 2) Whenever an existing token is changed
//     * Under #2, there are two scenarios when the existing token is changed:
//     * A) App is restored to a new device
//     * B) User uninstalls/reinstalls the app
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    private void createNotification(String activityToDirectTo,String title,String body,String jobid,String username){

        try {
            //generating a unique identifier for the notification
            int notificationId = jobid.hashCode();

            //Set up 2 actions("VIEW" and "DISMISS") for notification

            //pendingIntent to direct to targetActivity onClicking "VIEW"
            Class<?> cls = Class.forName(activityToDirectTo);
            Intent intent = new Intent(this, cls);
            intent.putExtra("itemId", Integer.valueOf(jobid));
            intent.putExtra("notificationTargetUserName", username);
            intent.putExtra("fromNotification", true);
            intent.putExtra("cancelNotificationOnSystemTray", notificationId);

            //set the FLAG_ONE_SHOT flag to ensure that the user is only redirected to the job details activity once.
            PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent,PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Action viewAction = new NotificationCompat.Action
                    .Builder(R.drawable.ic_notifications_status_change, "VIEW", pendingIntent)
                    .build();

            //pendingIntent to start a service to cancel notification onClicking "DISMISS"
            Intent dismissIntent = new Intent(this, DismissNotificationService.class);
            dismissIntent.putExtra("notification_id", notificationId);
            //FLAG_UPDATE_CURRENT flag to cancel the notification. This way, the user can either view the job details or dismiss the notification, but not both
            PendingIntent dismissPendingIntent = PendingIntent.getService(this, notificationId, dismissIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Action DismissAction = new NotificationCompat.Action
                    .Builder(R.drawable.ic_dismiss_notification, "DISMISS", dismissPendingIntent)
                    .build();
            //start background thread to not hog main UI thread as
            // decodingResource to bitmap is an expensive operation that can take some time to complete
            new Thread(() -> {
                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_notifications_status_change);

                //Creating the notification object
                NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setSmallIcon(R.drawable.ic_baseline_notifications)
                        .setLargeIcon(largeIcon)
                        .setColor(ContextCompat.getColor(this, R.color.light_grey))
                        .setAutoCancel(true)
                        .addAction(viewAction)
                        .addAction(DismissAction);

                NotificationManagerCompat.from(this).notify(notificationId, notification.build());
            }).start();

        } catch (ClassNotFoundException e) {
            Log.e("createNotification","error with either converting string to class or with notification creation");
            e.printStackTrace();
        }
    }

    private void createNotificationChannel(){

        //Creating a notification channel for  version >= Android 8.1 (Oreo)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL, NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, new AudioAttributes.Builder().build());
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }
}
