package com.zhimeng.android.ui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.zhimeng.android.R;
import com.zhimeng.android.ResultActivity;

/**
 * author：rongxianzhuo on 2016/7/14 10:25
 * email：rongxianzhuo@gmail.com
 */

public class System {

    /**
     *
     * @param context context
     * @param largeIconId 大图标id
     * @param smallIconId 小图标id
     * @param title 标题
     * @param message 消息
     * @param cls 要跳转到的Activity，可以为空
     */
    public static void simpleNotification(Context context, int largeIconId, int smallIconId, String title, String message, Class cls) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIconId))
                .setSmallIcon(smallIconId)
                .setContentTitle(title)
                .setContentText(message);
        if (cls != null) {
            Intent resultIntent = new Intent(context, ResultActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(cls);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT );
            mBuilder.setContentIntent(resultPendingIntent);
        }
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }
}
