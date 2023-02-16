package sg.nus.iss.team7.locum.FireBase;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class DismissNotificationService extends Service {
    public DismissNotificationService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int notificationId = intent.getIntExtra("notification_id", -1);
        NotificationManager notificationManager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
        stopSelf();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not implemented");
    }
}


