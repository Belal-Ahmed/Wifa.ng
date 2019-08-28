package com.storerepublic.wifaapp.packages;


import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.storerepublic.wifaapp.R;
import com.storerepublic.wifaapp.signinorup.Login_Fragment;


public class PackagesAndPayment extends AppCompatActivity {

    private TextView loading;
    private WebView myWebView;
    private WebView loginWebView;
    private ProgressBar progressBar;
    private String email, pass;

    private String LOGIN_URL = "https://wifa.ng/login/";
    private static final String URL = "https://wifa.ng/packages-and-pricing/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packages_and_payment);
        getSupportActionBar().setTitle("Packages");

        loginWebView = (WebView) findViewById(R.id.login);
        myWebView = (WebView) findViewById(R.id.webView);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        loading = (TextView) findViewById(R.id.loading_tv);

        //User login and Password
        email = Login_Fragment.getEmail("email_key", PackagesAndPayment.this);
        pass = Login_Fragment.getPass("pass_key", PackagesAndPayment.this);

        // JavaScript Permission For Login
        loginWebView.getSettings().setJavaScriptEnabled(true);
        loginWebView.getSettings().setDomStorageEnabled(true);
        loginWebView.loadUrl(LOGIN_URL);

        //JavaScript Permission For Payment
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.loadUrl(URL);


        //For Login
        if(email != null && pass != null){

            loginWebView.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);

                    //Auto fill and Login
                    final String js = "javascript:" +
                            "document.getElementById('sb_reg_email').value = '" + email + "';" +
                            "document.getElementsByName('sb_reg_password')[0].value = '" + pass + "';" +
                            "document.getElementById('sb_login_submit').click()";

                    view.loadUrl(js);

                }
            });
        }

        //For Payment
        myWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {

                //Hide Specific Part of WebView
                view.loadUrl("javascript:(function() { " +
                        "document.getElementById('sb_site_logo').style.display='none'; " +
                        "document.getElementsByClassName('footer-top new-demo')[0].style.display='none'; " +
                        "document.getElementsByClassName('sprt-top-bar')[0].style.display='none'; " +
                        "document.getElementsByClassName('heading-text')[0].style.display='none'; " +
                        "document.getElementsByClassName('form-group')[0].style.display='none'; " +
                        "document.getElementsByClassName('latest-book-menu')[0].style.display='none'; " +
                        "document.getElementsByClassName('bk-search-area')[0].style.display='none'; " +
                        "document.getElementsByClassName(' bread-3 page-header-area')[0].style.display='none'; " +
                        "document.getElementsByClassName('col-lg-2 col-xs-12 col-sm-4 col-md-3 col-lg-offset-1')[0].style.display='none'; " +
                        "})()");
                view.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);
            }
        });
    }

    //If Back Pressed
    @Override
    public void onBackPressed() {
        if (myWebView.canGoBack()) {
            myWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }


}
