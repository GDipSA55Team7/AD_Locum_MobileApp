package sg.nus.iss.team7.locum.FireBase;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

public class DismissNotificationService extends Service {
    public DismissNotificationService() {
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        int notificationId = intent.getIntExtra("notification_id", -1);
        Log.e("DismissNotificationService started","notification id : " + notificationId);
        NotificationManager notificationManager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
        //notificationManager.cancelAll();
        stopSelf();
        return START_NOT_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}


