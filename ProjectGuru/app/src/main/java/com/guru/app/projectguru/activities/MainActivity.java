package com.guru.app.projectguru.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.guru.app.projectguru.R;
import com.guru.app.projectguru.adapters.InternAdapter;
import com.guru.app.projectguru.database.FireBaseHelper;
import com.guru.app.projectguru.database.LocalPreferences;
import com.guru.app.projectguru.interfaces.AlertClickListners;
import com.guru.app.projectguru.interfaces.RequestCallback;
import com.guru.app.projectguru.listners.RecyclerTouchListener;
import com.guru.app.projectguru.models.DataModel;
import com.guru.app.projectguru.models.Identity;
import com.guru.app.projectguru.utils.AnimateUtils;
import com.guru.app.projectguru.utils.Constants;
import com.guru.app.projectguru.utils.DialogUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private List<DataModel> dataModelList = new ArrayList<>();
    private RecyclerView recyclerView;
    private InternAdapter mAdapter;
    View progressOverlay;
    Identity identity;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private TextView tvNoRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressOverlay = findViewById(R.id.progress_overlay_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        tvNoRecords = findViewById(R.id.tv_no_records);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (getIntent().getExtras() != null) {
            identity = (Identity) getIntent().getExtras().get(Constants.IntentKeys.IDENTITY);
        }
        if (identity != null) {
            if (identity.isIntern()) {
                tvNoRecords.setText("Once Univeristy will request you our Agent will contact you");
                tvNoRecords.setVisibility(View.VISIBLE);
            } else {
                mAdapter = new InternAdapter(dataModelList,identity.getReqEmails(),MainActivity.this);
                recyclerView.setAdapter(mAdapter);
                prepareInternData();
            }
        } else {
        }
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(MainActivity.this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                openDialog(position);
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

    private void openDialog(final int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            // ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_send_request, null);
        dialogBuilder.setView(dialogView);

        TextView tvName= dialogView.findViewById(R.id.tv_name);
        TextView tvCity= dialogView.findViewById(R.id.tv_city);
        TextView tvUnv= dialogView.findViewById(R.id.tv_unv);
        TextView tvWorksFor= dialogView.findViewById(R.id.tv_works_for);
        TextView tvSkills= dialogView.findViewById(R.id.tv_skills);
        tvName.setText(dataModelList.get(position).getName());
        tvCity.setText(dataModelList.get(position).getCity());
        tvUnv.setText(dataModelList.get(position).getUniversity());
        tvWorksFor.setText(dataModelList.get(position).getOrganization());
        tvSkills.setText(dataModelList.get(position).getSkill());
        Button btSendReq = dialogView.findViewById(R.id.bt_send_req);
        Button btCancel = dialogView.findViewById(R.id.bt_cancel);

        if(dataModelList.get(position).isRequested()){
            btSendReq.setVisibility(View.GONE);
            btCancel.setText(getResources().getString(R.string.ok));
        }
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        btSendReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                sendRequest(dataModelList.get(position).geteMail().split("\\.")[0]);
            }
        });
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void sendRequest(final String s) {
            Identity internIdent = new Identity();
            internIdent.setIntern(true);
            internIdent.setRegerstered(true);
            FireBaseHelper.sendObjectToServer(Constants.FireBasePaths.DATABASE_PATH_EMAILS+"/"+

                    FirebaseAuth.getInstance().getCurrentUser().getEmail().split("\\.")[0]+"/reqEmails/",s, internIdent, new RequestCallback() {
                @Override
                public void onStart() {
                    AnimateUtils.showLoadingView(progressOverlay);
                }

                @Override
                public void onSuccess(Object object) {
                    AnimateUtils.hideLoadingView(progressOverlay);
                    performSecondMapping(s);
                }

                @Override
                public void onError(Object object) {
                    AnimateUtils.hideLoadingView(progressOverlay);
                }
            });
    }
    private void performSecondMapping(String s){
        FireBaseHelper.sendObjectToServer(Constants.FireBasePaths.DATABASE_PATH_EMAILS+"/"+

                s+"/reqEmails/",FirebaseAuth.getInstance().getCurrentUser().getEmail().split("\\.")[0], identity, new RequestCallback() {
            @Override
            public void onStart() {
                AnimateUtils.showLoadingView(progressOverlay);
            }

            @Override
            public void onSuccess(Object object) {
                AnimateUtils.hideLoadingView(progressOverlay);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Object object) {
                AnimateUtils.hideLoadingView(progressOverlay);
            }
        });

    }

    private void prepareInternData() {
        FireBaseHelper.getSnapShotFromServer(Constants.FireBasePaths.DATABASE_PATH_UPLOADS, new RequestCallback() {
            @Override
            public void onStart() {
                AnimateUtils.showLoadingView(progressOverlay);
            }

            @Override
            public void onSuccess(Object object) {
                AnimateUtils.hideLoadingView(progressOverlay);
                //GenericTypeIndicator<List<DataModel>> typeIndicator = new GenericTypeIndicator<List<DataModel>>() {};
                //dataModelList.addAll(((DataSnapshot)object).getValue(typeIndicator));
                for (DataSnapshot postSnapshot : ((DataSnapshot) object).getChildren()) {
                    DataModel intern = postSnapshot.getValue(DataModel.class);
                    dataModelList.add(intern);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Object object) {
                AnimateUtils.hideLoadingView(progressOverlay);
            }
        });
        // recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_your_profile) {
            startActivity(new Intent(MainActivity.this, Profile.class));
        } else if (id == R.id.nav_your_requests) {

        } else if (id == R.id.nav_logout) {
            DialogUtils.getSimpleDialog(MainActivity.this, "Are you sure you want to Logout?", new AlertClickListners() {
                @Override
                public void onPositiveButtonClick() {
                }

                @Override
                public void onNegativeButtonClick() {
                }

                @Override
                public void onNeutralButtonClick() {
                    if(FirebaseAuth.getInstance()!=null){
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(MainActivity.this,LoginActivity.class));
                        finish();
                    }
                }
            }).show();
        } else if (id == R.id.nav_about_us) {
            /*ShareCompat.IntentBuilder.from(context)
                    .setType(Constants.TEXT_PLAIN)
                    .setChooserTitle(Constants.CHOOSER_TITLE)
                    .setText(Constants.PLAY_STORE + getPackageName())
                    .startChooser();*/
        }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column
            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)
                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
