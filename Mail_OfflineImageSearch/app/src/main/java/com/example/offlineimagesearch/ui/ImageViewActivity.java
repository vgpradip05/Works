package com.example.offlineimagesearch.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.offlineimagesearch.R;
import com.example.offlineimagesearch.models.Photo;
import com.facebook.shimmer.ShimmerFrameLayout;

import static com.example.offlineimagesearch.ui.MainActivity.INTENT_KEY_DETAIL;

public class ImageViewActivity extends AppCompatActivity {

    ImageView ivItem;
    ShimmerFrameLayout container;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        ivItem = findViewById(R.id.iv_item);
        container = findViewById(R.id.shimmer_view_container);
        Photo photo = getIntent().getParcelableExtra(INTENT_KEY_DETAIL);
        toolbar = findViewById(R.id.toolbar_image_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (photo != null) {
            loadImage(photo);
        }

    }

    private void loadImage(Photo photo) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.DATA);
        String url = "https://farm" +
                photo.getFarm() +
                ".staticflickr.com/" +
                photo.getServer() +
                "/" +
                photo.getId() +
                "_" +
                photo.getSecret() +
                ".jpg";
        Glide.with(ImageViewActivity.this)
                .load(url)
                .apply(requestOptions).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                container.stopShimmer();
                container.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                container.stopShimmer();
                container.setVisibility(View.GONE);
                return false;
            }
        })
                .into(ivItem);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
