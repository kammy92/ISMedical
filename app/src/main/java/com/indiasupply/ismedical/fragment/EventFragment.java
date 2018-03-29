package com.indiasupply.ismedical.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.indiasupply.ismedical.R;
import com.indiasupply.ismedical.activity.EventDetailActivity;
import com.indiasupply.ismedical.adapter.EventAdapter;
import com.indiasupply.ismedical.model.Event;
import com.indiasupply.ismedical.utils.AppConfigTags;
import com.indiasupply.ismedical.utils.AppConfigURL;
import com.indiasupply.ismedical.utils.AppDataPref;
import com.indiasupply.ismedical.utils.Constants;
import com.indiasupply.ismedical.utils.NetworkConnection;
import com.indiasupply.ismedical.utils.RecyclerViewMargin;
import com.indiasupply.ismedical.utils.SetTypeFace;
import com.indiasupply.ismedical.utils.ShimmerFrameLayout;
import com.indiasupply.ismedical.utils.UserDetailsPref;
import com.indiasupply.ismedical.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


public class EventFragment extends Fragment {
    RecyclerView rvEvents;
    List<Event> eventList = new ArrayList<> ();
    List<Event> eventTempList = new ArrayList<> ();
    EventAdapter eventAdapter;
    
    RelativeLayout rlFilter;
    TextView tvEventFilter;
    
    CoordinatorLayout clMain;
    ShimmerFrameLayout shimmerFrameLayout;
    AppDataPref appDataPref;
    
    boolean refresh;
    
    ArrayList<String> citiesList = new ArrayList<String> ();
    ArrayList<String> citiesSelectedList = new ArrayList<String> ();
    ArrayList<String> citiesSelectedTempList = new ArrayList<String> ();
    
    private FirebaseAnalytics mFirebaseAnalytics;
    
    public static EventFragment newInstance (boolean refresh) {
        EventFragment fragment = new EventFragment ();
        Bundle args = new Bundle ();
        args.putBoolean (AppConfigTags.REFRESH_FLAG, refresh);
        fragment.setArguments (args);
        return fragment;
    }
    
    public static EventFragment newInstance2 (boolean refresh, int event_id) {
        EventFragment fragment = new EventFragment ();
        Bundle args = new Bundle ();
        args.putBoolean (AppConfigTags.REFRESH_FLAG, refresh);
        args.putInt (AppConfigTags.EVENT_ID, event_id);
        fragment.setArguments (args);
        return fragment;
    }
    
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
    }
    
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate (R.layout.fragment_event, container, false);
        initBundle ();
        initView (rootView);
        initData ();
        initListener ();
        return rootView;
    }
    
    private void initView (View rootView) {
        rvEvents = (RecyclerView) rootView.findViewById (R.id.rvEvents);
        clMain = (CoordinatorLayout) rootView.findViewById (R.id.clMain);
        rlFilter = (RelativeLayout) rootView.findViewById (R.id.rlFilter);
        tvEventFilter = (TextView) rootView.findViewById (R.id.tvEventFilter);
        shimmerFrameLayout = (ShimmerFrameLayout) rootView.findViewById (R.id.shimmer_view_container);
    }
    
    private void initBundle () {
        Bundle bundle = this.getArguments ();
        try {
            refresh = bundle.getBoolean (AppConfigTags.REFRESH_FLAG);
            if (bundle.getInt (AppConfigTags.EVENT_ID, 0) > 0) {
                Intent intent = new Intent (getActivity (), EventDetailActivity.class);
                intent.putExtra (AppConfigTags.EVENT_ID, bundle.getInt (AppConfigTags.EVENT_ID, 0));
                getActivity ().startActivity (intent);
                getActivity ().overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
    
    private void initData () {
        Utils.setTypefaceToAllViews (getActivity (), rvEvents);
        appDataPref = AppDataPref.getInstance ();
    
        mFirebaseAnalytics = FirebaseAnalytics.getInstance (getActivity ());
    
        eventAdapter = new EventAdapter (getActivity (), eventList);
        rvEvents.setAdapter (eventAdapter);
        rvEvents.setNestedScrollingEnabled (false);
        rvEvents.setFocusable (false);
        rvEvents.setHasFixedSize (true);
        rvEvents.setLayoutManager (new LinearLayoutManager (getActivity (), LinearLayoutManager.VERTICAL, false));
        rvEvents.addItemDecoration (new RecyclerViewMargin ((int) Utils.pxFromDp (getActivity (), 16), (int) Utils.pxFromDp (getActivity (), 16), (int) Utils.pxFromDp (getActivity (), 16), (int) Utils.pxFromDp (getActivity (), 16), 1, 0, RecyclerViewMargin.LAYOUT_MANAGER_LINEAR, RecyclerViewMargin.ORIENTATION_VERTICAL));
    
        if (! refresh) {
            showOfflineData ();
        }
    }
    
    private void initListener () {
        rlFilter.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                // [START custom_event]
                Bundle params = new Bundle ();
                params.putBoolean ("clicked", true);
                mFirebaseAnalytics.logEvent ("event_filter", params);
                // [END custom_event]
    
                showFilterDialog ();
            }
        });
    }
    
    private void setData () {
        if (NetworkConnection.isNetworkAvailable (getActivity ())) {
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.URL_HOME_EVENT, true);
            StringRequest strRequest = new StringRequest (Request.Method.GET, AppConfigURL.URL_HOME_EVENT,
                    new Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (getActivity () != null && isAdded ()) {
                                if (response != null) {
                                    try {
                                        JSONObject jsonObj = new JSONObject (response);
                                        boolean is_error = jsonObj.getBoolean (AppConfigTags.ERROR);
                                        String message = jsonObj.getString (AppConfigTags.MESSAGE);
                                        if (! is_error) {
                                            citiesList.clear ();
                                            eventList.clear ();
                                            appDataPref.putStringPref (getActivity (), AppDataPref.HOME_EVENTS, response);
                                            JSONArray jsonArrayEvents = jsonObj.getJSONArray (AppConfigTags.EVENTS);
                                            for (int i = 0; i < jsonArrayEvents.length (); i++) {
                                                JSONObject jsonObjectEvents = jsonArrayEvents.getJSONObject (i);
                                                eventList.add (new Event (
                                                        jsonObjectEvents.getBoolean (AppConfigTags.EVENT_INTERESTED),
                                                        jsonObjectEvents.getInt (AppConfigTags.EVENT_ID),
                                                        R.drawable.default_event,
                                                        jsonObjectEvents.getString (AppConfigTags.EVENT_TYPE),
                                                        jsonObjectEvents.getString (AppConfigTags.EVENT_NAME),
                                                        jsonObjectEvents.getString (AppConfigTags.EVENT_START_DATE),
                                                        jsonObjectEvents.getString (AppConfigTags.EVENT_END_DATE),
                                                        jsonObjectEvents.getString (AppConfigTags.EVENT_VENUE),
                                                        jsonObjectEvents.getString (AppConfigTags.EVENT_IMAGE),
                                                        jsonObjectEvents.getString (AppConfigTags.EVENT_CITY)
                                                ));
    
                                                if (! citiesList.contains (jsonObjectEvents.getString (AppConfigTags.EVENT_CITY))) {
                                                    citiesList.add (jsonObjectEvents.getString (AppConfigTags.EVENT_CITY));
                                                }
                                            }
    
                                            Collections.sort (citiesList, new Comparator<String> () {
                                                @Override
                                                public int compare (String s1, String s2) {
                                                    return s1.compareToIgnoreCase (s2);
                                                }
                                            });
                                            citiesSelectedList.addAll (citiesList);
                                            citiesSelectedTempList.addAll (citiesList);
                                            
                                            eventAdapter.notifyDataSetChanged ();
                                            rvEvents.setVisibility (View.VISIBLE);
                                            shimmerFrameLayout.setVisibility (View.GONE);
                                        } else {
                                            if (! showOfflineData ()) {
                                                Utils.showSnackBar (getActivity (), clMain, message, Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                            }
                                        }
    
                                    } catch (Exception e) {
                                        e.printStackTrace ();
                                        if (! showOfflineData ()) {
                                            Utils.showSnackBar (getActivity (), clMain, getResources ().getString (R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                        }
                                    }
                                } else {
                                    if (! showOfflineData ()) {
                                        Utils.showSnackBar (getActivity (), clMain, getResources ().getString (R.string.snackbar_text_unstable_internet), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                    }
                                    Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                                }
                            }
                        }
                    },
                    new Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            if (getActivity () != null && isAdded ()) {
                                NetworkResponse response = error.networkResponse;
                                if (response != null && response.data != null) {
                                    Utils.showLog (Log.ERROR, AppConfigTags.ERROR, new String (response.data), true);
                                }
                                if (! showOfflineData ()) {
                                    Utils.showSnackBar (getActivity (), clMain, getResources ().getString (R.string.snackbar_text_unstable_internet), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                }
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
                    params.put (AppConfigTags.HEADER_USER_LOGIN_KEY, userDetailsPref.getStringPref (getActivity (), UserDetailsPref.USER_LOGIN_KEY));
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest (strRequest, 30);
        } else {
            if (getActivity () != null && isAdded ()) {
                if (! showOfflineData ()) {
                    Utils.showSnackBar (getActivity (), clMain, getResources ().getString (R.string.snackbar_text_no_internet_connection_available), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_go_to_settings), new View.OnClickListener () {
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
    }
    
   /*
    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState (outState);
        outState.putStringArrayList ("selected_cities", citiesSelectedList);
    }
    
    @Override
    public void onActivityCreated (@Nullable Bundle savedInstanceState) {
        super.onActivityCreated (savedInstanceState);
        citiesSelectedList = savedInstanceState.getStringArrayList ("selected_cities");
    }
   */
    
    public void showFilterDialog () {
        final MaterialDialog dialog =
                new MaterialDialog.Builder (getActivity ())
                        .title ("Select Cities")
                        .customView (R.layout.dialog_event_filter, true)
                        .positiveText ("APPLY")
                        .typeface (SetTypeFace.getTypeface (getActivity ()), SetTypeFace.getTypeface (getActivity ()))
                        .negativeText ("CANCEL")
                        .build ();
        
        dialog.getActionButton (DialogAction.POSITIVE).setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                citiesSelectedList.clear ();
                eventTempList.clear ();
                citiesSelectedList.addAll (citiesSelectedTempList);
                if (citiesSelectedList.size () == citiesList.size ()) {
                    tvEventFilter.setText ("All Cities");
                } else if (! citiesSelectedList.isEmpty ()) {
                    if (citiesSelectedList.size () > 1) {
                        tvEventFilter.setText (citiesSelectedList.get (0) + " + " + (citiesSelectedList.size () - 1));
                    } else {
                        tvEventFilter.setText (citiesSelectedList.get (0));
                    }
                } else {
                    tvEventFilter.setText ("No City");
                }
                
                
                for (int i = 0; i < citiesSelectedList.size (); i++) {
                    String city = citiesSelectedList.get (i);
                    for (int j = 0; j < eventList.size (); j++) {
                        Event event = eventList.get (j);
                        if (event.getCity ().contains (city)) {
                            eventTempList.add (event);
                        }
                    }
                }
                
                eventAdapter = new EventAdapter (getActivity (), eventTempList);
                rvEvents.setAdapter (eventAdapter);
                rvEvents.setNestedScrollingEnabled (false);
                rvEvents.setFocusable (false);
                rvEvents.setHasFixedSize (true);
                rvEvents.setLayoutManager (new LinearLayoutManager (getActivity (), LinearLayoutManager.VERTICAL, false));
                
                
                dialog.dismiss ();
            }
        });
        
        dialog.getActionButton (DialogAction.NEGATIVE).setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                citiesSelectedTempList.clear ();
                citiesSelectedTempList.addAll (citiesSelectedList);
                dialog.dismiss ();
            }
        });
        
        final RecyclerView rvCities = (RecyclerView) dialog.findViewById (R.id.rvCities);
        
        final CityAdapter cityAdapter = new CityAdapter (citiesList);
        rvCities.setAdapter (cityAdapter);
        rvCities.setNestedScrollingEnabled (false);
        rvCities.setFocusable (false);
        rvCities.setHasFixedSize (true);
        rvCities.setLayoutManager (new LinearLayoutManager (getActivity (), LinearLayoutManager.VERTICAL, false));
        
        Utils.setTypefaceToAllViews (getActivity (), rvCities);
        
        TextView tvSelectAll = (TextView) dialog.findViewById (R.id.tvSelectAll);
        TextView tvDeselectAll = (TextView) dialog.findViewById (R.id.tvDeselectAll);
        
        tvSelectAll.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                citiesSelectedTempList.clear ();
                citiesSelectedTempList.addAll (citiesList);
                cityAdapter.notifyDataSetChanged ();
            }
        });
        
        tvDeselectAll.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                citiesSelectedTempList.clear ();
                cityAdapter.notifyDataSetChanged ();
            }
        });
        dialog.show ();
    }
    
    @Override
    public void onStart () {
        super.onStart ();
        Utils.startShimmer (shimmerFrameLayout);
    }
    
    @Override
    public void onResume () {
        super.onResume ();
        setData ();
        shimmerFrameLayout.startShimmerAnimation ();
    }
    
    @Override
    public void onPause () {
        shimmerFrameLayout.stopShimmerAnimation ();
        super.onPause ();
    }
    
    private boolean showOfflineData () {
        String response = appDataPref.getStringPref (getActivity (), AppDataPref.HOME_EVENTS);
        if (response.length () > 0) {
            try {
                JSONObject jsonObj = new JSONObject (response);
                boolean is_error = jsonObj.getBoolean (AppConfigTags.ERROR);
                String message = jsonObj.getString (AppConfigTags.MESSAGE);
                if (! is_error) {
                    eventList.clear ();
                    citiesList.clear ();
                    JSONArray jsonArrayEvents = jsonObj.getJSONArray (AppConfigTags.EVENTS);
                    for (int i = 0; i < jsonArrayEvents.length (); i++) {
                        JSONObject jsonObjectEvents = jsonArrayEvents.getJSONObject (i);
                        eventList.add (new Event (
                                jsonObjectEvents.getBoolean (AppConfigTags.EVENT_INTERESTED),
                                jsonObjectEvents.getInt (AppConfigTags.EVENT_ID),
                                R.drawable.default_event,
                                jsonObjectEvents.getString (AppConfigTags.EVENT_TYPE),
                                jsonObjectEvents.getString (AppConfigTags.EVENT_NAME),
                                jsonObjectEvents.getString (AppConfigTags.EVENT_START_DATE),
                                jsonObjectEvents.getString (AppConfigTags.EVENT_END_DATE),
                                jsonObjectEvents.getString (AppConfigTags.EVENT_VENUE),
                                jsonObjectEvents.getString (AppConfigTags.EVENT_IMAGE),
                                jsonObjectEvents.getString (AppConfigTags.EVENT_CITY)
                        ));
    
                        if (! citiesList.contains (jsonObjectEvents.getString (AppConfigTags.EVENT_CITY))) {
                            citiesList.add (jsonObjectEvents.getString (AppConfigTags.EVENT_CITY));
                        }
                    }
                    eventAdapter.notifyDataSetChanged ();
                    rvEvents.setVisibility (View.VISIBLE);
                    shimmerFrameLayout.setVisibility (View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace ();
            }
            return true;
        } else {
            return false;
        }
    }
    
    public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {
        private List<String> citiesList = new ArrayList<> ();
        
        public CityAdapter (List<String> citiesList) {
            this.citiesList = citiesList;
        }
        
        @Override
        public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
            final LayoutInflater mInflater = LayoutInflater.from (parent.getContext ());
            final View sView = mInflater.inflate (R.layout.list_item_event_filter, parent, false);
            return new ViewHolder (sView);
        }
        
        @Override
        public void onBindViewHolder (final ViewHolder holder, int position) {
            final String city = citiesList.get (position);
            Utils.setTypefaceToAllViews (getActivity (), holder.cbCity);
            holder.cbCity.setText (city);
            
            
            if (citiesSelectedTempList.contains (city)) {
                holder.cbCity.setChecked (true);
            } else {
                holder.cbCity.setChecked (false);
            }
        }
        
        @Override
        public int getItemCount () {
            return citiesList.size ();
        }
        
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            CheckBox cbCity;
            
            public ViewHolder (View view) {
                super (view);
                cbCity = (CheckBox) view.findViewById (R.id.cbCity);
                view.setOnClickListener (this);
            }
            
            @Override
            public void onClick (View v) {
                String city = citiesList.get (getLayoutPosition ());
                if (citiesSelectedTempList.contains (city)) {
                    cbCity.setChecked (false);
                    citiesSelectedTempList.remove (city);
                } else {
                    cbCity.setChecked (true);
                    citiesSelectedTempList.add (city);
                }
            }
        }
    }
}