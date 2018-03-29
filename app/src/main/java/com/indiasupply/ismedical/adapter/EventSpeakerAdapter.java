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
import com.indiasupply.ismedical.model.EventSpeaker;
import com.indiasupply.ismedical.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class EventSpeakerAdapter extends RecyclerView.Adapter<EventSpeakerAdapter.ViewHolder> {
    OnItemClickListener mItemClickListener;
    private Activity activity;
    private List<EventSpeaker> eventSpeakerList = new ArrayList<> ();
    
    public EventSpeakerAdapter (Activity activity, List<EventSpeaker> eventSpeakerList) {
        this.activity = activity;
        this.eventSpeakerList = eventSpeakerList;
    }
    
    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        final LayoutInflater mInflater = LayoutInflater.from (parent.getContext ());
        final View sView = mInflater.inflate (R.layout.list_item_event_speaker, parent, false);
        return new ViewHolder (sView);
    }
    
    @Override
    public void onBindViewHolder (final ViewHolder holder, int position) {//        runEnterAnimation (holder.itemView);
        final EventSpeaker eventSpeaker = eventSpeakerList.get (position);
        Utils.setTypefaceToAllViews (activity, holder.tvSpeakerName);
    
        if (eventSpeaker.getImage ().length () == 0) {
            holder.ivSpeaker.setImageResource (eventSpeaker.getIcon ());
            holder.progressBar.setVisibility (View.GONE);
        } else {
            Glide.with (activity)
                    .load (eventSpeaker.getImage ())
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
                    .error (eventSpeaker.getIcon ())
                    .into (holder.ivSpeaker);
        }
    
        holder.tvSpeakerName.setText (eventSpeaker.getName ());
        holder.tvSpeakerQualification.setText (eventSpeaker.getQualification ());
    }
    
    @Override
    public int getItemCount () {
        return eventSpeakerList.size ();
    }
    
    public void SetOnItemClickListener (final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    
    public interface OnItemClickListener {
        public void onItemClick (View view, int position);
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivSpeaker;
        TextView tvSpeakerName;
        TextView tvSpeakerQualification;
        ProgressBar progressBar;
        
        public ViewHolder (View view) {
            super (view);
            ivSpeaker = (ImageView) view.findViewById (R.id.ivSpeaker);
            tvSpeakerName = (TextView) view.findViewById (R.id.tvSpeakerName);
            tvSpeakerQualification = (TextView) view.findViewById (R.id.tvSpeakerQualification);
            progressBar = (ProgressBar) view.findViewById (R.id.progressBar);
            
            view.setOnClickListener (this);
        }
        
        @Override
        public void onClick (View v) {
        }
    }
}
