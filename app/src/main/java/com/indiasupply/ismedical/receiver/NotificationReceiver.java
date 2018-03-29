package com.indiasupply.ismedical.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive (Context context, Intent intent) {
        Log.d ("myapp", "I got this awesome intent and will now do stuff in the background!");
        String action = intent.getAction ();
        int notification_id = intent.getIntExtra ("notification_id", 0);
        int notification_style = intent.getIntExtra ("notification_style", 0);
        int notification_type = intent.getIntExtra ("notification_type", 0);
        String user_mobile = "";//intent.getStringExtra ("notification_user_mobile");

        if (intent.getExtras ().containsKey ("notification_user_mobile")) {
            user_mobile = intent.getStringExtra ("notification_user_mobile");
        }

    }
}
