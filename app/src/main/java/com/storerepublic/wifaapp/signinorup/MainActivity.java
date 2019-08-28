package com.storerepublic.wifaapp.signinorup;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.storerepublic.wifaapp.R;
import com.storerepublic.wifaapp.helper.LocaleHelper;
import com.storerepublic.wifaapp.utills.SettingsMain;

public class MainActivity extends AppCompatActivity {
    private static FragmentManager fragmentManager;
    SettingsMain settingsMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        settingsMain = new SettingsMain(this);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();

        // If savedinstnacestate is null then replace login fragment
        if (savedInstanceState == null) {
            if (getIntent().getBooleanExtra("page", false)) {
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.frameContainer, new SignUp_Fragment(),
                                Utils.Login_Fragment).commit();
            } else
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.frameContainer, new Login_Fragment(),
                                Utils.Login_Fragment).commit();
        }
        updateViews(settingsMain.getAlertDialogMessage("gmap_lang"));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void updateViews(String languageCode) {
        LocaleHelper.setLocale(this, languageCode);
    }

    // Replace Login Fragment with animation
    protected void adforest_replaceLoginFragment() {
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                .replace(R.id.frameContainer, new Login_Fragment(),
                        Utils.Login_Fragment).commit();
    }

    @Override
    public void onBackPressed() {

        // Find the tag of signup and forgot password fragment
        Fragment SignUp_Fragment = fragmentManager
                .findFragmentByTag(Utils.SignUp_Fragment);
        Fragment ForgotPassword_Fragment = fragmentManager
                .findFragmentByTag(Utils.ForgotPassword_Fragment);
        Fragment VerifyAccount_Fragment = fragmentManager
                .findFragmentByTag(Utils.VerifyAccount_Fragment);


        // Check if both are null or not
        // If both are not null then replace login fragment else do backpressed
        // task

        if (SignUp_Fragment != null)
            adforest_replaceLoginFragment();
        else if (ForgotPassword_Fragment != null)
            adforest_replaceLoginFragment();
        else if (VerifyAccount_Fragment != null)
            adforest_replaceLoginFragment();
        else {
            super.onBackPressed();
            overridePendingTransition(R.anim.left_enter, R.anim.right_out);
        }
    }
}
