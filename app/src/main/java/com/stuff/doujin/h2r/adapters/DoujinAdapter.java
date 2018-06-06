package com.stuff.doujin.h2r.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.stuff.doujin.h2r.R;
import com.stuff.doujin.h2r.data.Doujin;

import java.util.List;

public class DoujinAdapter extends RecyclerView.Adapter<DoujinAdapter.DoujinViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<Doujin> doujinList;
    private DoujinAdapterListener doujinAdapterListener;

    public interface DoujinAdapterListener {
        void onDoujinClick(Doujin doujin);

        void onBottomReached();
    }

    public DoujinAdapter(@NonNull Context context, List<Doujin> doujinList) {
        this.context = context;
        this.doujinList = doujinList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public DoujinViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.grid_doujin_item, parent, false);
        DoujinViewHolder holder = new DoujinViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(DoujinViewHolder holder, int position) {
        Doujin doujin = doujinList.get(position);
        holder.textView.setText(doujin.doujinTitle);

        RequestOptions options = new RequestOptions();
        options.centerCrop();

        Glide.with(context).clear(holder.imageView);
        Glide.with(context).load(doujin.imageUrl).apply(options).into(holder.imageView);

        if (position == doujinList.size() - 1){
            doujinAdapterListener.onBottomReached();
        }
    }

    @Override
    public int getItemCount() {
        return doujinList.size();
    }

    public void setClickListener(DoujinAdapterListener doujinAdapterListener) {
        this.doujinAdapterListener = doujinAdapterListener;
    }

    class DoujinViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        ImageView imageView;
        TextView textView;

        public DoujinViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.doujin_title);
            Typeface type = Typeface.createFromAsset(context.getAssets(),"fonts/PTSans-NarrowBold.ttf");
            textView.setTypeface(type);

            imageView = itemView.findViewById(R.id.doujin_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (doujinAdapterListener != null) {
                doujinAdapterListener.onDoujinClick(doujinList.get(getAdapterPosition()));
            }
        }

    }
}