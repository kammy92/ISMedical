package com.indiasupply.ismedical.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bugsnag.android.Bugsnag;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.indiasupply.ismedical.R;
import com.indiasupply.ismedical.fragment.ContactsFragment;
import com.indiasupply.ismedical.fragment.EventFragment;
import com.indiasupply.ismedical.utils.AppConfigTags;
import com.indiasupply.ismedical.utils.AppConfigURL;
import com.indiasupply.ismedical.utils.Constants;
import com.indiasupply.ismedical.utils.NetworkConnection;
import com.indiasupply.ismedical.utils.SetTypeFace;
import com.indiasupply.ismedical.utils.UserDetailsPref;
import com.indiasupply.ismedical.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_LOGIN_SCREEN_RESULT = 2;
    CoordinatorLayout clMain;
    BottomNavigationView bottomNavigationView;
    UserDetailsPref userDetailsPref;
    
    boolean doubleBackToExitPressedOnce = false;
    
    
    int notification_type = 0;
    int event_id = 0;
    int offer_id = 0;
    
    ArrayList<Integer> screenList = new ArrayList<> ();
    
    private FirebaseAnalytics mFirebaseAnalytics;
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activtiy_main);
        initExtras ();
        initView ();
        initData ();
        initListener ();
        isLogin ();
    }
    
    private void initFirstFragment () {
        bottomNavigationView.getMenu ().findItem (R.id.action_item_events).setIcon (R.drawable.ic_home_events_filled);
        FragmentTransaction transaction = getSupportFragmentManager ().beginTransaction ();
        transaction.replace (R.id.frame_layout, EventFragment.newInstance (false));
        transaction.commit ();
    }
    
    private void initView () {
        clMain = (CoordinatorLayout) findViewById (R.id.clMain);
        bottomNavigationView = (BottomNavigationView) findViewById (R.id.navigation);
    }
    
    private void initData () {
//        Window window = getWindow ();
//        if (Build.VERSION.SDK_INT >= 21) {
//            window.clearFlags (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.addFlags (WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor (ContextCompat.getColor (this, R.color.text_color_white));
//        }
        Bugsnag.init (this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance (this);
        
      
    
        Utils.setTypefaceToAllViews (this, clMain);
        Utils.disableShiftMode (bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu ();
        menu.findItem (R.id.action_item_events).setIcon (R.drawable.ic_home_events);

//        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams ();
//        layoutParams.setBehavior (new BottomNavigationViewBehavior ());

        
        
        FirebaseDynamicLinks.getInstance ()
                .getDynamicLink (getIntent ())
                .addOnSuccessListener (this, new OnSuccessListener<PendingDynamicLinkData> () {
                    @Override
                    public void onSuccess (PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Utils.showLog (Log.INFO, "Deep Link", "Successfull Deeplink", true);
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink ();
                        }
    
                        if (deepLink != null) {
                            String path = deepLink.getPath ();
                            Utils.showLog (Log.INFO, "Deep Link Path", "DeepLink Path " + path, true);
        
                            String[] parts = path.split ("/");
                            for (int i = 0; i < parts.length; i++) {
//                                Log.e ("karman", "in loop " + parts[i]);
                            }
    
                            if (parts[1].equalsIgnoreCase ("event")) {
                                Utils.showLog (Log.INFO, "Deep Link", "in if", true);
                                Intent intent = new Intent (MainActivity.this, EventDetailActivity.class);
                                intent.putExtra (AppConfigTags.EVENT_ID, Integer.parseInt (parts[2]));
                                startActivity (intent);
                                overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        } else {
                        }
                    }
                })
                .addOnFailureListener (this, new OnFailureListener () {
                    @Override
                    public void onFailure (@NonNull Exception e) {
                        Utils.showLog (Log.ERROR, "Deep Link", "Error occurred " + e, true);
                    }
                });
    }
    
    private void isLogin () {
        userDetailsPref = UserDetailsPref.getInstance ();
        if (userDetailsPref.getStringPref (MainActivity.this, UserDetailsPref.USER_LOGIN_KEY).length () == 0) {
            Intent intent = new Intent (MainActivity.this, LoginActivity.class);
            startActivityForResult (intent, REQUEST_LOGIN_SCREEN_RESULT);
            overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
        } else {
            initApplication ();
    
            FragmentTransaction transaction = getSupportFragmentManager ().beginTransaction ();
            if (notification_type > 0) {
                switch (notification_type) {
                    case 2:
                        bottomNavigationView.getMenu ().findItem (R.id.action_item_events).setChecked (true).setIcon (R.drawable.ic_home_events_filled);
                        transaction.replace (R.id.frame_layout, EventFragment.newInstance2 (false, event_id));
                        transaction.commit ();
                        break;
                    default:
                        initFirstFragment ();
                        break;
                }
            } else {
                initFirstFragment ();
            }
        }
    }
    
    private void initExtras () {
        Intent intent = getIntent ();
        Bundle extras = intent.getExtras ();
        if (extras != null) {
            if (extras.containsKey (AppConfigTags.NOTIFICATION_TYPE)) {
                notification_type = intent.getIntExtra (AppConfigTags.NOTIFICATION_TYPE, 0);
                switch (notification_type) {
                    case 2:
                        event_id = intent.getIntExtra (AppConfigTags.EVENT_ID, 0);
                        break;
                    case 3:
                        offer_id = intent.getIntExtra (AppConfigTags.OFFER_ID, 0);
                        break;
                }
            }
        }
    }
    
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGIN_SCREEN_RESULT) {
            if (data.getBooleanExtra ("LOGIN", false)) {
                initFirstFragment ();
                initApplication ();
            } else {
                finish ();
            }
        }
    }
    
    private void initListener () {
        bottomNavigationView.setOnNavigationItemReselectedListener (new BottomNavigationView.OnNavigationItemReselectedListener () {
            @Override
            public void onNavigationItemReselected (@NonNull MenuItem item) {
                switch (item.getItemId ()) {
                    case R.id.action_item_events:
//                        Utils.showToast (MainActivity.this, "In reselected item 2", false);
                        break;
                    case R.id.action_item_contacts:
//                        Utils.showToast (MainActivity.this, "In reselected item 3", false);
                        break;
                }
            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener () {
                    @Override
                    public boolean onNavigationItemSelected (@NonNull MenuItem item) {
                        Menu menu = bottomNavigationView.getMenu ();
                        menu.findItem (R.id.action_item_events).setIcon (R.drawable.ic_home_events);
                        menu.findItem (R.id.action_item_contacts).setIcon (R.drawable.ic_home_contacts);
    
                        Fragment selectedFragment = null;
                        switch (item.getItemId ()) {
                            case R.id.action_item_events:
                                // [START custom_event]
                                Bundle params = new Bundle ();
                                params.putBoolean ("clicked", true);
                                mFirebaseAnalytics.logEvent ("home_events", params);
                                // [END custom_event]
                                
                                item.setIcon (R.drawable.ic_home_events_filled);
                                if (screenList.contains (R.id.action_item_events)) {
                                    selectedFragment = EventFragment.newInstance (false);
                                } else {
                                    selectedFragment = EventFragment.newInstance (true);
                                }
                                screenList.add (R.id.action_item_events);
                                break;
                            case R.id.action_item_contacts:
                                // [START custom_event]
                                Bundle params2 = new Bundle ();
                                params2.putBoolean ("clicked", true);
                                mFirebaseAnalytics.logEvent ("home_contacts", params2);
                                // [END custom_event]
                              
                                item.setIcon (R.drawable.ic_home_contacts_filled);
                                if (screenList.contains (R.id.action_item_contacts)) {
                                    selectedFragment = ContactsFragment.newInstance (false);
                                } else {
                                    selectedFragment = ContactsFragment.newInstance (true);
                                }
                                screenList.add (R.id.action_item_contacts);
                                break;
                        }
                        FragmentTransaction transaction = getSupportFragmentManager ().beginTransaction ();
                        transaction.replace (R.id.frame_layout, selectedFragment);
                        transaction.commit ();
                        return true;
                    }
                });
    }
    
    @Override
    public void onBackPressed () {
/*
        MaterialDialog dialog = new MaterialDialog.Builder (this)
                .content (R.string.dialog_text_quit_application)
                .positiveColor (getResources ().getColor (R.color.primary_text2))
                .contentColor (getResources ().getColor (R.color.primary_text2))
                .negativeColor (getResources ().getColor (R.color.primary_text2))
                .typeface (SetTypeFace.getTypeface (this), SetTypeFace.getTypeface (this))
                .canceledOnTouchOutside (true)
                .cancelable (true)
                .positiveText (R.string.dialog_action_yes)
                .negativeText (R.string.dialog_action_no)
                .onPositive (new MaterialDialog.SingleButtonCallback () {
                    @Override
                    public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        userDetailsPref.putBooleanPref (MainActivity.this, UserDetailsPref.LOGGED_IN_SESSION, false);
                        finish ();
                        overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                }).build ();
        dialog.show ();
*/
//        super.onBackPressed ();
    
        if (bottomNavigationView.getSelectedItemId () == R.id.action_item_events) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed ();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Utils.showToast (this, "Press again to exit", false);
        
            new Handler ().postDelayed (new Runnable () {
                @Override
                public void run () {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            bottomNavigationView.setSelectedItemId (R.id.action_item_events);
        }
    }
    
    private void initApplication () {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager ().getPackageInfo (getPackageName (), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace ();
        }
        
        if (NetworkConnection.isNetworkAvailable (this)) {
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.URL_INIT, true);
            final PackageInfo finalPInfo = pInfo;
            StringRequest strRequest = new StringRequest (Request.Method.POST, AppConfigURL.URL_INIT,
                    new Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                    boolean error = jsonObj.getBoolean (AppConfigTags.ERROR);
                                    String message = jsonObj.getString (AppConfigTags.MESSAGE);
                                    if (! error) {
                                        if (jsonObj.getBoolean (AppConfigTags.VERSION_UPDATE)) {
                                            if (! userDetailsPref.getBooleanPref (MainActivity.this, UserDetailsPref.LOGGED_IN_SESSION)) {
                                                new MaterialDialog.Builder (MainActivity.this)
                                                        .content (jsonObj.getString (AppConfigTags.UPDATE_MESSAGE))
                                                        .positiveColor (getResources ().getColor (R.color.primary_text2))
                                                        .contentColor (getResources ().getColor (R.color.primary_text2))
                                                        .negativeColor (getResources ().getColor (R.color.primary_text2))
                                                        .typeface (SetTypeFace.getTypeface (MainActivity.this), SetTypeFace.getTypeface (MainActivity.this))
                                                        .canceledOnTouchOutside (false)
                                                        .cancelable (false)
                                                        .positiveText (R.string.dialog_action_update)
                                                        .negativeText (R.string.dialog_action_ignore)
                                                        .onPositive (new MaterialDialog.SingleButtonCallback () {
                                                            @Override
                                                            public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                final String appPackageName = getPackageName ();
                                                                try {
                                                                    startActivity (new Intent (Intent.ACTION_VIEW, Uri.parse ("market://details?id=" + appPackageName)));
                                                                } catch (android.content.ActivityNotFoundException anfe) {
                                                                    startActivity (new Intent (Intent.ACTION_VIEW, Uri.parse ("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                                }
                                                            }
                                                        })
                                                        .onNegative (new MaterialDialog.SingleButtonCallback () {
                                                            @Override
                                                            public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                dialog.dismiss ();
                                                            }
                                                        }).show ();
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace ();
                                }
                            } else {
                                //   initDefaultBanner ();
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                        }
                    },
                    new Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                        }
                    }) {
                
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<> ();
                    params.put ("app_version", String.valueOf (finalPInfo.versionCode));
                    params.put (AppConfigTags.FIREBASE_ID, userDetailsPref.getStringPref (MainActivity.this, UserDetailsPref.USER_FIREBASE_ID));
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }
                
                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<> ();
                    UserDetailsPref userDetailsPref = UserDetailsPref.getInstance ();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put (AppConfigTags.HEADER_USER_LOGIN_KEY, userDetailsPref.getStringPref (MainActivity.this, UserDetailsPref.USER_LOGIN_KEY));
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            strRequest.setRetryPolicy (new DefaultRetryPolicy (DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Utils.sendRequest (strRequest, 30);
        } else {
        }
    }
}