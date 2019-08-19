package com.example.offlineimagesearch.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.offlineimagesearch.R;
import com.example.offlineimagesearch.listners.RecyclerClickListner;
import com.example.offlineimagesearch.models.Photo;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.PhotosViewHolder> {

    private List<Photo> photos;
    private Context context;
    private RecyclerClickListner recyclerClickListner;
    class PhotosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivItem;
        ShimmerFrameLayout container;
        RecyclerClickListner recyclerClickListner;
        PhotosViewHolder(View view,RecyclerClickListner recyclerClickListner) {
            super(view);
            ivItem = view.findViewById(R.id.iv_item);
            container = view.findViewById(R.id.shimmer_view_container);
            view.setOnClickListener(this);
            this.recyclerClickListner = recyclerClickListner;
        }

        @Override
        public void onClick(View view) {
            recyclerClickListner.onItemClick(getAdapterPosition());
        }
    }


    public RecyclerAdapter(Context context, List<Photo> photos,RecyclerClickListner recyclerClickListner) {
        this.photos = photos;
        this.context = context;
        this.recyclerClickListner = recyclerClickListner;
    }

    @NonNull
    @Override
    public PhotosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_layout, parent, false);

        return new PhotosViewHolder(itemView,recyclerClickListner);
    }

    @Override
    public void onBindViewHolder(@NonNull final PhotosViewHolder holder, int position) {
        holder.container.startShimmer();
        Photo photo = photos.get(position);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.DATA);
        requestOptions.centerCrop();
        requestOptions.override(150,150);
        String url = "https://farm" +
                photo.getFarm() +
                ".staticflickr.com/" +
                photo.getServer() +
                "/" +
                photo.getId() +
                "_" +
                photo.getSecret() +
                "_" +
                "t" +
                ".jpg";
        Glide.with(context)
                .load(url)
                .apply(requestOptions).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                holder.container.stopShimmer();
                holder.container.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.container.stopShimmer();
                holder.container.setVisibility(View.GONE);
                return false;
            }
        })
                .into(holder.ivItem);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }
}