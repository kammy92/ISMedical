package com.indiasupply.ismedical.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.indiasupply.ismedical.R;
import com.indiasupply.ismedical.helper.DatabaseHandler;
import com.indiasupply.ismedical.model.CategoryFilter;
import com.indiasupply.ismedical.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class CategoryFilterAdapter extends RecyclerView.Adapter<CategoryFilterAdapter.ViewHolder> {
    OnItemClickListener mItemClickListener;
    DatabaseHandler db;
    private Activity activity;
    private List<CategoryFilter> categoryFilters = new ArrayList<> ();
    
    public CategoryFilterAdapter (Activity activity, List<CategoryFilter> categoryFilters) {
        this.activity = activity;
        this.categoryFilters = categoryFilters;
        db = new DatabaseHandler (activity);
    }
    
    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        final LayoutInflater mInflater = LayoutInflater.from (parent.getContext ());
        final View sView = mInflater.inflate (R.layout.list_item_category_filter, parent, false);
        return new ViewHolder (sView);
    }
    
    @Override
    public void onBindViewHolder (final ViewHolder holder, int position) {//        runEnterAnimation (holder.itemView);
        final CategoryFilter filter = categoryFilters.get (position);
        Utils.setTypefaceToAllViews (activity, holder.tvCategoryName);
        holder.tvCategoryName.setText (filter.getName ());
        holder.cbCategorySelect.setChecked (filter.is_selected ());
    
        for (int i = 0; i < db.getAllFilters ().size (); i++) {
            if (db.getAllFilters ().get (i).equalsIgnoreCase (filter.getName ())) {
                holder.cbCategorySelect.setChecked (true);
                filter.setIs_selected (true);
            }
        }
    }
    
    @Override
    public int getItemCount () {
        return categoryFilters.size ();
    }
    
    public void SetOnItemClickListener (final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    
    public interface OnItemClickListener {
        public void onItemClick (View view, int position);
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvCategoryName;
        CheckBox cbCategorySelect;
        
        public ViewHolder (View view) {
            super (view);
            tvCategoryName = (TextView) view.findViewById (R.id.tvCategoryName);
            cbCategorySelect = (CheckBox) view.findViewById (R.id.cbCategorySelect);
            
            view.setOnClickListener (this);
        }
        
        @Override
        public void onClick (View v) {
            CategoryFilter categoryFilter = categoryFilters.get (getLayoutPosition ());
            if (db.isFilterExist (categoryFilter.getName ())) {
                cbCategorySelect.setChecked (false);
                categoryFilter.setIs_selected (false);
                db.deleteFilter (categoryFilter.getName ());
                Log.e ("karman", "item removed : " + categoryFilter.getName ());
            } else {
                categoryFilter.setIs_selected (true);
                cbCategorySelect.setChecked (true);
                db.insertFilter (categoryFilter.getName ());
                Log.e ("karman", "item added : " + categoryFilter.getName ());
            }
            mItemClickListener.onItemClick (v, getLayoutPosition ());
        }
    }
}