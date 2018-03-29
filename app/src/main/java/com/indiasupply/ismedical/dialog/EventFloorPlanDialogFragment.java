package com.indiasupply.ismedical.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.indiasupply.ismedical.R;
import com.indiasupply.ismedical.helper.DatabaseHandler;
import com.indiasupply.ismedical.utils.AppConfigTags;
import com.indiasupply.ismedical.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class EventFloorPlanDialogFragment extends DialogFragment {
    ImageView ivCancel;
    TextView tvTitle;
    SubsamplingScaleImageView ivFloorPlan;
    
    String eventFloorPlan;
    int event_id;
    ProgressBar progressBar;
    Bitmap bitmap;
    
    DatabaseHandler db;
    
    public static EventFloorPlanDialogFragment newInstance (int event_id, String eventFloorPlan) {
        EventFloorPlanDialogFragment fragment = new EventFloorPlanDialogFragment ();
        Bundle args = new Bundle ();
        args.putInt (AppConfigTags.EVENT_ID, event_id);
        args.putString (AppConfigTags.EVENT_FLOOR_PLAN, eventFloorPlan);
        fragment.setArguments (args);
        return fragment;
    }
    
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setStyle (DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }
    
    @Override
    public void onActivityCreated (Bundle arg0) {
        super.onActivityCreated (arg0);
        Window window = getDialog ().getWindow ();
        window.getAttributes ().windowAnimations = R.style.DialogAnimation;
        if (Build.VERSION.SDK_INT >= 21) {
            window.clearFlags (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags (WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor (ContextCompat.getColor (getActivity (), R.color.text_color_white));
        }
    }
    
    @Override
    public void onResume () {
        super.onResume ();
        getDialog ().setOnKeyListener (new DialogInterface.OnKeyListener () {
            @Override
            public boolean onKey (DialogInterface dialog, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                    //This is the filter
                    if (event.getAction () != KeyEvent.ACTION_UP)
                        return true;
                    else {
                        getDialog ().dismiss ();
                        //Hide your keyboard here!!!!!!
                        return true; // pretend we've processed it
                    }
                } else
                    return false; // pass on to be processed as normal
            }
        });
    }
    
    @Override
    public void onStart () {
        super.onStart ();
        Dialog d = getDialog ();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow ().setLayout (width, height);
        }
    }
    
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate (R.layout.fragment_dialog_event_floor_plan, container, false);
        initBundle ();
        initView (root);
        initData ();
        initListener ();
        return root;
    }
    
    private void initBundle () {
        Bundle bundle = this.getArguments ();
        eventFloorPlan = bundle.getString (AppConfigTags.EVENT_FLOOR_PLAN);
        event_id = bundle.getInt (AppConfigTags.EVENT_ID);
    }
    
    private void initView (View root) {
        tvTitle = (TextView) root.findViewById (R.id.tvTitle);
        ivCancel = (ImageView) root.findViewById (R.id.ivCancel);
        ivFloorPlan = (SubsamplingScaleImageView) root.findViewById (R.id.ivFloorPlan);
        progressBar = (ProgressBar) root.findViewById (R.id.progressBar);
    }
    
    private void initData () {
        Utils.setTypefaceToAllViews (getActivity (), tvTitle);
        db = new DatabaseHandler (getActivity ());
        if (db.getEventFloorPlan (event_id).length () > 0) {
            ivFloorPlan.setImage (ImageSource.bitmap (Utils.base64ToBitmap (db.getEventFloorPlan (event_id))));
            ivFloorPlan.setVisibility (View.VISIBLE);
            progressBar.setVisibility (View.GONE);
        } else {
            new getBitmapFromURL ().execute (eventFloorPlan);
        }
    }
    
    private void initListener () {
        ivCancel.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                getDialog ().dismiss ();
            }
        });
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
            ivFloorPlan.setVisibility (View.VISIBLE);
            progressBar.setVisibility (View.GONE);
        }
        
        @Override
        protected void onPreExecute () {
            progressBar.setVisibility (View.VISIBLE);
        }
        
        @Override
        protected void onProgressUpdate (Void... values) {
        }
    }
}