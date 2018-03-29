package com.indiasupply.ismedical.dialog;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.indiasupply.ismedical.R;
import com.indiasupply.ismedical.adapter.CategoryFilterAdapter;
import com.indiasupply.ismedical.fragment.ContactsFragment;
import com.indiasupply.ismedical.helper.DatabaseHandler;
import com.indiasupply.ismedical.model.CategoryFilter;
import com.indiasupply.ismedical.utils.AppConfigTags;
import com.indiasupply.ismedical.utils.SetTypeFace;
import com.indiasupply.ismedical.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoryFilterDialogFragment extends DialogFragment {
    ContactsFragment.MyDialogCloseListener closeListener;
    ImageView ivCancel;
    LinearLayout llDynamic;
    NestedScrollView nestedScrollView;
    CategoryFilterAdapter categoryFilterAdapter;
    ArrayList<CategoryFilter> categoryFilterList = new ArrayList<> ();
    String filter;
    TextView tvApply;
    TextView tvReset;
    
    DatabaseHandler db;
    
    public CategoryFilterDialogFragment newInstance (String filter) {
        CategoryFilterDialogFragment f = new CategoryFilterDialogFragment ();
        Bundle args = new Bundle ();
        args.putString (AppConfigTags.CATEGORY_FILTERS, filter);
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
        View root = inflater.inflate (R.layout.fragment_dialog_category_filter, container, false);
        initView (root);
        initBundle ();
        initData ();
        initListener ();
        setData ();
        
        return root;
    }
    
    private void initView (View root) {
        ivCancel = (ImageView) root.findViewById (R.id.ivCancel);
        nestedScrollView = (NestedScrollView) root.findViewById (R.id.nestedScrollView);
        llDynamic = (LinearLayout) root.findViewById (R.id.llDynamic);
        tvApply = (TextView) root.findViewById (R.id.tvApply);
        tvReset = (TextView) root.findViewById (R.id.tvReset);
    }
    
    private void initBundle () {
        Bundle bundle = this.getArguments ();
        filter = bundle.getString (AppConfigTags.CATEGORY_FILTERS);
    }
    
    private void initData () {
        Utils.setTypefaceToAllViews (getActivity (), tvApply);
        db = new DatabaseHandler (getActivity ());
        if (db.getAllFilters ().size () > 0) {
            tvApply.setBackgroundColor (Color.parseColor ("#2c98a5"));
        } else {
            tvApply.setBackgroundColor (getResources ().getColor (R.color.secondary_text2));
        }
    }
    
    private void initListener () {
        ivCancel.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                getDialog ().dismiss ();
            }
        });
    
        tvReset.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                db.deleteAllFilters ();
                getDialog ().dismiss ();
            }
        });
        
        tvApply.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                if (db.getAllFilters ().size () > 0) {
                    getDialog ().dismiss ();
                }
            }
        });
    
    
    }
    
    private void setData () {
        try {
            JSONArray jsonArray = new JSONArray (filter);
            for (int i = 0; i < jsonArray.length (); i++) {
                categoryFilterList = new ArrayList<> ();
                categoryFilterList.clear ();
                JSONObject jsonObjectfilter = jsonArray.getJSONObject (i);
                
                TextView tv = new TextView (getActivity ());
                tv.setText (jsonObjectfilter.getString (AppConfigTags.GROUP_NAME));
                tv.setLayoutParams (new LinearLayout.LayoutParams (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                tv.setTextSize (TypedValue.COMPLEX_UNIT_SP, 16);
                tv.setTypeface (SetTypeFace.getTypeface (getActivity (), "AvenirNextLTPro-Demi.otf"), Typeface.BOLD);
                tv.setTextColor (getResources ().getColor (R.color.primary_text2));
                tv.setPadding ((int) Utils.pxFromDp (getActivity (), 16), (int) Utils.pxFromDp (getActivity (), 16), (int) Utils.pxFromDp (getActivity (), 16), 0);
                llDynamic.addView (tv);
                
                JSONArray jsonArrayCategory = jsonObjectfilter.getJSONArray (AppConfigTags.CATEGORIES);
                for (int j = 0; j < jsonArrayCategory.length (); j++) {
                    JSONObject jsonObjectCategory = jsonArrayCategory.getJSONObject (j);
                    categoryFilterList.add (new CategoryFilter (
                            jsonObjectCategory.getInt (AppConfigTags.CATEGORY_ID),
                            "",
                            jsonObjectCategory.getString (AppConfigTags.CATEGORY_NAME)
                    ));
                }
                RecyclerView rv = new RecyclerView (getActivity ());
                categoryFilterAdapter = new CategoryFilterAdapter (getActivity (), categoryFilterList);
                rv.setAdapter (categoryFilterAdapter);
                rv.setHasFixedSize (true);
                rv.setNestedScrollingEnabled (false);
                rv.setFocusable (false);
                rv.setLayoutManager (new LinearLayoutManager (getActivity (), LinearLayoutManager.VERTICAL, false));
                rv.setItemAnimator (new DefaultItemAnimator ());
                //    rv.addItemDecoration(new RecyclerViewMargin((int) Utils.pxFromDp(getActivity(), 16), (int) Utils.pxFromDp(getActivity(), 16), (int) Utils.pxFromDp(getActivity(), 16), (int) Utils.pxFromDp(getActivity(), 16), 2, 0, RecyclerViewMargin.LAYOUT_MANAGER_GRID, RecyclerViewMargin.ORIENTATION_VERTICAL));
    
                categoryFilterAdapter.SetOnItemClickListener (new CategoryFilterAdapter.OnItemClickListener () {
                    @Override
                    public void onItemClick (View view, int position) {
                        if (db.getAllFilters ().size () > 0) {
                            tvApply.setBackgroundColor (Color.parseColor ("#2c98a5"));
                        } else {
                            tvApply.setBackgroundColor (getResources ().getColor (R.color.secondary_text2));
                        }
                    }
                });

//                categoryFilterAdapter.SetOnItemClickListener (new CategoryFilterAdapter.OnItemClickListener () {
//                    @Override
//                    public void onItemClick (View view, int position) {
//                        CategoryFilter categoryFilter = categoryFilterList.get (position);
//                        Log.e ("karman", "in adapter" + categoryFilter.getName ());
//                    }
//                });
//
                
                llDynamic.addView (rv);
                
                View view = new View (getActivity ());
                view.setLayoutParams (new LinearLayout.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT, (int) Utils.pxFromDp (getActivity (), 16)));
                view.setBackgroundColor (getResources ().getColor (R.color.view_color));
                llDynamic.addView (view);
            }
        } catch (Exception e) {
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
}