package com.storerepublic.wifaapp.Search;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.storerepublic.wifaapp.R;
import com.storerepublic.wifaapp.helper.LocaleHelper;
import com.storerepublic.wifaapp.utills.Admob;
import com.storerepublic.wifaapp.utills.AnalyticsTrackers;
import com.storerepublic.wifaapp.utills.SettingsMain;

public class SearchActivity extends AppCompatActivity {

    SettingsMain settingsMain;
    String ad_title, requestFrom, ad_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        settingsMain = new SettingsMain(this);

        Intent intent = getIntent();
        if (intent != null) {
            ad_title = getIntent().getStringExtra("ad_title");
            ad_id = getIntent().getStringExtra("catId");
            requestFrom = getIntent().getStringExtra("requestFrom");

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(settingsMain.getMainColor()));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView imageViewRefresh = (ImageView) findViewById(R.id.refresh);
        ImageView imageView = (ImageView) findViewById(R.id.collapse);
        imageView.setVisibility(View.GONE);

        imageViewRefresh.setOnClickListener(v -> SearchActivity.this.recreate());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setTitle("Search");
        toolbar.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));

        Fragment_search fragment_search = new Fragment_search();
        Bundle bundle = new Bundle();
        bundle.putString("catId", ad_id);
        bundle.putString("requestFrom", requestFrom);
        bundle.putString("ad_title", ad_title);
        fragment_search.setArguments(bundle);
        if (settingsMain.getBannerShow())
            adforest_bannersAds();
        startFragment(fragment_search);
        updateViews(settingsMain.getAlertDialogMessage("gmap_lang"));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void updateViews(String languageCode) {
        LocaleHelper.setLocale(this, languageCode);
    }

    public void adforest_bannersAds() {
        if (settingsMain.getAdsShow() && !settingsMain.getBannerAdsId().equals("")) {
            if (settingsMain.getAdsPostion().equals("top")) {
                LinearLayout frameLayout = (LinearLayout) findViewById(R.id.adSearcAd);
                Admob.adforest_Displaybanners(this, frameLayout);
            } else {
                LinearLayout frameLayout = (LinearLayout) findViewById(R.id.adSearcAdBottom);
                FrameLayout maimFrame = (FrameLayout) findViewById(R.id.frameContainer);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.bottomMargin = 120;
                maimFrame.setLayoutParams(layoutParams);
                Admob.adforest_Displaybanners(this, frameLayout);
            }
        }
    }

    public void startFragment(Fragment someFragment) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.frameContainer);

        if (fragment == null) {
            fragment = someFragment;

            fm.beginTransaction()
                    .add(R.id.frameContainer, fragment)
                    .commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getSupportFragmentManager().findFragmentByTag("Fragment_search") != null)
            getSupportFragmentManager().findFragmentByTag("Fragment_search").setRetainInstance(true);
    }

    @Override
    protected void onResume() {
        try {
            if (settingsMain.getAnalyticsShow() && !settingsMain.getAnalyticsId().equals("")) {
                AnalyticsTrackers.getInstance().trackScreenView("Advance Search");
            }
            super.onResume();
            if (getSupportFragmentManager().findFragmentByTag("Fragment_search") != null)
                getSupportFragmentManager().findFragmentByTag("Fragment_search").getRetainInstance();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_enter, R.anim.right_out);
    }
}
