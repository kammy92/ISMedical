package com.indiasupply.ismedical.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.indiasupply.ismedical.R;
import com.indiasupply.ismedical.model.EventExhibitor;
import com.indiasupply.ismedical.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class EventExhibitorAdapter extends RecyclerView.Adapter<EventExhibitorAdapter.ViewHolder> {
    OnItemClickListener mItemClickListener;
    private Activity activity;
    private List<EventExhibitor> eventExhibitorList = new ArrayList<> ();
    
    public EventExhibitorAdapter (Activity activity, List<EventExhibitor> eventExhibitorList) {
        this.activity = activity;
        this.eventExhibitorList = eventExhibitorList;
    }
    
    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        final LayoutInflater mInflater = LayoutInflater.from (parent.getContext ());
        final View sView = mInflater.inflate (R.layout.list_item_event_exhibitor, parent, false);
        return new ViewHolder (sView);
    }
    
    @Override
    public void onBindViewHolder (final ViewHolder holder, int position) {
        final EventExhibitor eventExhibitor = eventExhibitorList.get (position);
        Utils.setTypefaceToAllViews (activity, holder.tvExhibitorName);
    
        holder.tvExhibitorName.setText (eventExhibitor.getName ());
        holder.tvExhibitorStall.setText (eventExhibitor.getStall ());
    
        if (eventExhibitor.getImage ().length () == 0) {
            holder.ivExhibitor.setImageResource (eventExhibitor.getIcon ());
            holder.progressBar.setVisibility (View.GONE);
        } else {
            Glide.with (activity)
                    .load (eventExhibitor.getImage ())
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
                    .error (eventExhibitor.getIcon ())
                    .into (holder.ivExhibitor);
        }
    }
    
    @Override
    public int getItemCount () {
        return eventExhibitorList.size ();
    }
    
    public void SetOnItemClickListener (final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    
    
    public interface OnItemClickListener {
        public void onItemClick (View view, int position);
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivExhibitor;
        TextView tvExhibitorName;
        TextView tvExhibitorStall;
        ProgressBar progressBar;
        
        public ViewHolder (View view) {
            super (view);
            ivExhibitor = (ImageView) view.findViewById (R.id.ivExhibitor);
            tvExhibitorName = (TextView) view.findViewById (R.id.tvExhibitorName);
            tvExhibitorStall = (TextView) view.findViewById (R.id.tvExhibitorStall);
            progressBar = (ProgressBar) view.findViewById (R.id.progressBar);
            view.setOnClickListener (this);
        }
        
        @Override
        public void onClick (View v) {
        }
    }
}
