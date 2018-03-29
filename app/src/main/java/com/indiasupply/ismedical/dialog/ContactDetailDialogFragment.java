package com.indiasupply.ismedical.dialog;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.indiasupply.ismedical.R;
import com.indiasupply.ismedical.adapter.ContactDetailAdapter;
import com.indiasupply.ismedical.fragment.ContactsFragment;
import com.indiasupply.ismedical.model.ContactDetail;
import com.indiasupply.ismedical.utils.AppConfigTags;
import com.indiasupply.ismedical.utils.RecyclerViewMargin;
import com.indiasupply.ismedical.utils.SetTypeFace;
import com.indiasupply.ismedical.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ContactDetailDialogFragment extends DialogFragment {
    ContactsFragment.MyDialogCloseListener closeListener;
    RecyclerView rvContactList;
    List<ContactDetail> contactDetailList = new ArrayList<> ();
    List<ContactDetail> contactDetailTempList = new ArrayList<> ();
    ContactDetailAdapter contactDetailAdapter;
    
    ImageView ivCancel;
    TextView tvTitle;
    
    String contacts;
    String company_name;
    
    RelativeLayout rlFilter;
    TextView tvEventFilter;
    
    ArrayList<String> citiesList = new ArrayList<String> ();
    ArrayList<String> citiesSelectedList = new ArrayList<String> ();
    ArrayList<String> citiesSelectedTempList = new ArrayList<String> ();
    
    FirebaseAnalytics mFirebaseAnalytics;
    
    public ContactDetailDialogFragment newInstance (String company_name, String contacts) {
        ContactDetailDialogFragment f = new ContactDetailDialogFragment ();
        Bundle args = new Bundle ();
        args.putString (AppConfigTags.COMPANY_NAME, company_name);
        args.putString (AppConfigTags.COMPANY_CONTACTS, contacts);
        f.setArguments (args);
        return f;
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
        View root = inflater.inflate (R.layout.fragment_dialog_contact_details, container, false);
        initView (root);
        initBundle ();
        initData ();
        initListener ();
        setData ();
        return root;
    }
    
    private void initView (View root) {
        rvContactList = (RecyclerView) root.findViewById (R.id.rvContactList);
        ivCancel = (ImageView) root.findViewById (R.id.ivCancel);
        tvTitle = (TextView) root.findViewById (R.id.tvTitle);
        rlFilter = (RelativeLayout) root.findViewById (R.id.rlFilter);
        tvEventFilter = (TextView) root.findViewById (R.id.tvEventFilter);
    }
    
    private void initBundle () {
        Bundle bundle = this.getArguments ();
        company_name = bundle.getString (AppConfigTags.COMPANY_NAME);
        contacts = bundle.getString (AppConfigTags.COMPANY_CONTACTS);
    }
    
    private void initData () {
        Utils.setTypefaceToAllViews (getActivity (), tvTitle);
        tvTitle.setText (company_name);
    
        mFirebaseAnalytics = FirebaseAnalytics.getInstance (getActivity ());
        
    
        contactDetailAdapter = new ContactDetailAdapter (getActivity (), contactDetailList);
        rvContactList.setAdapter (contactDetailAdapter);
        rvContactList.setHasFixedSize (true);
        rvContactList.setLayoutManager (new LinearLayoutManager (getActivity (), LinearLayoutManager.VERTICAL, false));
        rvContactList.setItemAnimator (new DefaultItemAnimator ());
        rvContactList.addItemDecoration (new RecyclerViewMargin ((int) Utils.pxFromDp (getActivity (), 16), (int) Utils.pxFromDp (getActivity (), 16), (int) Utils.pxFromDp (getActivity (), 16), (int) Utils.pxFromDp (getActivity (), 16), 1, 0, RecyclerViewMargin.LAYOUT_MANAGER_LINEAR, RecyclerViewMargin.ORIENTATION_VERTICAL));
    }
    
    private void initListener () {
        ivCancel.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                getDialog ().dismiss ();
            }
        });
        rlFilter.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                showFilterDialog ();
            }
        });
    
    }
    
    private void setData () {
        try {
            citiesList.clear ();
            JSONArray jsonArray = new JSONArray (contacts);
            for (int j = 0; j < jsonArray.length (); j++) {
                JSONObject jsonObject = jsonArray.getJSONObject (j);
                contactDetailList.add (new ContactDetail (
                        jsonObject.getInt (AppConfigTags.CONTACT_ID),
                        jsonObject.getInt (AppConfigTags.CONTACT_TYPE),
                        R.drawable.default_company,
                        jsonObject.getBoolean (AppConfigTags.CONTACT_FAVOURITE),
                        jsonObject.getString (AppConfigTags.CONTACT_NAME),
                        jsonObject.getString (AppConfigTags.CONTACT_LOCATION),
                        jsonObject.getString (AppConfigTags.CONTACT_CITY),
                        jsonObject.getString (AppConfigTags.CONTACT_STATE),
                        jsonObject.getString (AppConfigTags.CONTACT_PHONE),
                        jsonObject.getString (AppConfigTags.CONTACT_EMAIL),
                        jsonObject.getString (AppConfigTags.CONTACT_IMAGE)
                ));
    
    
                if (! citiesList.contains (jsonObject.getString (AppConfigTags.CONTACT_STATE))) {
                    citiesList.add (jsonObject.getString (AppConfigTags.CONTACT_STATE));
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
            
            contactDetailAdapter.notifyDataSetChanged ();
        } catch (JSONException e) {
            e.printStackTrace ();
        }
    }
    
    public void setDismissListener (ContactsFragment.MyDialogCloseListener closeListener) {
        this.closeListener = closeListener;
    }
    
    @Override
    public void onDismiss (DialogInterface dialog) {
        super.onDismiss (dialog);
        
        if (closeListener != null) {
            closeListener.handleDialogClose (null);
        }
    }
    
    public void showFilterDialog () {
        final MaterialDialog dialog =
                new MaterialDialog.Builder (getActivity ())
                        .title ("Select States")
                        .customView (R.layout.dialog_event_filter, true)
                        .positiveText ("APPLY")
                        .typeface (SetTypeFace.getTypeface (getActivity ()), SetTypeFace.getTypeface (getActivity ()))
                        .negativeText ("CANCEL")
                        .build ();
        
        dialog.getActionButton (DialogAction.POSITIVE).setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                citiesSelectedList.clear ();
                contactDetailTempList.clear ();
                citiesSelectedList.addAll (citiesSelectedTempList);
                if (citiesSelectedList.size () == citiesList.size ()) {
                    tvEventFilter.setText ("All States");
                } else if (! citiesSelectedList.isEmpty ()) {
                    if (citiesSelectedList.size () > 1) {
                        tvEventFilter.setText (citiesSelectedList.get (0) + " + " + (citiesSelectedList.size () - 1));
                    } else {
                        tvEventFilter.setText (citiesSelectedList.get (0));
                    }
                } else {
                    tvEventFilter.setText ("No State");
                }
                
                for (int i = 0; i < citiesSelectedList.size (); i++) {
                    String city = citiesSelectedList.get (i);
                    for (int j = 0; j < contactDetailList.size (); j++) {
                        ContactDetail contactDetail = contactDetailList.get (j);
                        if (contactDetail.getState ().contains (city)) {
                            contactDetailTempList.add (contactDetail);
                            Log.e ("karman", "j = " + j);
                            
                        }
                    }
                }
                
                contactDetailAdapter = new ContactDetailAdapter (getActivity (), contactDetailTempList);
                rvContactList.setAdapter (contactDetailAdapter);
                rvContactList.setHasFixedSize (true);
                rvContactList.setLayoutManager (new LinearLayoutManager (getActivity (), LinearLayoutManager.VERTICAL, false));
                rvContactList.setItemAnimator (new DefaultItemAnimator ());
                
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
    
    public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {
        private List<String> citiesList = new ArrayList<> ();
        
        public CityAdapter (List<String> citiesList) {
            this.citiesList = citiesList;
        }
        
        @Override
        public CityAdapter.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
            final LayoutInflater mInflater = LayoutInflater.from (parent.getContext ());
            final View sView = mInflater.inflate (R.layout.list_item_event_filter, parent, false);
            return new CityAdapter.ViewHolder (sView);
        }
        
        @Override
        public void onBindViewHolder (final CityAdapter.ViewHolder holder, int position) {
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