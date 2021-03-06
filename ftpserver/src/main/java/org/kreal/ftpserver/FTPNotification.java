package org.kreal.ftpserver;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.kreal.ftpserver.configure.SharedConfigure;

/**
 * Helper class for showing and canceling ftp
 * notifications.
 * <p/>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class FTPNotification {
    /**
     * The unique identifier for this type of notification.
     */
    private static final String NOTIFICATION_TAG = "FTP";

    /**
     * Shows the notification, or updates a previously shown notification of
     * this type, with the given parameters.
     * <p/>
     * TODO: Customize this method's arguments to present relevant content in
     * the notification.
     * <p/>
     * TODO: Customize the contents of this method to tweak the behavior and
     * presentation of ftp notifications. Make
     * sure to follow the
     * <a href="https://developer.android.com/design/patterns/notifications.html">
     * Notification design guidelines</a> when doing so.
     *
     * @see #cancel(Context)
     */
    public static void notify(final Context context,
                              final String exampleString, final int number) {
        final Resources res = context.getResources();

        // This image is used as the notification's large icon (thumbnail).
        // TODO: Remove this if your notification has no relevant thumbnail.
        final String info = String.format("ftp://%s:%s@%s:%d",SharedConfigure.getUser(context),SharedConfigure.getPassword(context),exampleString,SharedConfigure.getPort(context));
        Bitmap picture;
        try {
            picture = BitMatrixToBitmap(new MultiFormatWriter().encode(info, BarcodeFormat.QR_CODE, 500, 500));
        } catch (WriterException e) {
            e.printStackTrace();
            picture = BitmapFactory.decodeResource(res, R.drawable.example_picture);
        }

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)

                // .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_stat_ftp)
                .setContentText(info)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setLargeIcon(picture)
                //.setTicker(ticker)
                //.setNumber(number)
                .setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                0,
                                ShowFTPserverQR.startQRActivityIntent(context,info),
                                PendingIntent.FLAG_UPDATE_CURRENT))
                .setOngoing(true);
        if (number == 1)
            builder
                    .setContentTitle("FTP Server Working >>>")
//                    .addAction(R.drawable.abc_ic_menu_copy_mtrl_am_alpha, "PAUSE", PendingIntent.getBroadcast(context, 0, new Intent(FtpServerAndroid.ACTION_PAUSE_FTPSERVER), PendingIntent.FLAG_UPDATE_CURRENT))
                    .addAction(R.drawable.abc_ic_menu_copy_mtrl_am_alpha, "STOP SERVICE", PendingIntent.getBroadcast(context, 0, new Intent(FtpServerAndroid.ACTION_STOP_FTPSERVER), PendingIntent.FLAG_UPDATE_CURRENT));
//        else if (number == 2)
//            builder
//                    .setContentTitle("FTP Serer Paused <<<")
//                    .addAction(R.drawable.abc_ic_menu_copy_mtrl_am_alpha, "RESUME", PendingIntent.getBroadcast(context, 0, new Intent(FtpServerAndroid.ACTION_RESUME_FTPSERVER), PendingIntent.FLAG_UPDATE_CURRENT))
//                    .addAction(R.drawable.abc_ic_menu_copy_mtrl_am_alpha, "STOP", PendingIntent.getBroadcast(context, 0, new Intent(FtpServerAndroid.ACTION_STOP_FTPSERVER), PendingIntent.FLAG_UPDATE_CURRENT));
        else
            builder
                    .setContentTitle("FTP Server Create Failed  >_<")
                    .setOngoing(false);
        notify(context, builder.build());
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG, 0, notification);
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
        }
    }

    /**
     * Cancels any notifications of this type previously shown using
     * {@link #notify(Context, String, int)}.
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG, 0);
        } else {
            nm.cancel(NOTIFICATION_TAG.hashCode());
        }
    }

    public static Bitmap BitMatrixToBitmap(BitMatrix bitMatrix) {
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < width; ++y) {
            for (int x = 0; x < height; ++x) {
                pixels[y * width + x] = bitMatrix.get(x, y) ? 0xff000000 : 0xffffffff; // black pixel
            }
        }
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmp;
    }
}
