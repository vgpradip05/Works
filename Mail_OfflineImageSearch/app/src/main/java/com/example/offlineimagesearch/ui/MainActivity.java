package com.example.offlineimagesearch.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import android.widget.Toast;

import com.example.offlineimagesearch.R;
import com.example.offlineimagesearch.adapters.RecyclerAdapter;
import com.example.offlineimagesearch.database.DatabaseUtils;
import com.example.offlineimagesearch.listners.RecyclerClickListner;
import com.example.offlineimagesearch.models.Photo;
import com.example.offlineimagesearch.models.Rsp;
import com.example.offlineimagesearch.network.APIClient;
import com.example.offlineimagesearch.utils.ItemOffsetDecoration;
import com.example.offlineimagesearch.utils.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements RecyclerClickListner {

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    List<Photo> photos;
    AutoCompleteTextView etSearch;
    Button btSearch;
    Toolbar toolbar;
    GridLayoutManager gridLayoutManager;
    ShimmerFrameLayout container;
    int currentItems,scrolledItems,totalItems,pageCount=1;
    boolean isScrolling;
    ProgressDialog progressDialog;
    String key;
    public static final String INTENT_KEY_DETAIL = "_ik_Detail";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initVars();

        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                key = etSearch.getText().toString();
                if(!TextUtils.isEmpty(key)) {
                    fetchImages(key,true);//Clear list key may have changed
                }else{
                    Toast.makeText(MainActivity.this, getString(R.string.pl_type_something),Toast.LENGTH_LONG).show();
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = gridLayoutManager.getChildCount();
                totalItems = gridLayoutManager.getItemCount();
                scrolledItems = gridLayoutManager.findFirstVisibleItemPosition();
                if(isScrolling && (currentItems+scrolledItems == totalItems)){
                    isScrolling = false;
                    pageCount++;
                    fetchImages(key,false);// dont clear list as endless scrolling needs to be performed
                }
            }
        });

    }
    private void fetchImages(String key,boolean toClearList) {
        if(Utils.isNetworkAvailable(MainActivity.this)){
            proceedFetch(key,toClearList);
        }else{
            loadOffline(key);
        }
    }

    private void loadOffline(String key) {
        photos.clear();
        photos.addAll(DatabaseUtils.getPhotos(MainActivity.this, key));
        Toast.makeText(MainActivity.this, getString(R.string.loading_images_offline),Toast.LENGTH_LONG).show();
        recyclerAdapter.notifyDataSetChanged();
    }

    private void proceedFetch(final String key, final boolean toClearList) {
        Call<Rsp> call1 = APIClient.getAPIManager().getPhotos(Utils.Constants.API_KEY,
                Utils.Constants.METHOD, key, String.valueOf(pageCount),
                Utils.Constants.FORMAT, Utils.Constants.NO_JSON_CALLBACK);
        Log.i("Request : ", call1.request().toString());
        progressDialog.show();
        call1.enqueue(new Callback<Rsp>() {
            @Override
            public void onResponse(Call<Rsp> call, Response<Rsp> response) {
                //Log.i("Response",response.body().getPhotos().getTotal());
                if (progressDialog != null) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
                if(toClearList){
                    photos.clear();
                }
                if (response.body() != null) {
                    photos.addAll(response.body().getPhotos().getPhotos());
                    DatabaseUtils.insertOrUpdatePhotos(MainActivity.this, response.body().getPhotos().getPhotos(), key);
                }
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Rsp> call, Throwable t) {
                if (progressDialog != null) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
                Log.i("Failure", t.getMessage());
            }
        });
    }

    private void initVars() {
        etSearch = findViewById(R.id.et_search);
        etSearch.setAdapter(new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_dropdown_item_1line,
                DatabaseUtils.getDistinctSearchKeys(MainActivity.this)
                ));
        etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etSearch.showDropDown();
            }
        });

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        btSearch = findViewById(R.id.bt_search);
        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        photos = new ArrayList<>();
        recyclerView = findViewById(R.id.rv_images);
        recyclerAdapter = new RecyclerAdapter(MainActivity.this, photos, this);
        gridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new ItemOffsetDecoration(5));
        recyclerView.setAdapter(recyclerAdapter);
        container = findViewById(R.id.shimmer_view_container);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.setCancelable(false);

    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(MainActivity.this,ImageViewActivity.class);
        intent.putExtra(INTENT_KEY_DETAIL,photos.get(position));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.grid_2:
                gridLayoutManager.setSpanCount(2);
                break;
            case R.id.grid_3:
                gridLayoutManager.setSpanCount(3);
                break;
            case R.id.grid_4:
                gridLayoutManager.setSpanCount(4);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
