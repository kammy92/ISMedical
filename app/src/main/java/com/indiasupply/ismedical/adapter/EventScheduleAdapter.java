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
import com.indiasupply.ismedical.model.EventSchedule;
import com.indiasupply.ismedical.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by l on 26/09/2017.
 */

public class EventScheduleAdapter extends RecyclerView.Adapter<EventScheduleAdapter.ViewHolder> {
    OnItemClickListener mItemClickListener;
    private Activity activity;
    private List<EventSchedule> eventScheduleList = new ArrayList<> ();
    
    public EventScheduleAdapter (Activity activity, List<EventSchedule> eventScheduleList) {
        this.activity = activity;
        this.eventScheduleList = eventScheduleList;
    }
    
    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        final LayoutInflater mInflater = LayoutInflater.from (parent.getContext ());
        final View sView = mInflater.inflate (R.layout.list_item_event_schedule, parent, false);
        return new ViewHolder (sView);
    }
    
    @Override
    public void onBindViewHolder (final ViewHolder holder, int position) {
        final EventSchedule eventSchedule = eventScheduleList.get (position);
        Utils.setTypefaceToAllViews (activity, holder.tvEventTiming);
    
        if (eventSchedule.getEnd_time ().equalsIgnoreCase ("00:00:00")) {
            holder.tvEventTiming.setText (Utils.convertTimeFormat (eventSchedule.getDate (), "yyyy-MM-dd", "dd MMM") + " " + Utils.convertTimeFormat (eventSchedule.getStart_time (), "HH:mm:ss", "HH:mm") + " Onwards");
        } else {
            holder.tvEventTiming.setText (Utils.convertTimeFormat (eventSchedule.getDate (), "yyyy-MM-dd", "dd MMM") + " (" + Utils.convertTimeFormat (eventSchedule.getStart_time (), "HH:mm:ss", "HH:mm") + " - " + Utils.convertTimeFormat (eventSchedule.getEnd_time (), "HH:mm:ss", "HH:mm") + ")");
        }
        
        holder.tvEventDescription.setText (eventSchedule.getDescription ());
        holder.tvEventLocation.setText (eventSchedule.getLocation ());
    
        if (eventSchedule.getImage ().length () == 0) {
            holder.ivEventImage.setImageResource (eventSchedule.getIcon ());
            holder.progressBar.setVisibility (View.GONE);
        } else {
            holder.progressBar.setVisibility (View.VISIBLE);
            Glide.with (activity)
                    .load (eventSchedule.getImage ())
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
                    .error (eventSchedule.getIcon ())
                    .into (holder.ivEventImage);
        }
    }
    
    @Override
    public int getItemCount () {
        return eventScheduleList.size ();
    }
    
    public void SetOnItemClickListener (final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    
    public interface OnItemClickListener {
        public void onItemClick (View view, int position);
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvEventTiming;
        TextView tvEventDescription;
        TextView tvEventLocation;
        ImageView ivEventImage;
    
        ProgressBar progressBar;
        
        public ViewHolder (View view) {
            super (view);
            tvEventTiming = (TextView) view.findViewById (R.id.tvEventTiming);
            tvEventDescription = (TextView) view.findViewById (R.id.tvEventDescription);
            tvEventLocation = (TextView) view.findViewById (R.id.tvEventLocation);
            ivEventImage = (ImageView) view.findViewById (R.id.ivEventSchedule);
            progressBar = (ProgressBar) view.findViewById (R.id.progressBar);
            view.setOnClickListener (this);
        }
        
        @Override
        public void onClick (View v) {
//            mItemClickListener.onItemClick (v, getLayoutPosition ());
        }
    }
}