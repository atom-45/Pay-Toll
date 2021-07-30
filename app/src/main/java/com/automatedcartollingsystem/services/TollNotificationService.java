package com.automatedcartollingsystem.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.automatedcartollingsystem.appfunctionality.MapsActivity;
import com.automatedcartollingsystem.appfunctionality.ScannerActivity;
import com.automatedcartollingsystem.appfunctionality.TransactionHistoryActivity;
import com.example.automatedcartollingsystem.R;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class TollNotificationService extends IntentService {

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS

    // TODO: Rename parameters
    public static final String EXTRA_MESSAGE = "message";
    private static final int NOTIFICATION_ID = 5453;

    public TollNotificationService() {
        super("TollNotificationService");
    }

    /*
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     *
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1) {
        Intent intent = new Intent(context, TollNotificationService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_MESSAGE, param1);
        context.startService(intent);
    }*/

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public void showNotification(String text) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String name1 = "Transaction notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_SERVICE,name1,importance);
            notificationChannel.setDescription("This is a notification");
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);


            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Intent actionIntent = new Intent(this, TransactionHistoryActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntentWithParentStack(actionIntent);
        PendingIntent pendingIntent = taskStackBuilder
                .getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(this,NOTIFICATION_SERVICE)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle("Tollgate Payment")
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setChannelId(NOTIFICATION_SERVICE)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_SERVICE,NOTIFICATION_ID,builder.build());


        //NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //notificationManager.notify(NOTIFICATION_ID, builder.build());

        /**Intent intent = new Intent(context, TollNotificationService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_MESSAGE, text);
        context.startService(intent);**/
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            synchronized (this) {
                try{
                    wait(100);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            String text = intent.getStringExtra(EXTRA_MESSAGE);
            showNotification(text);
        }
    }
}