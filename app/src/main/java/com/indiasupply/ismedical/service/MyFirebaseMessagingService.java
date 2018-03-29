package com.indiasupply.ismedical.service;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.indiasupply.ismedical.activity.MainActivity;
import com.indiasupply.ismedical.model.Notification;
import com.indiasupply.ismedical.utils.AppConfigTags;
import com.indiasupply.ismedical.utils.Constants;
import com.indiasupply.ismedical.utils.NotificationUtils;
import com.indiasupply.ismedical.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("karman", "message received");
//        Utils.showLog (Log.DEBUG, TAG, "from " + remoteMessage.getFrom (), true);
//        Utils.showLog (Log.DEBUG, TAG, "To " + remoteMessage.getTo (), true);
//        Utils.showLog (Log.DEBUG, TAG, "Collapse Key " + remoteMessage.getCollapseKey (), true);
//        Utils.showLog (Log.DEBUG, TAG, "Message ID " + remoteMessage.getMessageId (), true);
//        Utils.showLog (Log.DEBUG, TAG, "Message Type" + remoteMessage.getMessageType (), true);
//        Utils.showLog (Log.DEBUG, TAG, "Sent Time " + remoteMessage.getSentTime (), true);
//        Utils.showLog (Log.DEBUG, TAG, "TTL " + remoteMessage.getTtl (), true);

        if (remoteMessage == null)
            return;
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Utils.showLog(Log.DEBUG, TAG, "Notification Body: " + remoteMessage.getNotification().getBody(), true);
            handleNotification(remoteMessage.getNotification().getBody());
        }
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Utils.showLog(Log.DEBUG, TAG, "Data Payload: " + remoteMessage.getData().toString(), true);
            try {
                handleDataMessage(new JSONObject (remoteMessage.getData().toString()));
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
//        if (! NotificationUtils.isAppIsInBackground (getApplicationContext ())) {
//            // app is in foreground, broadcast the push message
//            Intent pushNotification = new Intent (Constants.PUSH_NOTIFICATION);
//            pushNotification.putExtra ("message", message);
//            LocalBroadcastManager.getInstance (this).sendBroadcast (pushNotification);
//            // play notification sound
//            NotificationUtils notificationUtils = new NotificationUtils (getApplicationContext ());
//            notificationUtils.playNotificationSound ();
//        } else {


        // If the app is in background, firebase itself handles the notification
//        }
    }

    private void handleDataMessage(JSONObject notificationData) {
        Notification notification = new Notification();
        try {
            JSONObject data = notificationData.getJSONObject("data");
            notification.setBackground (data.getBoolean (AppConfigTags.NOTIFICATION_IS_BACKGROUND));
            notification.setTitle (data.getString (AppConfigTags.NOTIFICATION_TITLE));
            notification.setMessage (data.getString (AppConfigTags.NOTIFICATION_MESSAGE));
            notification.setImage_url (data.getString (AppConfigTags.NOTIFICATION_IMAGE));
            notification.setTimestamp (data.getString (AppConfigTags.NOTIFICATION_TIMESTAMP));
            notification.setPayload (data.getJSONObject (AppConfigTags.NOTIFICATION_PAYLOAD));
    
            JSONObject payload = data.getJSONObject (AppConfigTags.NOTIFICATION_PAYLOAD);
    
            notification.setNotification_type (payload.getInt (AppConfigTags.NOTIFICATION_TYPE));
            notification.setNotification_priority (payload.getInt (AppConfigTags.NOTIFICATION_PRIORITY));
        } catch (JSONException e) {
            Log.e (TAG, "JSON Exception: " + e.getMessage ());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }

        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            Intent intent = new Intent (Constants.PUSH_NOTIFICATION);
            intent.putExtra (AppConfigTags.NOTIFICATION_MESSAGE, notification.getMessage ());
            notificationUtils = new NotificationUtils(getApplicationContext());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            notificationUtils.showNotificationMessage(intent, notification);
        } else {
            // app is in background, show the notification in notification tray
            Intent intent = new Intent (getApplicationContext (), MainActivity.class);
            intent.putExtra (AppConfigTags.NOTIFICATION_MESSAGE, notification.getMessage ());
            notificationUtils = new NotificationUtils(getApplicationContext());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            notificationUtils.showNotificationMessage(intent, notification);
        }
    }
}