package com.mifos.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.mifos.mifosxdroid.online.notification.NotificationDetailActivity;
import com.mifos.mifosxdroid.R;
import com.mifos.objects.NotificationTemplate;

import java.util.Date;

/**
 * Created by mayankjindal on 12/08/17.
 */

public class NotificationUtil {

    private static String subject;
    private static String object;
    private static String action;
    private static String actor;
    private static Context mContext;
    private static NotificationManager mNotificationManager;
    private static NotificationCompat.Builder mNotificationBuilder;

    public static void createNotification(Context context,
                                 NotificationTemplate notificationTemplate) {
        mContext = context;
        subject = notificationTemplate.getNotificationContent().getObject();
        object = notificationTemplate.getNotificationContent().getObject();
        action = notificationTemplate.getNotificationContent().getAction();
        actor = notificationTemplate.getNotificationContent().getActor();
        mNotificationManager = (NotificationManager) context.getApplicationContext().
                getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationBuilder = createNotificationBuider();
        showNotification();
    }

    private static NotificationCompat.Builder createNotificationBuider() {
        Uri notificationSound = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        return new NotificationCompat.Builder(mContext).setSmallIcon(R.drawable.mifos_logo)
                .setContentTitle(object + action)
                .setContentText(subject + object + action + actor)
                .setSound(notificationSound)
                .setAutoCancel(true);
    }

    private static void showNotification() {
        Intent notificationIntent = new Intent(mContext, NotificationDetailActivity.class);
        notificationIntent.putExtra(Constants.NOTIFICATION_SUBJECT, subject);
        notificationIntent.putExtra(Constants.NOTIFICATION_OBJECT, object);
        notificationIntent.putExtra(Constants.NOTIFICATION_ACTION, action);
        notificationIntent.putExtra(Constants.NOTIFICATION_ACTOR, actor);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(NotificationDetailActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPending = stackBuilder
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mNotificationBuilder.setContentIntent(notificationPending);
        mNotificationManager.notify((int) (new Date().getTime()),
                mNotificationBuilder.build());
    }

}
