package com.indiasupply.ismedical.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.indiasupply.ismedical.R;
import com.indiasupply.ismedical.adapter.EventExhibitorAdapter;
import com.indiasupply.ismedical.adapter.EventScheduleAdapter;
import com.indiasupply.ismedical.adapter.EventSpeakerAdapter;
import com.indiasupply.ismedical.dialog.EventFloorPlanDialogFragment;
import com.indiasupply.ismedical.helper.DatabaseHandler;
import com.indiasupply.ismedical.model.EventExhibitor;
import com.indiasupply.ismedical.model.EventSchedule;
import com.indiasupply.ismedical.model.EventSpeaker;
import com.indiasupply.ismedical.utils.AppConfigTags;
import com.indiasupply.ismedical.utils.AppConfigURL;
import com.indiasupply.ismedical.utils.Constants;
import com.indiasupply.ismedical.utils.NetworkConnection;
import com.indiasupply.ismedical.utils.RecyclerViewMargin;
import com.indiasupply.ismedical.utils.UserDetailsPref;
import com.indiasupply.ismedical.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class EventDetailActivity extends AppCompatActivity {
    RelativeLayout rlBack;
    CoordinatorLayout clMain;
    
    int event_id;
    
    //    ShimmerFrameLayout shimmerFrameLayout;
    RelativeLayout rlMain;
    
    RelativeLayout rlNoResult;
    ImageView ivNoResult;
    TextView tvNoResultTitle;
    TextView tvNoResultDescription;
    TextView tvNoResultButton;
    
    ProgressBar progressBar2;
    
    
    NestedScrollView nestedScrollView;
    
    
    TextView tvEventName;
    TextView tvEventDates;
    TextView tvEventVenue;
    ImageView ivEventImage;
    ProgressBar progressBar;
    
    
    RelativeLayout rlSchedule;
    RelativeLayout rlE1;
    ImageView ivE1;
    RecyclerView rvSchedule;
    EventScheduleAdapter eventScheduleAdapter;
    
    
    RelativeLayout rlSpeaker;
    RelativeLayout rlE2;
    ImageView ivE2;
    RecyclerView rvSpeaker;
    EventSpeakerAdapter eventSpeakerAdapter;
    
    
    RelativeLayout rlExhibitors;
    RelativeLayout rlE3;
    ImageView ivE3;
    RecyclerView rvExhibitor;
    EventExhibitorAdapter exhibitorAdapter;
    
    
    RelativeLayout rlFloorPlan;
    RelativeLayout rlE4;
    ImageView ivE4;
    SubsamplingScaleImageView ivFloorPlan;
    
    RelativeLayout rlInformation;
    RelativeLayout rlE5;
    ImageView ivE5;
    WebView wvInformation;
    
    RelativeLayout rlRegistration;
    RelativeLayout rlE6;
    ImageView ivE6;
    WebView wvRegistration;
    
    
    DatabaseHandler db;
    
    
    List<EventSchedule> eventScheduleList = new ArrayList<> ();
    List<EventSpeaker> eventSpeakerList = new ArrayList<> ();
    List<EventExhibitor> eventExhibitorList = new ArrayList<> ();
    Bitmap bitmap;
    
    String eventFloorPlan = "";
    
    FirebaseAnalytics mFirebaseAnalytics;
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_event_detail);
        getExtras ();
        initView ();
        initData ();
        initListener ();
        setData ();
    }
    
    private void initListener () {
        rlBack.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                finish ();
                overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
    
            }
        });
    
        rlE1.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                // [START custom_event]
                Bundle params = new Bundle ();
                params.putBoolean ("clicked", true);
                params.putInt ("event_id", event_id);
                params.putString ("item", "schedule");
                mFirebaseAnalytics.logEvent ("event_item", params);
                // [END custom_event]
    
                
                if (rvSchedule.getVisibility () == View.VISIBLE) {
                    rvSchedule.setVisibility (View.GONE);
                    ivE1.animate ().alpha (0f).setDuration (200);
                    new Handler ().postDelayed (new Runnable () {
                        @Override
                        public void run () {
                            ivE1.setImageResource (R.drawable.ic_arrow_down);
                            ivE1.animate ().alpha (1.0f).setDuration (200);
                        }
                    }, 200);
                } else {
                    rvSchedule.setVisibility (View.VISIBLE);
                    ivE1.animate ().alpha (0f).setDuration (200);
                    new Handler ().postDelayed (new Runnable () {
                        @Override
                        public void run () {
                            ivE1.setImageResource (R.drawable.ic_arrow_up);
                            ivE1.animate ().alpha (1.0f).setDuration (200);
                        }
                    }, 200);
                }
    
            }
        });
    
        rlE2.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                // [START custom_event]
                Bundle params = new Bundle ();
                params.putBoolean ("clicked", true);
                params.putInt ("event_id", event_id);
                params.putString ("item", "speaker");
                mFirebaseAnalytics.logEvent ("event_item", params);
                // [END custom_event]

                if (rvSpeaker.getVisibility () == View.VISIBLE) {
                    rvSpeaker.setVisibility (View.GONE);
                    ivE2.animate ().alpha (0f).setDuration (200);
                    new Handler ().postDelayed (new Runnable () {
                        @Override
                        public void run () {
                            ivE2.setImageResource (R.drawable.ic_arrow_down);
                            ivE2.animate ().alpha (1.0f).setDuration (200);
                        }
                    }, 200);
                } else {
                    rvSpeaker.setVisibility (View.VISIBLE);
                    ivE2.animate ().alpha (0f).setDuration (200);
                    new Handler ().postDelayed (new Runnable () {
                        @Override
                        public void run () {
                            ivE2.setImageResource (R.drawable.ic_arrow_up);
                            ivE2.animate ().alpha (1.0f).setDuration (200);
                        }
                    }, 200);
                }
            }
        });
    
        rlE3.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                // [START custom_event]
                Bundle params = new Bundle ();
                params.putBoolean ("clicked", true);
                params.putInt ("event_id", event_id);
                params.putString ("item", "exhibitor");
                mFirebaseAnalytics.logEvent ("event_item", params);
                // [END custom_event]

                if (rvExhibitor.getVisibility () == View.VISIBLE) {
                    rvExhibitor.setVisibility (View.GONE);
                    ivE3.animate ().alpha (0f).setDuration (200);
                    new Handler ().postDelayed (new Runnable () {
                        @Override
                        public void run () {
                            ivE3.setImageResource (R.drawable.ic_arrow_down);
                            ivE3.animate ().alpha (1.0f).setDuration (200);
                        }
                    }, 200);
                } else {
                    rvExhibitor.setVisibility (View.VISIBLE);
                    ivE3.animate ().alpha (0f).setDuration (200);
                    new Handler ().postDelayed (new Runnable () {
                        @Override
                        public void run () {
                            ivE3.setImageResource (R.drawable.ic_arrow_up);
                            ivE3.animate ().alpha (1.0f).setDuration (200);
                        }
                    }, 200);
                }
            }
        });
    
        rlE4.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                // [START custom_event]
                Bundle params = new Bundle ();
                params.putBoolean ("clicked", true);
                params.putInt ("event_id", event_id);
                params.putString ("item", "floor_plan");
                mFirebaseAnalytics.logEvent ("event_item", params);
                // [END custom_event]

                if (ivFloorPlan.getVisibility () == View.VISIBLE) {
                    ivFloorPlan.setVisibility (View.GONE);
                    ivE4.animate ().alpha (0f).setDuration (200);
                    new Handler ().postDelayed (new Runnable () {
                        @Override
                        public void run () {
                            ivE4.setImageResource (R.drawable.ic_arrow_down);
                            ivE4.animate ().alpha (1.0f).setDuration (200);
                        }
                    }, 200);
                } else {
                    ivFloorPlan.setVisibility (View.VISIBLE);
                    ivE4.animate ().alpha (0f).setDuration (200);
                    new Handler ().postDelayed (new Runnable () {
                        @Override
                        public void run () {
                            ivE4.setImageResource (R.drawable.ic_arrow_up);
                            ivE4.animate ().alpha (1.0f).setDuration (200);
                        }
                    }, 200);
                }
            }
        });
        rlE5.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                // [START custom_event]
                Bundle params = new Bundle ();
                params.putBoolean ("clicked", true);
                params.putInt ("event_id", event_id);
                params.putString ("item", "information");
                mFirebaseAnalytics.logEvent ("event_item", params);
                // [END custom_event]
    
                if (wvInformation.getVisibility () == View.VISIBLE) {
                    wvInformation.setVisibility (View.GONE);
                    ivE5.animate ().alpha (0f).setDuration (200);
                    new Handler ().postDelayed (new Runnable () {
                        @Override
                        public void run () {
                            ivE5.setImageResource (R.drawable.ic_arrow_down);
                            ivE5.animate ().alpha (1.0f).setDuration (200);
                        }
                    }, 200);
                } else {
                    wvInformation.setVisibility (View.VISIBLE);
                    ivE5.animate ().alpha (0f).setDuration (200);
                    new Handler ().postDelayed (new Runnable () {
                        @Override
                        public void run () {
                            ivE5.setImageResource (R.drawable.ic_arrow_up);
                            ivE5.animate ().alpha (1.0f).setDuration (200);
                        }
                    }, 200);
                }
            }
        });
        rlE6.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                // [START custom_event]
                Bundle params = new Bundle ();
                params.putBoolean ("clicked", true);
                params.putInt ("event_id", event_id);
                params.putString ("item", "registration");
                mFirebaseAnalytics.logEvent ("event_item", params);
                // [END custom_event]
    
                if (wvRegistration.getVisibility () == View.VISIBLE) {
                    wvRegistration.setVisibility (View.GONE);
                    ivE6.animate ().alpha (0f).setDuration (200);
                    new Handler ().postDelayed (new Runnable () {
                        @Override
                        public void run () {
                            ivE6.setImageResource (R.drawable.ic_arrow_down);
                            ivE6.animate ().alpha (1.0f).setDuration (200);
                        }
                    }, 200);
                } else {
                    wvRegistration.setVisibility (View.VISIBLE);
                    ivE6.animate ().alpha (0f).setDuration (200);
                    new Handler ().postDelayed (new Runnable () {
                        @Override
                        public void run () {
                            ivE6.setImageResource (R.drawable.ic_arrow_up);
                            ivE6.animate ().alpha (1.0f).setDuration (200);
                        }
                    }, 200);
    
                    new Handler ().postDelayed (new Runnable () {
                        @Override
                        public void run () {
                            nestedScrollView.setSmoothScrollingEnabled (true);
                            nestedScrollView.fullScroll (NestedScrollView.FOCUS_DOWN);
                        }
                    }, 400);
    
    
                }
            }
        });
    
        ivFloorPlan.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                FragmentTransaction ft = getFragmentManager ().beginTransaction ();
                EventFloorPlanDialogFragment frag4 = EventFloorPlanDialogFragment.newInstance (event_id, eventFloorPlan);
                frag4.show (ft, "4");
            }
        });
        
    }
    
    private void initData () {
        db = new DatabaseHandler (getApplicationContext ());
        eventClicked (event_id);
        Utils.setTypefaceToAllViews (this, tvNoResultButton);
        Window window = getWindow ();
        if (Build.VERSION.SDK_INT >= 21) {
            window.clearFlags (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags (WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor (ContextCompat.getColor (this, R.color.text_color_white));
        }
    
        mFirebaseAnalytics = FirebaseAnalytics.getInstance (this);
    
        // [START custom_event]
        Bundle params = new Bundle ();
        params.putBoolean ("clicked", true);
        params.putInt ("event_id", event_id);
        mFirebaseAnalytics.logEvent ("event_detail", params);
        // [END custom_event]
    
    
        eventScheduleAdapter = new EventScheduleAdapter (this, eventScheduleList);
        rvSchedule.setAdapter (eventScheduleAdapter);
        rvSchedule.setHasFixedSize (true);
        rvSchedule.setLayoutManager (new LinearLayoutManager (this, LinearLayoutManager.VERTICAL, false));
        rvSchedule.setItemAnimator (new DefaultItemAnimator ());
        rvSchedule.addItemDecoration (new RecyclerViewMargin ((int) Utils.pxFromDp (this, 8), (int) Utils.pxFromDp (this, 8), (int) Utils.pxFromDp (this, 16), (int) Utils.pxFromDp (this, 16), 1, 0, RecyclerViewMargin.LAYOUT_MANAGER_LINEAR, RecyclerViewMargin.ORIENTATION_VERTICAL));
        rvSchedule.setNestedScrollingEnabled (false);
    
        eventSpeakerAdapter = new EventSpeakerAdapter (this, eventSpeakerList);
        rvSpeaker.setAdapter (eventSpeakerAdapter);
        rvSpeaker.setHasFixedSize (true);
        rvSpeaker.setLayoutManager (new LinearLayoutManager (this, LinearLayoutManager.VERTICAL, false));
        rvSpeaker.setItemAnimator (new DefaultItemAnimator ());
        rvSpeaker.addItemDecoration (new RecyclerViewMargin ((int) Utils.pxFromDp (this, 8), (int) Utils.pxFromDp (this, 8), (int) Utils.pxFromDp (this, 16), (int) Utils.pxFromDp (this, 16), 1, 0, RecyclerViewMargin.LAYOUT_MANAGER_LINEAR, RecyclerViewMargin.ORIENTATION_VERTICAL));
        rvSpeaker.setNestedScrollingEnabled (false);
    
        exhibitorAdapter = new EventExhibitorAdapter (this, eventExhibitorList);
        rvExhibitor.setAdapter (exhibitorAdapter);
        rvExhibitor.setHasFixedSize (true);
        rvExhibitor.setLayoutManager (new LinearLayoutManager (this, LinearLayoutManager.VERTICAL, false));
        rvExhibitor.setItemAnimator (new DefaultItemAnimator ());
        rvExhibitor.addItemDecoration (new RecyclerViewMargin ((int) Utils.pxFromDp (this, 8), (int) Utils.pxFromDp (this, 8), (int) Utils.pxFromDp (this, 16), (int) Utils.pxFromDp (this, 16), 1, 0, RecyclerViewMargin.LAYOUT_MANAGER_LINEAR, RecyclerViewMargin.ORIENTATION_VERTICAL));
        rvExhibitor.setNestedScrollingEnabled (false);
    }
    
    private void initView () {
        rlBack = (RelativeLayout) findViewById (R.id.rlBack);
        clMain = (CoordinatorLayout) findViewById (R.id.clMain);
//        shimmerFrameLayout = (ShimmerFrameLayout) findViewById (R.id.shimmer_view_container);
        rlMain = (RelativeLayout) findViewById (R.id.rlMain);
        rlNoResult = (RelativeLayout) findViewById (R.id.rlNoResult);
        ivNoResult = (ImageView) findViewById (R.id.ivNoResult);
        tvNoResultTitle = (TextView) findViewById (R.id.tvNoResultTitle);
        tvNoResultDescription = (TextView) findViewById (R.id.tvNoResultDescription);
        tvNoResultButton = (TextView) findViewById (R.id.tvNoResultButton);
        nestedScrollView = (NestedScrollView) findViewById (R.id.nestedScrollView);
    
        progressBar2 = (ProgressBar) findViewById (R.id.progressBar2);
        
        
        tvEventDates = (TextView) findViewById (R.id.tvEventDates);
        tvEventName = (TextView) findViewById (R.id.tvEventName);
        tvEventVenue = (TextView) findViewById (R.id.tvEventVenue);
        ivEventImage = (ImageView) findViewById (R.id.ivEventImage);
        progressBar = (ProgressBar) findViewById (R.id.progressBar);
    
    
        rlSchedule = (RelativeLayout) findViewById (R.id.rlSchedule);
        rlE1 = (RelativeLayout) findViewById (R.id.rlE1);
        ivE1 = (ImageView) findViewById (R.id.ivE1);
        rvSchedule = (RecyclerView) findViewById (R.id.rvSchedule);
    
        rlSpeaker = (RelativeLayout) findViewById (R.id.rlSpeakers);
        rlE2 = (RelativeLayout) findViewById (R.id.rlE2);
        ivE2 = (ImageView) findViewById (R.id.ivE2);
        rvSpeaker = (RecyclerView) findViewById (R.id.rvSpeakers);
    
        rlExhibitors = (RelativeLayout) findViewById (R.id.rlExhibitors);
        rlE3 = (RelativeLayout) findViewById (R.id.rlE3);
        ivE3 = (ImageView) findViewById (R.id.ivE3);
        rvExhibitor = (RecyclerView) findViewById (R.id.rvExhibitors);
    
        rlFloorPlan = (RelativeLayout) findViewById (R.id.rlFloorPlan);
        rlE4 = (RelativeLayout) findViewById (R.id.rlE4);
        ivE4 = (ImageView) findViewById (R.id.ivE4);
        ivFloorPlan = (SubsamplingScaleImageView) findViewById (R.id.ivFloorPlan);
    
        rlInformation = (RelativeLayout) findViewById (R.id.rlInformation);
        rlE5 = (RelativeLayout) findViewById (R.id.rlE5);
        ivE5 = (ImageView) findViewById (R.id.ivE5);
        wvInformation = (WebView) findViewById (R.id.wvInformation);
    
        rlRegistration = (RelativeLayout) findViewById (R.id.rlRegistration);
        rlE6 = (RelativeLayout) findViewById (R.id.rlE6);
        ivE6 = (ImageView) findViewById (R.id.ivE6);
        wvRegistration = (WebView) findViewById (R.id.wvRegistration);
    }
    
    private void getExtras () {
        Intent intent = getIntent ();
        event_id = intent.getIntExtra (AppConfigTags.EVENT_ID, 0);
    }
    
    @Override
    public void onBackPressed () {
        finish ();
        overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
    }
    
    private void setData () {
        progressBar2.setVisibility (View.VISIBLE);
//        shimmerFrameLayout.setVisibility (View.VISIBLE);
        rlMain.setVisibility (View.GONE);
        rlNoResult.setVisibility (View.GONE);
        if (NetworkConnection.isNetworkAvailable (this)) {
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.URL_EVENT_DETAILS + "/" + event_id, true);
            StringRequest strRequest = new StringRequest (Request.Method.GET, AppConfigURL.URL_EVENT_DETAILS + "/" + event_id,
                    new Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                    boolean is_error = jsonObj.getBoolean (AppConfigTags.ERROR);
                                    String message = jsonObj.getString (AppConfigTags.MESSAGE);
                                    if (! is_error) {
                                        if (db.isEventExist (event_id)) {
                                            db.updateEventDetails (event_id, response);
                                        } else {
                                            db.insertEvent (event_id, response);
                                        }
    
                                        tvEventName.setText (jsonObj.getString (AppConfigTags.EVENT_NAME));
                                        if (jsonObj.getString (AppConfigTags.EVENT_START_DATE).equalsIgnoreCase (jsonObj.getString (AppConfigTags.EVENT_END_DATE))) {
                                            tvEventDates.setText (Utils.convertTimeFormat (jsonObj.getString (AppConfigTags.EVENT_END_DATE), "yyyy-MM-dd", "dd MMM"));
                                        } else {
                                            tvEventDates.setText (Utils.convertTimeFormat (jsonObj.getString (AppConfigTags.EVENT_START_DATE), "yyyy-MM-dd", "dd MMM") + " - " + Utils.convertTimeFormat (jsonObj.getString (AppConfigTags.EVENT_END_DATE), "yyyy-MM-dd", "dd MMM"));
                                        }
    
                                        if (jsonObj.getString (AppConfigTags.EVENT_VENUE_FULL).length () > 0) {
                                            tvEventVenue.setText (jsonObj.getString (AppConfigTags.EVENT_VENUE_FULL) + ", " + jsonObj.getString (AppConfigTags.EVENT_CITY));
                                        } else {
                                            tvEventVenue.setText (jsonObj.getString (AppConfigTags.EVENT_CITY));
                                        }
    
                                        if (jsonObj.getString (AppConfigTags.EVENT_IMAGE).length () == 0) {
                                            ivEventImage.setImageResource (R.drawable.default_event2);
                                            progressBar.setVisibility (View.GONE);
                                        } else {
                                            progressBar.setVisibility (View.VISIBLE);
                                            Glide.with (EventDetailActivity.this)
                                                    .load (jsonObj.getString (AppConfigTags.EVENT_IMAGE))
                                                    .listener (new RequestListener<String, GlideDrawable> () {
                                                        @Override
                                                        public boolean onException (Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                                            progressBar.setVisibility (View.GONE);
                                                            return false;
                                                        }
    
                                                        @Override
                                                        public boolean onResourceReady (GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                                            progressBar.setVisibility (View.GONE);
                                                            return false;
                                                        }
                                                    })
                                                    .error (R.drawable.default_event2)
                                                    .into (ivEventImage);
                                        }
    
    
                                        if (jsonObj.getJSONObject (AppConfigTags.EVENT_SCHEDULE).getJSONArray ("schedules").length () > 0) {
                                            JSONArray jsonArraySchedules = jsonObj.getJSONObject (AppConfigTags.EVENT_SCHEDULE).getJSONArray ("schedules");
                                            for (int j = 0; j < jsonArraySchedules.length (); j++) {
                                                JSONObject jsonObjectSchedules = jsonArraySchedules.getJSONObject (j);
                                                eventScheduleList.add (new EventSchedule (
                                                        jsonObjectSchedules.getInt ("schedule_id"),
                                                        R.drawable.ic_date,
                                                        jsonObjectSchedules.getInt ("schedule_date_id"),
                                                        jsonObjectSchedules.getString ("schedule_date"),
                                                        jsonObjectSchedules.getString ("schedule_start_time"),
                                                        jsonObjectSchedules.getString ("schedule_end_time"),
                                                        jsonObjectSchedules.getString ("schedule_description"),
                                                        jsonObjectSchedules.getString ("schedule_location"),
                                                        jsonObjectSchedules.getString ("schedule_image")
                                                ));
                                            }
                                            eventScheduleAdapter.notifyDataSetChanged ();
                                            rlSchedule.setVisibility (View.VISIBLE);
                                        } else {
                                            rlSchedule.setVisibility (View.GONE);
                                        }
    
                                        if (jsonObj.getJSONArray (AppConfigTags.EVENT_SPEAKERS).length () > 0) {
                                            JSONArray jsonArray = jsonObj.getJSONArray (AppConfigTags.EVENT_SPEAKERS);
                                            for (int j = 0; j < jsonArray.length (); j++) {
                                                JSONObject jsonObjectSpeaker = jsonArray.getJSONObject (j);
                                                eventSpeakerList.add (j, new EventSpeaker (jsonObjectSpeaker.getInt (AppConfigTags.SPEAKER_ID),
                                                        R.drawable.default_speaker,
                                                        jsonObjectSpeaker.getString (AppConfigTags.SPEAKERS_NAME),
                                                        jsonObjectSpeaker.getString (AppConfigTags.SPEAKERS_DESCRIPTION),
                                                        jsonObjectSpeaker.getString (AppConfigTags.SPEAKERS_IMAGE)));
                                            }
                                            eventSpeakerAdapter.notifyDataSetChanged ();
                                            rlSpeaker.setVisibility (View.VISIBLE);
                                        } else {
                                            rlSpeaker.setVisibility (View.GONE);
                                        }
    
                                        if (jsonObj.getJSONArray (AppConfigTags.EVENT_EXHIBITORS).length () > 0) {
                                            JSONArray jsonArray = jsonObj.getJSONArray (AppConfigTags.EVENT_EXHIBITORS);
                                            for (int j = 0; j < jsonArray.length (); j++) {
                                                JSONObject jsonObjectExhibitor = jsonArray.getJSONObject (j);
                                                eventExhibitorList.add (j, new EventExhibitor (jsonObjectExhibitor.getInt (AppConfigTags.EXHIBITOR_ID),
                                                        R.drawable.ic_event_exhibitor,
                                                        jsonObjectExhibitor.getString (AppConfigTags.EXHIBITOR_NAME),
                                                        jsonObjectExhibitor.getString (AppConfigTags.EXHIBITOR_DESCRIPTION),
                                                        jsonObjectExhibitor.getString (AppConfigTags.EXHIBITOR_IMAGE)));
                                            }
                                            exhibitorAdapter.notifyDataSetChanged ();
                                            rlExhibitors.setVisibility (View.VISIBLE);
                                        } else {
                                            rlExhibitors.setVisibility (View.GONE);
                                        }
    
                                        boolean flag = false;
                                        for (String ext : new String[] {".png", ".jpg", ".jpeg"}) {
                                            if (jsonObj.getString (AppConfigTags.EVENT_FLOOR_PLAN).endsWith (ext)) {
                                                new getBitmapFromURL ().execute (jsonObj.getString (AppConfigTags.EVENT_FLOOR_PLAN));
                                                flag = true;
                                                break;
                                            }
                                        }
                                        if (flag) {
                                            eventFloorPlan = jsonObj.getString (AppConfigTags.EVENT_FLOOR_PLAN);
    
                                            if (db.getEventFloorPlan (event_id).length () > 0) {
                                                ivFloorPlan.setImage (ImageSource.bitmap (Utils.base64ToBitmap (db.getEventFloorPlan (event_id))));
                                            } else {
                                                new getBitmapFromURL ().execute (jsonObj.getString (AppConfigTags.EVENT_FLOOR_PLAN));
                                            }
                                            rlFloorPlan.setVisibility (View.VISIBLE);
                                        } else {
                                            rlFloorPlan.setVisibility (View.GONE);
                                        }
    
                                        if (jsonObj.getString (AppConfigTags.EVENT_INFORMATION).length () > 0) {
                                            getWebView (wvInformation, jsonObj.getString (AppConfigTags.EVENT_INFORMATION));
                                            rlInformation.setVisibility (View.VISIBLE);
                                        } else {
                                            rlInformation.setVisibility (View.GONE);
                                        }
    
                                        if (jsonObj.getString (AppConfigTags.EVENT_REGISTRATION).length () > 0) {
                                            getWebView (wvRegistration, jsonObj.getString (AppConfigTags.EVENT_REGISTRATION));
                                            rlRegistration.setVisibility (View.VISIBLE);
                                        } else {
                                            rlRegistration.setVisibility (View.GONE);
                                        }
    
                                        progressBar2.setVisibility (View.GONE);
//                                        shimmerFrameLayout.setVisibility (View.GONE);
                                        rlMain.setVisibility (View.VISIBLE);
                                    } else {
                                        if (! showOfflineData (event_id)) {
                                            Utils.showSnackBar (EventDetailActivity.this, clMain, message, Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace ();
                                    Utils.showLog (Log.WARN, AppConfigTags.EXCEPTION, e.getMessage (), true);
                                    if (! showOfflineData (event_id)) {
                                        Utils.showSnackBar (EventDetailActivity.this, clMain, getResources ().getString (R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                    }
                                }
                            } else {
                                if (! showOfflineData (event_id)) {
                                    Utils.showSnackBar (EventDetailActivity.this, clMain, getResources ().getString (R.string.snackbar_text_unstable_internet), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                }
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                        }
                    },
                    new Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {
                                Utils.showLog (Log.ERROR, AppConfigTags.ERROR, new String (response.data), true);
                            }
                            if (! showOfflineData (event_id)) {
                                Utils.showSnackBar (EventDetailActivity.this, clMain, getResources ().getString (R.string.snackbar_text_unstable_internet), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                            }
                        }
                    }) {
    
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String> ();
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }
    
                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<> ();
                    UserDetailsPref userDetailsPref = UserDetailsPref.getInstance ();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put (AppConfigTags.HEADER_USER_LOGIN_KEY, userDetailsPref.getStringPref (EventDetailActivity.this, UserDetailsPref.USER_LOGIN_KEY));
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest (strRequest, 20);
        } else {
            if (! showOfflineData (event_id)) {
                Utils.showSnackBar (this, clMain, getResources ().getString (R.string.snackbar_text_no_internet_connection_available), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_go_to_settings), new View.OnClickListener () {
                    @Override
                    public void onClick (View v) {
                        Intent dialogIntent = new Intent (Settings.ACTION_SETTINGS);
                        dialogIntent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity (dialogIntent);
                    }
                });
            }
        }
    }
    
    @Override
    public void onStart () {
        super.onStart ();
//        Utils.startShimmer (shimmerFrameLayout);
    }
    
    @Override
    public void onResume () {
        super.onResume ();
//        shimmerFrameLayout.startShimmerAnimation ();
    }
    
    @Override
    public void onPause () {
//        shimmerFrameLayout.stopShimmerAnimation ();
        super.onPause ();
    }
    
    private boolean showOfflineData (int event_id) {
        if (db.isEventExist (event_id)) {
    
            String response = db.getEventDetails (event_id);
            try {
                JSONObject jsonObj = new JSONObject (response);
                boolean is_error = jsonObj.getBoolean (AppConfigTags.ERROR);
                String message = jsonObj.getString (AppConfigTags.MESSAGE);
                if (! is_error) {
                    if (db.isEventExist (event_id)) {
                        db.updateEventDetails (event_id, response);
                    } else {
                        db.insertEvent (event_id, response);
                    }
                    tvEventName.setText (jsonObj.getString (AppConfigTags.EVENT_NAME));
                    if (jsonObj.getString (AppConfigTags.EVENT_START_DATE).equalsIgnoreCase (jsonObj.getString (AppConfigTags.EVENT_END_DATE))) {
                        tvEventDates.setText (Utils.convertTimeFormat (jsonObj.getString (AppConfigTags.EVENT_END_DATE), "yyyy-MM-dd", "dd MMM"));
                    } else {
                        tvEventDates.setText (Utils.convertTimeFormat (jsonObj.getString (AppConfigTags.EVENT_START_DATE), "yyyy-MM-dd", "dd MMM") + " - " + Utils.convertTimeFormat (jsonObj.getString (AppConfigTags.EVENT_END_DATE), "yyyy-MM-dd", "dd MMM"));
                    }
    
                    tvEventVenue.setText (jsonObj.getString (AppConfigTags.EVENT_VENUE_FULL) + ", " + jsonObj.getString (AppConfigTags.EVENT_CITY));
    
                    if (jsonObj.getString (AppConfigTags.EVENT_IMAGE).length () == 0) {
                        ivEventImage.setImageResource (R.drawable.default_event2);
                        progressBar.setVisibility (View.GONE);
                    } else {
                        progressBar.setVisibility (View.VISIBLE);
                        Glide.with (EventDetailActivity.this)
                                .load (jsonObj.getString (AppConfigTags.EVENT_IMAGE))
                                .listener (new RequestListener<String, GlideDrawable> () {
                                    @Override
                                    public boolean onException (Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        progressBar.setVisibility (View.GONE);
                                        return false;
                                    }
                    
                                    @Override
                                    public boolean onResourceReady (GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        progressBar.setVisibility (View.GONE);
                                        return false;
                                    }
                                })
                                .error (R.drawable.default_event2)
                                .into (ivEventImage);
                    }
    
    
                    if (jsonObj.getJSONObject (AppConfigTags.EVENT_SCHEDULE).getJSONArray ("schedules").length () > 0) {
                        JSONArray jsonArraySchedules = jsonObj.getJSONObject (AppConfigTags.EVENT_SCHEDULE).getJSONArray ("schedules");
                        for (int j = 0; j < jsonArraySchedules.length (); j++) {
                            JSONObject jsonObjectSchedules = jsonArraySchedules.getJSONObject (j);
                            eventScheduleList.add (new EventSchedule (
                                    jsonObjectSchedules.getInt ("schedule_id"),
                                    R.drawable.ic_date,
                                    jsonObjectSchedules.getInt ("schedule_date_id"),
                                    jsonObjectSchedules.getString ("schedule_date"),
                                    jsonObjectSchedules.getString ("schedule_start_time"),
                                    jsonObjectSchedules.getString ("schedule_end_time"),
                                    jsonObjectSchedules.getString ("schedule_description"),
                                    jsonObjectSchedules.getString ("schedule_location"),
                                    jsonObjectSchedules.getString ("schedule_image")
                            ));
                        }
                        eventScheduleAdapter.notifyDataSetChanged ();
                        rlSchedule.setVisibility (View.VISIBLE);
                    } else {
                        rlSchedule.setVisibility (View.GONE);
                    }
    
                    if (jsonObj.getJSONArray (AppConfigTags.EVENT_SPEAKERS).length () > 0) {
                        JSONArray jsonArray = jsonObj.getJSONArray (AppConfigTags.EVENT_SPEAKERS);
                        for (int j = 0; j < jsonArray.length (); j++) {
                            JSONObject jsonObjectSpeaker = jsonArray.getJSONObject (j);
                            eventSpeakerList.add (j, new EventSpeaker (jsonObjectSpeaker.getInt (AppConfigTags.SPEAKER_ID),
                                    R.drawable.default_speaker,
                                    jsonObjectSpeaker.getString (AppConfigTags.SPEAKERS_NAME),
                                    jsonObjectSpeaker.getString (AppConfigTags.SPEAKERS_DESCRIPTION),
                                    jsonObjectSpeaker.getString (AppConfigTags.SPEAKERS_IMAGE)));
                        }
                        eventSpeakerAdapter.notifyDataSetChanged ();
                        rlSpeaker.setVisibility (View.VISIBLE);
                    } else {
                        rlSpeaker.setVisibility (View.GONE);
                    }
    
                    if (jsonObj.getJSONArray (AppConfigTags.EVENT_EXHIBITORS).length () > 0) {
                        JSONArray jsonArray = jsonObj.getJSONArray (AppConfigTags.EVENT_EXHIBITORS);
                        for (int j = 0; j < jsonArray.length (); j++) {
                            JSONObject jsonObjectExhibitor = jsonArray.getJSONObject (j);
                            eventExhibitorList.add (j, new EventExhibitor (jsonObjectExhibitor.getInt (AppConfigTags.EXHIBITOR_ID),
                                    R.drawable.ic_event_exhibitor,
                                    jsonObjectExhibitor.getString (AppConfigTags.EXHIBITOR_NAME),
                                    jsonObjectExhibitor.getString (AppConfigTags.EXHIBITOR_DESCRIPTION),
                                    jsonObjectExhibitor.getString (AppConfigTags.EXHIBITOR_IMAGE)));
                        }
                        exhibitorAdapter.notifyDataSetChanged ();
                        rlExhibitors.setVisibility (View.VISIBLE);
                    } else {
                        rlExhibitors.setVisibility (View.GONE);
                    }
    
                    boolean flag = false;
                    for (String ext : new String[] {".png", ".jpg", ".jpeg"}) {
                        if (jsonObj.getString (AppConfigTags.EVENT_FLOOR_PLAN).endsWith (ext)) {
                            new getBitmapFromURL ().execute (jsonObj.getString (AppConfigTags.EVENT_FLOOR_PLAN));
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        eventFloorPlan = jsonObj.getString (AppConfigTags.EVENT_FLOOR_PLAN);
    
                        if (db.getEventFloorPlan (event_id).length () > 0) {
                            ivFloorPlan.setImage (ImageSource.bitmap (Utils.base64ToBitmap (db.getEventFloorPlan (event_id))));
                        } else {
                            new getBitmapFromURL ().execute (jsonObj.getString (AppConfigTags.EVENT_FLOOR_PLAN));
                        }
                        rlFloorPlan.setVisibility (View.VISIBLE);
                    } else {
                        rlFloorPlan.setVisibility (View.GONE);
                    }
    
                    if (jsonObj.getString (AppConfigTags.EVENT_INFORMATION).length () > 0) {
                        getWebView (wvInformation, jsonObj.getString (AppConfigTags.EVENT_INFORMATION));
                        rlInformation.setVisibility (View.VISIBLE);
                    } else {
                        rlInformation.setVisibility (View.GONE);
                    }
    
                    if (jsonObj.getString (AppConfigTags.EVENT_REGISTRATION).length () > 0) {
                        getWebView (wvRegistration, jsonObj.getString (AppConfigTags.EVENT_REGISTRATION));
                        rlRegistration.setVisibility (View.VISIBLE);
                    } else {
                        rlRegistration.setVisibility (View.GONE);
                    }
                    progressBar2.setVisibility (View.GONE);
//                    shimmerFrameLayout.setVisibility (View.GONE);
                    rlMain.setVisibility (View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace ();
            }
    
            return true;
        } else {
            return false;
        }
    }
    
    private void eventClicked (int event_id) {
        if (NetworkConnection.isNetworkAvailable (this)) {
            Utils.showLog (Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_EVENT_CLICKED + "/" + event_id, true);
            StringRequest strRequest = new StringRequest (Request.Method.GET, AppConfigURL.URL_EVENT_CLICKED + "/" + event_id,
                    new Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                    boolean is_error = jsonObj.getBoolean (AppConfigTags.ERROR);
                                    String message = jsonObj.getString (AppConfigTags.MESSAGE);
                                    if (! is_error) {
                                    } else {
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                        }
                    },
                    new Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {
                                Utils.showLog (Log.ERROR, AppConfigTags.ERROR, new String (response.data), true);
                            }
                        }
                    }) {
                
                
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String> ();
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }
                
                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<> ();
                    UserDetailsPref userDetailsPref = UserDetailsPref.getInstance ();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put (AppConfigTags.HEADER_USER_LOGIN_KEY, userDetailsPref.getStringPref (EventDetailActivity.this, UserDetailsPref.USER_LOGIN_KEY));
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest (strRequest, 20);
        } else {
        }
    }
    
    private void getWebView (WebView webView, String html) {
        webView.setWebViewClient (new CustomWebViewClient ());
        WebSettings webSetting = webView.getSettings ();
        webSetting.setJavaScriptEnabled (true);
        webSetting.setDisplayZoomControls (true);
        //htmlWebView.loadUrl ("https://www.indiasupply.com/");
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder (
                "<style>@font-face{font-family: myFont; src: url(file:///android_asset/" + Constants.font_name + ");}</style>" + html);
        webView.loadDataWithBaseURL ("", spannableStringBuilder.toString (), "text/html", "UTF-8", "");
    }
    
    private class getBitmapFromURL extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground (String... params) {
            try {
                URL url = new URL (params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection ();
                connection.setDoInput (true);
                connection.connect ();
                InputStream input = connection.getInputStream ();
                bitmap = BitmapFactory.decodeStream (input);
                
            } catch (IOException e) {
                e.printStackTrace ();
            }
            return null;
        }
        
        @Override
        protected void onPostExecute (String result) {
            ivFloorPlan.setImage (ImageSource.bitmap (bitmap));
        }
        
        @Override
        protected void onPreExecute () {
        }
        
        @Override
        protected void onProgressUpdate (Void... values) {
        }
    }
    
    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading (WebView view, String url) {
            if (url != null && (url.startsWith ("http://") || url.startsWith ("https://"))) {
                view.getContext ().startActivity (new Intent (Intent.ACTION_VIEW, Uri.parse (url)));
                return true;
            } else {
                view.loadUrl (url);
                return false;
            }
        }
    }
}