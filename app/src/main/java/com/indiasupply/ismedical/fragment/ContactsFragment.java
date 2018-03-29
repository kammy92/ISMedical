package com.indiasupply.ismedical.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.indiasupply.ismedical.R;
import com.indiasupply.ismedical.adapter.CompanyAdapter;
import com.indiasupply.ismedical.dialog.CategoryFilterDialogFragment;
import com.indiasupply.ismedical.dialog.ContactDetailDialogFragment;
import com.indiasupply.ismedical.helper.DatabaseHandler;
import com.indiasupply.ismedical.model.Company;
import com.indiasupply.ismedical.utils.AppConfigTags;
import com.indiasupply.ismedical.utils.AppConfigURL;
import com.indiasupply.ismedical.utils.AppDataPref;
import com.indiasupply.ismedical.utils.Constants;
import com.indiasupply.ismedical.utils.NetworkConnection;
import com.indiasupply.ismedical.utils.RecyclerViewMargin;
import com.indiasupply.ismedical.utils.ShimmerFrameLayout;
import com.indiasupply.ismedical.utils.UserDetailsPref;
import com.indiasupply.ismedical.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactsFragment extends Fragment {
    RecyclerView rvContacts;
    CoordinatorLayout clMain;
    List<Company> companyAllList = new ArrayList<> ();
    List<Company> companyDisplayList = new ArrayList<> ();
    CompanyAdapter companyAdapter;
    Button btFilter;
    Button btSearch;
    ImageView ivCancel;
    ImageView ivBack;
    RelativeLayout rlSearch;
    RelativeLayout rlToolbar;
    EditText etSearch;
    
    
    LinearLayoutManager linearLayoutManager;
    
    ShimmerFrameLayout shimmerFrameLayout;
    
    String filters = "";
    boolean isLoading = false;
    
    AppDataPref appDataPref;
    
    RelativeLayout rlNoCompanyFound;
    DatabaseHandler db;
    
    boolean refresh;
    
    FirebaseAnalytics mFirebaseAnalytics;
    
    public static ContactsFragment newInstance (boolean refresh) {
        ContactsFragment fragment = new ContactsFragment ();
        Bundle args = new Bundle ();
        args.putBoolean (AppConfigTags.REFRESH_FLAG, refresh);
        fragment.setArguments (args);
        return fragment;
    }
    
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
    }
    
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate (R.layout.fragment_contact, container, false);
        initView (rootView);
        initBundle ();
        initData ();
        initListener ();
        setData ();
        return rootView;
    }
    
    private void initView (View rootView) {
        rvContacts = (RecyclerView) rootView.findViewById (R.id.rvContacts);
        btFilter = (Button) rootView.findViewById (R.id.btFilter);
        btSearch = (Button) rootView.findViewById (R.id.btSearch);
        etSearch = (EditText) rootView.findViewById (R.id.etSearch);
        rlSearch = (RelativeLayout) rootView.findViewById (R.id.rlSearch);
        rlToolbar = (RelativeLayout) rootView.findViewById (R.id.rlToolbar);
        ivBack = (ImageView) rootView.findViewById (R.id.ivBack);
        ivCancel = (ImageView) rootView.findViewById (R.id.ivCancel);
        clMain = (CoordinatorLayout) rootView.findViewById (R.id.clMain);
        shimmerFrameLayout = (ShimmerFrameLayout) rootView.findViewById (R.id.shimmer_view_container);
        rlNoCompanyFound = (RelativeLayout) rootView.findViewById (R.id.rlNoCompanyFound);
    }
    
    private void initBundle () {
        Bundle bundle = this.getArguments ();
        refresh = bundle.getBoolean (AppConfigTags.REFRESH_FLAG);
    }
    
    private void initData () {
        db = new DatabaseHandler (getActivity ());
        Utils.setTypefaceToAllViews (getActivity (), rvContacts);
        appDataPref = AppDataPref.getInstance ();
        linearLayoutManager = new LinearLayoutManager (getActivity (), LinearLayoutManager.VERTICAL, false);
        db.deleteAllFilters ();
//        linearLayoutManager.setAutoMeasureEnabled (false);
    
        mFirebaseAnalytics = FirebaseAnalytics.getInstance (getActivity ());
    
        companyAdapter = new CompanyAdapter (getActivity (), companyDisplayList);
        rvContacts.setAdapter (companyAdapter);
        rvContacts.setNestedScrollingEnabled (false);
        rvContacts.setFocusable (false);
        rvContacts.setHasFixedSize (true);
        rvContacts.setLayoutManager (linearLayoutManager);
        rvContacts.setItemAnimator (new DefaultItemAnimator ());
        rvContacts.addItemDecoration (new RecyclerViewMargin ((int) Utils.pxFromDp (getActivity (), 16), (int) Utils.pxFromDp (getActivity (), 16), (int) Utils.pxFromDp (getActivity (), 16), (int) Utils.pxFromDp (getActivity (), 16), 1, 0, RecyclerViewMargin.LAYOUT_MANAGER_LINEAR, RecyclerViewMargin.ORIENTATION_VERTICAL));
    
        if (! refresh) {
            showOfflineData ();
        }
    }
    
    private void initListener () {
        btFilter.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                // [START custom_event]
                Bundle params = new Bundle ();
                params.putBoolean ("clicked", true);
                mFirebaseAnalytics.logEvent ("contacts_filter", params);
                // [END custom_event]
                
                android.app.FragmentManager fm = getActivity ().getFragmentManager ();
                android.app.FragmentTransaction ft = fm.beginTransaction ();
                CategoryFilterDialogFragment dialog = new CategoryFilterDialogFragment ().newInstance (filters);
                dialog.setDismissListener (new MyDialogCloseListener () {
                    @Override
                    public void handleDialogClose (DialogInterface dialog) {
                        if (db.getAllFilters ().size () > 0) {
//                            Utils.showToast (getActivity (), "in ondismmiss " + db.getAllFilters ().size () + " filter selected", false);
                            companyDisplayList.clear ();
                            for (int i = 0; i < db.getAllFilters ().size (); i++) {
                                String item = db.getAllFilters ().get (i);
                                for (Company company : companyAllList) {
                                    if (company.getCategory ().contains (item)) {
                                        if (! companyDisplayList.contains (company)) {
                                            companyDisplayList.add (company);
                                        }
                                    }
                                }
                            }
                            companyAdapter = new CompanyAdapter (getActivity (), companyDisplayList);
                            rvContacts.setAdapter (companyAdapter);
                            companyAdapter.SetOnItemClickListener (new CompanyAdapter.OnItemClickListener () {
                                @Override
                                public void onItemClick (View view, int position) {
                                    Company contact = companyDisplayList.get (position);
                                    android.app.FragmentTransaction ft = getActivity ().getFragmentManager ().beginTransaction ();
                                    new ContactDetailDialogFragment ().newInstance (contact.getName (), contact.getContacts ()).show (ft, "Contacts");
                                }
                            });
    
                            if (companyDisplayList.size () == 0) {
                                rlNoCompanyFound.setVisibility (View.VISIBLE);
                                rvContacts.setVisibility (View.GONE);
                            } else {
                                rvContacts.setVisibility (View.VISIBLE);
                                rlNoCompanyFound.setVisibility (View.GONE);
                            }
                        } else {
                            companyAdapter = new CompanyAdapter (getActivity (), companyAllList);
                            rvContacts.setAdapter (companyAdapter);
                            companyAdapter.SetOnItemClickListener (new CompanyAdapter.OnItemClickListener () {
                                @Override
                                public void onItemClick (View view, int position) {
                                    Company contact = companyAllList.get (position);
                                    android.app.FragmentTransaction ft = getActivity ().getFragmentManager ().beginTransaction ();
                                    new ContactDetailDialogFragment ().newInstance (contact.getName (), contact.getContacts ()).show (ft, "Contacts");
                                }
                            });
                            rlNoCompanyFound.setVisibility (View.GONE);
                            rvContacts.setVisibility (View.VISIBLE);
//                            Utils.showToast (getActivity (), "in ondismmiss no filter selected", false);
                        }
                    }
                });
                dialog.show (ft, "MyDialog");
//                fm.executePendingTransactions ();
//
//                dialog.getDialog ().setOnDismissListener (new DialogInterface.OnDismissListener () {
//                    @Override
//                    public void onDismiss (DialogInterface dialogInterface) {
//                        Utils.showToast (getActivity (), "heelo karman in on dismmiss", false);
//                    }
//                });


//                CategoryFilterDialogFragment dialog = new CategoryFilterDialogFragment ().newInstance (filters);
//                dialog.show (fm, "category_dialog");
                
                
            }
        });
        companyAdapter.SetOnItemClickListener (new CompanyAdapter.OnItemClickListener () {
            @Override
            public void onItemClick (View view, int position) {
                Company contact = companyDisplayList.get (position);
    
                // [START custom_event]
                Bundle params = new Bundle ();
                params.putBoolean ("clicked", true);
                params.putString ("contact_name", contact.getName ());
                mFirebaseAnalytics.logEvent ("contact_detail_open", params);
                // [END custom_event]
    
                android.app.FragmentTransaction ft = getActivity ().getFragmentManager ().beginTransaction ();
                ContactDetailDialogFragment dialog = new ContactDetailDialogFragment ().newInstance (contact.getName (), contact.getContacts ());
                dialog.setDismissListener (new MyDialogCloseListener () {
                    @Override
                    public void handleDialogClose (DialogInterface dialog) {
                        setData ();
                    }
                });
                dialog.show (ft, "Contacts");
            }
        });
    
    
        
/*
        getParentFragment ().getView ().setOnKeyListener (new View.OnKeyListener () {
            @Override
            public boolean onKey (View v, int keyCode, KeyEvent event) {
                if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                    //This is the filter
                    if (event.getAction () != KeyEvent.ACTION_UP)
                        return true;
                    else {
                        if (rlSearch.getVisibility () == View.VISIBLE) {
                            final Handler handler = new Handler ();
                            handler.postDelayed (new Runnable () {
                                @Override
                                public void run () {
                                    etSearch.setText ("");
                                }
                            }, 300);
                            final Handler handler2 = new Handler ();
                            handler2.postDelayed (new Runnable () {
                                @Override
                                public void run () {
                                    final InputMethodManager imm = (InputMethodManager) getActivity ().getSystemService (Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow (getView ().getWindowToken (), 0);
                                }
                            }, 600);
                            rlSearch.setVisibility (View.GONE);
                        } else {
                            getActivity ().onBackPressed ();
                        }
                        //Hide your keyboard here!!!!!!
                        return true; // pretend we've processed it
                    }
                } else
                    return false; // pass on to be processed as normal
            }
        });
*/
        
        
        etSearch.addTextChangedListener (new TextWatcher () {
            @Override
            public void onTextChanged (CharSequence s, int start, int before, int count) {
                if (s.length () > 0) {
                    ivCancel.setVisibility (View.VISIBLE);
                } else {
                    ivCancel.setVisibility (View.GONE);
                }
            }
            
            @Override
            public void beforeTextChanged (CharSequence s, int start, int count, int after) {
                
            }
            
            @Override
            public void afterTextChanged (Editable s) {
                if (s.toString ().length () == 0) {
                    companyAdapter = new CompanyAdapter (getActivity (), companyAllList);
                    rvContacts.setAdapter (companyAdapter);
                    companyAdapter.SetOnItemClickListener (new CompanyAdapter.OnItemClickListener () {
                        @Override
                        public void onItemClick (View view, int position) {
                            Company contact = companyAllList.get (position);
                            android.app.FragmentTransaction ft = getActivity ().getFragmentManager ().beginTransaction ();
                            new ContactDetailDialogFragment ().newInstance (contact.getName (), contact.getContacts ()).show (ft, "Contacts");
                        }
                    });
                    rlNoCompanyFound.setVisibility (View.GONE);
                    rvContacts.setVisibility (View.VISIBLE);
                }
                if (s.toString ().length () > 0) {
                    companyDisplayList.clear ();
                    for (Company company : companyAllList) {
                        if (company.getName ().toUpperCase ().contains (s.toString ().toUpperCase ()) ||
                                company.getName ().toLowerCase ().contains (s.toString ().toLowerCase ())) {// ||
//                                company.getCategory ().toLowerCase ().contains (s.toString ().toLowerCase ()) ||
//                                company.getCategory ().toUpperCase ().contains (s.toString ().toUpperCase ())) {
                            companyDisplayList.add (company);
                        }
                    }
    
                    companyAdapter = new CompanyAdapter (getActivity (), companyDisplayList);
                    rvContacts.setAdapter (companyAdapter);
                    companyAdapter.SetOnItemClickListener (new CompanyAdapter.OnItemClickListener () {
                        @Override
                        public void onItemClick (View view, int position) {
                            Company contact = companyDisplayList.get (position);
                            android.app.FragmentTransaction ft = getActivity ().getFragmentManager ().beginTransaction ();
                            new ContactDetailDialogFragment ().newInstance (contact.getName (), contact.getContacts ()).show (ft, "Contacts");
                        }
                    });
    
                    if (companyDisplayList.size () == 0) {
                        rlNoCompanyFound.setVisibility (View.VISIBLE);
                        rvContacts.setVisibility (View.GONE);
                    } else {
                        rvContacts.setVisibility (View.VISIBLE);
                        rlNoCompanyFound.setVisibility (View.GONE);
                    }
                }
            }
        });
    
    
        ivBack.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                new Handler ().postDelayed (new Runnable () {
                    @Override
                    public void run () {
                        final InputMethodManager imm = (InputMethodManager) getActivity ().getSystemService (Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow (getView ().getWindowToken (), 0);
                    }
                }, 1000);
                new Handler ().postDelayed (new Runnable () {
                    @Override
                    public void run () {
                        etSearch.setText ("");
                        rlToolbar.setVisibility (View.VISIBLE);
                    }
                }, 300);
                rlSearch.setVisibility (View.GONE);
            }
        });
    
        btSearch.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                // [START custom_event]
                Bundle params = new Bundle ();
                params.putBoolean ("clicked", true);
                mFirebaseAnalytics.logEvent ("contacts_search", params);
                // [END custom_event]
    
                new Handler ().postDelayed (new Runnable () {
                    @Override
                    public void run () {
                        rlSearch.setVisibility (View.VISIBLE);
                        etSearch.requestFocus ();
                    }
                }, 300);
                new Handler ().postDelayed (new Runnable () {
                    @Override
                    public void run () {
                        final InputMethodManager imm = (InputMethodManager) getActivity ().getSystemService (Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput (InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                }, 1000);
                rlToolbar.setVisibility (View.GONE);
            }
        });
    
        ivCancel.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                etSearch.setText ("");
            }
        });
    }
    
    private void setData () {
        if (NetworkConnection.isNetworkAvailable (getActivity ())) {
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.URL_HOME_COMPANIES, true);
            StringRequest strRequest = new StringRequest (Request.Method.GET, AppConfigURL.URL_HOME_COMPANIES,
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
                                            companyAllList.clear ();
                                            companyDisplayList.clear ();
                                            appDataPref.putStringPref (getActivity (), AppDataPref.HOME_CONTACTS, response);
                                            JSONArray jsonArrayCompany = jsonObj.getJSONArray (AppConfigTags.COMPANIES);
                                            filters = jsonObj.getJSONArray (AppConfigTags.CATEGORY_FILTERS).toString ();
                                            for (int i = 0; i < jsonArrayCompany.length (); i++) {
                                                JSONObject jsonObjectCompany = jsonArrayCompany.getJSONObject (i);
                                                companyAllList.add (new Company (
                                                        jsonObjectCompany.getInt (AppConfigTags.COMPANY_ID),
                                                        R.drawable.default_company,
                                                        jsonObjectCompany.getJSONArray (AppConfigTags.COMPANY_CONTACTS).length (),
                                                        jsonObjectCompany.getString (AppConfigTags.COMPANY_NAME),
                                                        jsonObjectCompany.getString (AppConfigTags.COMPANY_DESCRIPTION),
                                                        jsonObjectCompany.getString (AppConfigTags.COMPANY_CATEGORIES),
                                                        jsonObjectCompany.getString (AppConfigTags.COMPANY_EMAIL),
                                                        jsonObjectCompany.getString (AppConfigTags.COMPANY_WEBSITE),
                                                        jsonObjectCompany.getString (AppConfigTags.COMPANY_IMAGE),
                                                        jsonObjectCompany.getJSONArray (AppConfigTags.COMPANY_CONTACTS).toString ()));
                                            }
//                                            onScrolledToBottom ();
                                            companyDisplayList.addAll (companyAllList);
                                            companyAdapter.notifyDataSetChanged ();
                                            rvContacts.setVisibility (View.VISIBLE);
                                            shimmerFrameLayout.setVisibility (View.GONE);
                                            new Handler ().postDelayed (new Runnable () {
                                                @Override
                                                public void run () {
                                                    btSearch.setVisibility (View.VISIBLE);
                                                    btFilter.setVisibility (View.VISIBLE);
                                                }
                                            }, 500);
                                        } else {
                                            if (! showOfflineData ()) {
                                                Utils.showSnackBar (getActivity (), clMain, message, Snackbar.LENGTH_LONG, null, null);
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace ();
                                        if (! showOfflineData ()) {
                                            Utils.showSnackBar (getActivity (), clMain, getResources ().getString (R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                        }
                                    }
                                } else {
                                    Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                                    if (! showOfflineData ()) {
                                        Utils.showSnackBar (getActivity (), clMain, getResources ().getString (R.string.snackbar_text_unstable_internet), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                    }
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
    
    @Override
    public void onStart () {
        super.onStart ();
        Log.e ("karman", "onStart");
        Utils.startShimmer (shimmerFrameLayout);
    }
    
    @Override
    public void onResume () {
        super.onResume ();
        Log.e ("karman", "onResume");
        shimmerFrameLayout.startShimmerAnimation ();
    }
    
    @Override
    public void onPause () {
        shimmerFrameLayout.stopShimmerAnimation ();
        Log.e ("karman", "onPause");
        super.onPause ();
    }
    
    private boolean showOfflineData () {
        String response = appDataPref.getStringPref (getActivity (), AppDataPref.HOME_CONTACTS);
        if (response.length () > 0) {
            try {
                JSONObject jsonObj = new JSONObject (response);
                boolean is_error = jsonObj.getBoolean (AppConfigTags.ERROR);
                String message = jsonObj.getString (AppConfigTags.MESSAGE);
                if (! is_error) {
                    companyAllList.clear ();
                    companyDisplayList.clear ();
                    JSONArray jsonArrayCompany = jsonObj.getJSONArray (AppConfigTags.COMPANIES);
                    filters = jsonObj.getJSONArray (AppConfigTags.CATEGORY_FILTERS).toString ();
                    for (int i = 0; i < jsonArrayCompany.length (); i++) {
                        JSONObject jsonObjectCompany = jsonArrayCompany.getJSONObject (i);
                        companyAllList.add (new Company (
                                jsonObjectCompany.getInt (AppConfigTags.COMPANY_ID),
                                R.drawable.default_company,
                                jsonObjectCompany.getJSONArray (AppConfigTags.COMPANY_CONTACTS).length (),
                                jsonObjectCompany.getString (AppConfigTags.COMPANY_NAME),
                                jsonObjectCompany.getString (AppConfigTags.COMPANY_DESCRIPTION),
                                jsonObjectCompany.getString (AppConfigTags.COMPANY_CATEGORIES),
                                jsonObjectCompany.getString (AppConfigTags.COMPANY_EMAIL),
                                jsonObjectCompany.getString (AppConfigTags.COMPANY_WEBSITE),
                                jsonObjectCompany.getString (AppConfigTags.COMPANY_IMAGE),
                                jsonObjectCompany.getJSONArray (AppConfigTags.COMPANY_CONTACTS).toString ()));
                    }
                    companyDisplayList.addAll (companyAllList);
                    companyAdapter.notifyDataSetChanged ();
                    rvContacts.setVisibility (View.VISIBLE);
                    shimmerFrameLayout.setVisibility (View.GONE);
                    new Handler ().postDelayed (new Runnable () {
                        @Override
                        public void run () {
                            btSearch.setVisibility (View.VISIBLE);
                            btFilter.setVisibility (View.VISIBLE);
                        }
                    }, 500);
                }
            } catch (Exception e) {
                e.printStackTrace ();
            }
            return true;
        } else {
            return false;
        }
    }
    
    public interface MyDialogCloseListener {
        public void handleDialogClose (DialogInterface dialog);
    }
}