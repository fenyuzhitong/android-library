

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

    /**
     *
     * @param urls url数组，格式  url1,url2,url3...
     * @return
     */
    public static String[] urlsToArray(String urls) {
        byte[] string = urls.getBytes();
        int count = 1;
        int i = 0;
        while (i < string.length) if (string[i++] == ',') count++;
        String[] result = new String[count];
        int index = urls.indexOf(',');
        if (index != -1) result[0] = urls.substring(0, urls.indexOf(','));
        else result[0] = urls;
        int start = result[0].length() + 1;
        i = 1;
        while (i < count) {
            index = urls.indexOf(',', start);
            if (index != -1) result[i] = urls.substring(start, urls.indexOf(','));
            else {
                result[i] = urls.substring(start);
                break;
            }
            start+=result[i].length();
            i++;
        }
        return result;
    }

    /**
     * 时间戳转换成日期格式字符串
     * @param seconds 精确到秒的字符串
     * @return
     */
    public static String timeStampToDate(String seconds) {
        if(seconds == null || seconds.isEmpty() || seconds.equals("null")) return "";
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds+"000")));
    }

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
            Intent resultIntent = new Intent(context, cls);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(cls);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT );
            mBuilder.setContentIntent(resultPendingIntent);
        }
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

    /**
     *
     * @param context context
     * @param largeIconId 大图标id
     * @param smallIconId 小图标id
     * @param title 标题
     * @param message 消息
     * @param cls 要跳转到的Activity，可以为空
     * @param intent 自定义跳转到cls的意图
     */
    public static void simpleNotification(Context context, int largeIconId, int smallIconId, String title, String message, Class cls, Intent intent) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIconId))
                .setSmallIcon(smallIconId)
                .setContentTitle(title)
                .setContentText(message);
        if (cls != null) {
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(cls);
            stackBuilder.addNextIntent(intent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT );
            mBuilder.setContentIntent(resultPendingIntent);
        }
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

}
