package com.indiasupply.ismedical.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.indiasupply.ismedical.R;
import com.indiasupply.ismedical.model.ContactDetail;
import com.indiasupply.ismedical.utils.AppConfigTags;
import com.indiasupply.ismedical.utils.AppConfigURL;
import com.indiasupply.ismedical.utils.Constants;
import com.indiasupply.ismedical.utils.NetworkConnection;
import com.indiasupply.ismedical.utils.UserDetailsPref;
import com.indiasupply.ismedical.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


public class ContactDetailAdapter extends RecyclerView.Adapter<ContactDetailAdapter.ViewHolder> {
    FirebaseAnalytics mFirebaseAnalytics;
    private Activity activity;
    private List<ContactDetail> contactDetailList = new ArrayList<> ();
    
    public ContactDetailAdapter (Activity activity, List<ContactDetail> contactDetailList) {
        this.activity = activity;
        this.contactDetailList = contactDetailList;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance (activity);
    }
    
    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        final LayoutInflater mInflater = LayoutInflater.from (parent.getContext ());
        final View sView = mInflater.inflate (R.layout.list_item_contacts_detail, parent, false);
        return new ViewHolder (sView);
    }
    
    @Override
    public void onBindViewHolder (final ViewHolder holder, int position) {
        final ContactDetail contactsDetail = contactDetailList.get (position);
        
        Utils.setTypefaceToAllViews (activity, holder.tvContactName);
        
        holder.tvContactName.setText (contactsDetail.getName ());
        if (contactsDetail.getCity ().length () > 0) {
            holder.tvContactLocation.setText (contactsDetail.getCity () + ", " + contactsDetail.getState ());
        } else {
            holder.tvContactLocation.setText (contactsDetail.getState ());
        }
        
        switch (contactsDetail.getType ()) {
            case 1:
                holder.tvContactType.setText ("Company Office");
                break;
            case 2:
                holder.tvContactType.setText ("Sales Office");
                break;
            case 3:
                holder.tvContactType.setText ("Service Office");
                break;
            case 4:
                holder.tvContactType.setText ("Dealer / Distributor");
                break;
        }
    
        if (contactsDetail.is_favourite ()) {
            holder.ivContactFavourite.setImageResource (R.drawable.ic_favourite_filled);
        } else {
            holder.ivContactFavourite.setImageResource (R.drawable.ic_favourite);
        }
    
    
        holder.ivContactFavourite.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                if (contactsDetail.is_favourite ()) {
                    updateFavourite (contactsDetail, holder.progressBarButton, holder.ivContactFavourite, contactsDetail.getId ());
                } else {
                    updateFavourite (contactsDetail, holder.progressBarButton, holder.ivContactFavourite, contactsDetail.getId ());
                }
            }
        });
    
        holder.rlCall.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                // [START custom_event]
                Bundle params = new Bundle ();
                params.putBoolean ("clicked", true);
                params.putInt ("contact_id", contactsDetail.getId ());
                mFirebaseAnalytics.logEvent ("contact_called", params);
                // [END custom_event]
                
                if (contactsDetail.getContact_number ().length () > 0) {
                    contactCalled (contactsDetail.getId ());
                    Utils.callPhone (activity, contactsDetail.getContact_number ());
                } else {
                    Utils.showToast (activity, "No Phone specified", false);
                }
            }
        });
    
    
        holder.rlMail.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                // [START custom_event]
                Bundle params = new Bundle ();
                params.putBoolean ("clicked", true);
                params.putInt ("contact_id", contactsDetail.getId ());
                mFirebaseAnalytics.logEvent ("contact_mailed", params);
                // [END custom_event]
                
                if (contactsDetail.getEmail ().length () > 0) {
                    contactMailed (contactsDetail.getId ());
                    Utils.shareToGmail (activity, new String[] {contactsDetail.getEmail ()}, "Enquiry", "");
                } else {
                    Utils.showToast (activity, "No email specified", false);
                }
            }
        });
    
    
        if (contactsDetail.getImage ().length () == 0) {
            holder.ivContactImage.setImageResource (contactsDetail.getIcon ());
            holder.progressBar.setVisibility (View.GONE);
        } else {
            Glide.with (activity)
                    .load (contactsDetail.getImage ())
                    .listener (new RequestListener<String, GlideDrawable> () {
                        @Override
                        public boolean onException (Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            holder.progressBar.setVisibility (View.GONE);
                            return false;
                        }
    
                        @Override
                        public boolean onResourceReady (GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.progressBar.setVisibility (View.GONE);
                            return false;
                        }
                    })
                    .error (contactsDetail.getIcon ())
                    .into (holder.ivContactImage);
        }
    }
    
    @Override
    public int getItemCount () {
        return contactDetailList.size ();
    }
    
    private void updateFavourite (final ContactDetail contactDetail, final ProgressBar progressBarButton, final ImageView ivFavourite, final int id) {
        if (NetworkConnection.isNetworkAvailable (activity)) {
            ivFavourite.setVisibility (View.GONE);
            progressBarButton.setVisibility (View.VISIBLE);
            Utils.showLog (Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_FAVOURITE, true);
            StringRequest strRequest = new StringRequest (Request.Method.POST, AppConfigURL.URL_FAVOURITE,
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
                                        switch (jsonObj.getInt (AppConfigTags.STATUS)) {
                                            case 1:
                                                contactDetail.setIs_favourite (true);
                                                progressBarButton.setVisibility (View.GONE);
                                                ivFavourite.setVisibility (View.VISIBLE);
                                                ivFavourite.setImageResource (R.drawable.ic_favourite_filled);
                                                Utils.showToast (activity, "Contact added to My Favourites", false);
                                                break;
                                            case 2:
                                                contactDetail.setIs_favourite (false);
                                                progressBarButton.setVisibility (View.GONE);
                                                ivFavourite.setVisibility (View.VISIBLE);
                                                ivFavourite.setImageResource (R.drawable.ic_favourite);
                                                Utils.showToast (activity, "Contact removed from My Favourites", false);
                                                break;
                                        }
                                    } else {
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace ();
                                    ivFavourite.setVisibility (View.VISIBLE);
                                    progressBarButton.setVisibility (View.GONE);
                                    Utils.showToast (activity, "Unstable Internet Connection", false);
                                }
                            } else {
                                ivFavourite.setVisibility (View.VISIBLE);
                                progressBarButton.setVisibility (View.GONE);
                                Utils.showToast (activity, "Unstable Internet Connection", false);
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
                            ivFavourite.setVisibility (View.VISIBLE);
                            progressBarButton.setVisibility (View.GONE);
                            Utils.showToast (activity, "Unstable Internet Connection", false);
                        }
                    }) {
                
                
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String> ();
                    params.put (AppConfigTags.TYPE, String.valueOf (3));
                    params.put (AppConfigTags.TYPE_ID, String.valueOf (id));
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }
                
                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<> ();
                    UserDetailsPref userDetailsPref = UserDetailsPref.getInstance ();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put (AppConfigTags.HEADER_USER_LOGIN_KEY, userDetailsPref.getStringPref (activity, UserDetailsPref.USER_LOGIN_KEY));
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest (strRequest, 20);
        } else {
            Utils.showToast (activity, "Unstable Internet Connection", false);
        }
    }
    
    private void contactCalled (int contact_id) {
        if (NetworkConnection.isNetworkAvailable (activity)) {
            Utils.showLog (Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_CONTACT_CALLED + "/" + contact_id, true);
            StringRequest strRequest = new StringRequest (Request.Method.GET, AppConfigURL.URL_CONTACT_CALLED + "/" + contact_id,
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
                    params.put (AppConfigTags.HEADER_USER_LOGIN_KEY, userDetailsPref.getStringPref (activity, UserDetailsPref.USER_LOGIN_KEY));
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest (strRequest, 20);
        } else {
        }
    }
    
    private void contactMailed (int contact_id) {
        if (NetworkConnection.isNetworkAvailable (activity)) {
            Utils.showLog (Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_CONTACT_MAILED + "/" + contact_id, true);
            StringRequest strRequest = new StringRequest (Request.Method.GET, AppConfigURL.URL_CONTACT_MAILED + "/" + contact_id,
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
                    params.put (AppConfigTags.HEADER_USER_LOGIN_KEY, userDetailsPref.getStringPref (activity, UserDetailsPref.USER_LOGIN_KEY));
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest (strRequest, 20);
        } else {
        }
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvContactName;
        TextView tvContactLocation;
        TextView tvContactType;
        ImageView ivContactFavourite;
        RelativeLayout rlCall;
        RelativeLayout rlMail;
        ImageView ivContactImage;
        ProgressBar progressBar;
        ProgressBar progressBarButton;
        
        public ViewHolder (View view) {
            super (view);
            tvContactName = (TextView) view.findViewById (R.id.tvContactName);
            tvContactLocation = (TextView) view.findViewById (R.id.tvContactLocation);
            tvContactType = (TextView) view.findViewById (R.id.tvContactType);
            ivContactFavourite = (ImageView) view.findViewById (R.id.ivContactFavourite);
            rlCall = (RelativeLayout) view.findViewById (R.id.rlCall);
            rlMail = (RelativeLayout) view.findViewById (R.id.rlMail);
            ivContactImage = (ImageView) view.findViewById (R.id.ivContactImage);
            progressBar = (ProgressBar) view.findViewById (R.id.progressBar);
            progressBarButton = (ProgressBar) view.findViewById (R.id.progressBarButton);
            view.setOnClickListener (this);
        }
        
        @Override
        public void onClick (View v) {
        }
    }
}