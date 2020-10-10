package id.fake.gps.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import id.fake.gps.R;
import xxx.MA;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.O;
import static androidx.core.app.NotificationCompat.VISIBILITY_PRIVATE;

public class MockNotification {
    public static final int ID_MOCK_NOTIFICATION = 505;
    private static final String GROUP_ID_MOCK_STARTED = "ID_MOCK_STARTED_01";
    private static final String GROUP_NAME_MOCK_STARTED = "Mock Started";
    private NotificationManagerCompat notificationManager;
    private Context context;

    MockNotification(Context context) {
        notificationManager = NotificationManagerCompat.from((this.context = context));
        if (!notificationManager.getNotificationChannelGroups().contains(notificationManager.getNotificationChannelGroup(GROUP_ID_MOCK_STARTED)))
            if (SDK_INT >= O)
                notificationManager.createNotificationChannelGroup(new NotificationChannelGroup(GROUP_ID_MOCK_STARTED, GROUP_NAME_MOCK_STARTED));
    }

    void showNotification(String contentText) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "100")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Mock Running")
                .setContentText("position " + contentText)
                .setGroup(MockNotification.class.getSimpleName())
                .setShowWhen(false)
                .setVisibility(VISIBILITY_PRIVATE)
                .setContentIntent(
                        PendingIntent.getActivity(
                                context, 0, new Intent(context, MA.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK), 0)
                );

        if (SDK_INT >= O) {
            notificationBuilder.setChannelId("mock notification");
            notificationManager.createNotificationChannel(new NotificationChannel("mock notification", "mock notification", NotificationManager.IMPORTANCE_HIGH));
        }

        Notification notification = notificationBuilder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        notificationManager.notify(ID_MOCK_NOTIFICATION, notification);
    }
}
