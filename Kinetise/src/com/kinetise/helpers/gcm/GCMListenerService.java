package com.kinetise.helpers.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;
import com.kinetise.components.activity.KinetiseActivity;
import com.kinetise.helpers.RWrapper;
import com.kinetise.support.logger.Logger;

public class GCMListenerService extends GcmListenerService {

    private static int NOTIFICATION_ID = 0;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);

        Logger.v(this, "onMessage, from: " + from + " data: " + data.getString("CMD"));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        long when = System.currentTimeMillis(); // notification time
        CharSequence contentTitle;
        // expanded message
        if (data.containsKey("title")) {
            contentTitle = data.getString("title");
        } else {
            contentTitle = getString(RWrapper.string.app_name);
        }
        CharSequence contentText = data.getString("message"); // expanded
        Intent notificationIntent = new Intent(this, KinetiseActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        // the next two lines initialize the Notification, using the
        // configurations above

        Notification notification;
        int smallIcon;

        Notification.Builder builder = new Notification.Builder(getApplicationContext())
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setWhen(when)
                .setContentIntent(contentIntent);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            smallIcon = RWrapper.drawable.notification_icon_since_android_l;
            Bitmap icon = BitmapFactory.decodeResource(getResources(), RWrapper.drawable.getIcon());

            builder.setSmallIcon(smallIcon)
                    .setLargeIcon(icon)
                    .setStyle(new Notification.BigTextStyle()
                            .bigText(contentText));
            notification = builder.build();
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            builder.setSmallIcon(RWrapper.drawable.getIcon())
                    .setStyle(new Notification.BigTextStyle()
                            .bigText(contentText));
            notification = builder.build();
        } else {
            builder.setSmallIcon(RWrapper.drawable.getIcon());
            notification = builder.getNotification();
        }


        notification.ledARGB = 0xff0000ff;
        notification.ledOnMS = 300;
        notification.ledOffMS = 300;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE;
        notification.contentIntent = contentIntent;

        notificationManager.notify(++NOTIFICATION_ID, notification);
    }

}
