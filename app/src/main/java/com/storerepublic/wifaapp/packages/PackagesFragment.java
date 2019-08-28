package com.storerepublic.wifaapp.packages;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.storerepublic.wifaapp.R;
import com.storerepublic.wifaapp.helper.OnItemClickListenerPackages;
import com.storerepublic.wifaapp.home.HomeActivity;
import com.storerepublic.wifaapp.modelsList.PackagesModel;
import com.storerepublic.wifaapp.packages.adapter.ItemPackagesAdapter;
import com.storerepublic.wifaapp.utills.AnalyticsTrackers;
import com.storerepublic.wifaapp.utills.Network.RestService;
import com.storerepublic.wifaapp.utills.SettingsMain;
import com.storerepublic.wifaapp.utills.UrlController;

public class PackagesFragment extends Fragment {

    public static final int PAYPAL_REQUEST_CODE = 123;
    RecyclerView recyclerView;
    ArrayList<PackagesModel> listitems = new ArrayList<>();
    SettingsMain settingsMain;
    ItemPackagesAdapter itemPackagesAdapter;
    RestService restService;
    boolean spinnerTouched = false;
    String packageId, packageType;
    String merchantKey, userCredentials;
    JSONObject responseJsonObject, jsonObjectResponse;
    String billing_error;
    private TextView textViewEmptyData;


    public PackagesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_packages, container, false);

        settingsMain = new SettingsMain(getActivity());
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        textViewEmptyData = view.findViewById(R.id.noPackagesfound);
        textViewEmptyData.setVisibility(View.GONE);
        if (settingsMain.getAppOpen()) {
            restService = UrlController.createService(RestService.class);
        } else
            restService = UrlController.createService(RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), getActivity());

        final LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(MyLayoutManager);
        SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(true);

        adforest_getData();
        return view;
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void adforest_getData() {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            if (!HomeActivity.checkLoading)
                SettingsMain.showDilog(getActivity());

            Call<ResponseBody> myCall = restService.getPackagesDetails(UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info Packages Responce", "" + responseObj.toString());
                            HomeActivity.checkLoading = false;

                            final JSONObject response = new JSONObject(responseObj.body().string());
                            jsonObjectResponse = response;
                            getActivity().setTitle(response.getJSONObject("extra").getString("page_title"));

                            if (response.getBoolean("success")) {
                                final JSONObject responseData = response.getJSONObject("data");
                                Log.d("info Packages object", "" + response.getJSONObject("data"));
                                Log.d("info Packages object", "" + response.getJSONObject("data").getJSONArray("payment_types").length());

                                adforest_initializeList(response.getJSONObject("data").getJSONArray("products"), response.getJSONObject("data").getJSONArray("payment_types"));
                                responseJsonObject = response.getJSONObject("data");
                                billing_error = response.getJSONObject("extra").getString("billing_error");
                                itemPackagesAdapter = new ItemPackagesAdapter(getActivity(), listitems);

                                if (listitems.size() > 0 & recyclerView != null) {
                                    recyclerView.setAdapter(itemPackagesAdapter);

                                    itemPackagesAdapter.setOnItemClickListener(new OnItemClickListenerPackages() {
                                        @Override
                                        public void onItemClick(PackagesModel item) {
                                            Intent intent = new Intent(getActivity(), StripePayment.class);
                                            intent.putExtra("id", item.getBtnTag());
                                            startActivity(intent);
                                        }

                                        @Override
                                        public void onItemTouch() {
                                            Log.d("info Spinner Touched", "Real Touch Felt.");
                                            spinnerTouched = true;
                                        }

                                        @Override
                                        public void onItemSelected(final PackagesModel item, final int spinnerPosition) {
                                            if (spinnerTouched) {
                                                if (spinnerPosition > 0) {
                                                    adforest_byPackage(item, spinnerPosition);

                                                }
                                            }
                                        }
                                    });
                                }
                            } else {
                                textViewEmptyData.setVisibility(View.VISIBLE);
                                textViewEmptyData.setText(response.get("message").toString());
//                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        SettingsMain.hideDilog();

                    } catch (JSONException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    } catch (IOException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    SettingsMain.hideDilog();
                    Log.d("info Packages error", String.valueOf(t));
                    Log.d("info Packages error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    private void adforest_byPackage(final PackagesModel item, final int spinnerPosition) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle(settingsMain.getGenericAlertTitle());
        alert.setCancelable(false);
        alert.setMessage(settingsMain.getGenericAlertMessage());
        alert.setPositiveButton(settingsMain.getGenericAlertOkText(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,
                                int which) {
                switch (item.getSpinnerValue().get(spinnerPosition)) {
                    case "stripe":
                        Intent intent = new Intent(getActivity(), StripePayment.class);

                        intent.putExtra("id", item.getBtnTag());
                        intent.putExtra("packageType", item.getSpinnerValue().get(spinnerPosition));
                        startActivity(intent);
                        break;
                    case "paypal":
                        try {
                            if (responseJsonObject.getBoolean("is_paypal_key")) {

                                //PayPal checkout
                                packageId = item.getBtnTag();
                                packageType = item.getSpinnerValue().get(spinnerPosition);

                                adforest_PayPal(item.getPackagesPrice(), responseJsonObject.getJSONObject("paypal"), item.getPlanType());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "app_inapp":
                        try {
                            if (!item.getJsonObject().getString("android").equals("")) {
                                packageId = item.getBtnTag();
                                packageType = item.getSpinnerValue().get(spinnerPosition);
                                Log.d("info purchase", item.getJsonObject().getString("android"));
                                Intent intent1 = new Intent(getActivity(), InAppPurchaseActivity.class);
                                JSONObject jsonObject = jsonObjectResponse.getJSONObject("extra").getJSONObject("android");
                                if (jsonObject.getBoolean("in_app_on")) {

                                    intent1.putExtra("id", item.getBtnTag());
                                    intent1.putExtra("packageType", item.getSpinnerValue().get(spinnerPosition));
                                    intent1.putExtra("porductId", item.getJsonObject().getString("android"));
                                    intent1.putExtra("activityName", jsonObject.getString("title_text"));
                                    intent1.putExtra("billing_error", billing_error);
                                    intent1.putExtra("one_time", jsonObject.getJSONObject("message").getString("one_time"));
                                    intent1.putExtra("no_market", jsonObject.getJSONObject("message").getString("no_market"));
                                    intent1.putExtra("LICENSE_KEY", jsonObject.getString("secret_code"));
                                    startActivity(intent1);
                                } else {
                                    showToast(item.getJsonObject().getString("message"));
                                }
                            } else
                                showToast(item.getJsonObject().getString("message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        packageId = item.getBtnTag();
                        packageType = item.getSpinnerValue().get(spinnerPosition);
                        JsonObject object = new JsonObject();
                        object.addProperty("package_id", packageId);
                        object.addProperty("payment_from", packageType);
                        adforest_CheckOut(object);


                }

                dialog.dismiss();
            }
        });
        alert.setNegativeButton(settingsMain.getGenericAlertCancelText(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.show();


    }

    public void adforest_initializeList(JSONArray timeline, JSONArray packagesArray) {
        listitems.clear();

        for (int i = 0; i < timeline.length(); i++) {

            PackagesModel item = new PackagesModel();
            JSONObject firstEvent;
            try {
                firstEvent = (JSONObject) timeline.get(i);
                if (firstEvent != null) {

                    item.setBtnTag(firstEvent.getString("product_id"));

                    item.setPlanType(firstEvent.getString("product_title"));
                    item.setPrice(firstEvent.getString("product_price"));
                    item.setBtnText(firstEvent.getString("product_btn"));

                    item.setFreeAds(firstEvent.getString("free_ads_text") + ": " + firstEvent.getString("free_ads_value"));
                    item.setFeatureAds(firstEvent.getString("featured_ads_text") + ": " + firstEvent.getString("featured_ads_value"));
                    item.setValidaty(firstEvent.getString("days_text") + ": " + firstEvent.getString("days_value"));
                    item.setBumupAds(firstEvent.getString("bump_ads_text") + ": " + firstEvent.getString("bump_ads_value"));

                    Log.d("info packages amount", firstEvent.getJSONObject("product_amount").toString());
                    item.setPackagesPrice(firstEvent.getJSONObject("product_amount").getString("value"));
                    item.setSpinnerData(packagesArray);
                    item.setSpinnerValue(packagesArray);
                    item.setJsonObject(firstEvent.getJSONObject("product_appCode"));
                    listitems.add(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void replaceFragment(Fragment someFragment, String tag) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.right_enter, R.anim.left_out, R.anim.left_enter, R.anim.right_out);
        transaction.replace(R.id.frameContainer, someFragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    @Override
    public void onResume() {
        try {
            if (settingsMain.getAnalyticsShow() && !settingsMain.getAnalyticsId().equals(""))
                AnalyticsTrackers.getInstance().trackScreenView("Packages");
            super.onResume();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void adforest_PayPal(String packagePayment, JSONObject jsonObject, String packageName) {
        PayPalConfiguration
                config = null;
        try {
            Log.d("info payment", jsonObject.toString());
            config = new PayPalConfiguration()
                    // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
                    // or live (ENVIRONMENT_PRODUCTION)
                    .environment(jsonObject.getString("mode"))
                    .clientId(jsonObject.getString("api_key"))
                    .merchantName(jsonObject.getString("merchant_name"))
                    .merchantPrivacyPolicyUri(Uri.parse(jsonObject.getString("privecy_url")))
                    .merchantUserAgreementUri(Uri.parse(jsonObject.getString("agreement_url")));
            Intent intent = new Intent(getActivity(), PayPalService.class);

            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

            getActivity().startService(intent);

            //Creating a paypalpayment
            PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(packagePayment)), jsonObject.getString("currency"), packageName,
                    PayPalPayment.PAYMENT_INTENT_SALE);

            //Creating Paypal Payment activity intent
            Intent intent1 = new Intent(getActivity(), PaymentActivity.class);

            //putting the paypal configuration to the intent
            intent1.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

            //Puting paypal payment to the intent
            intent1.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

            //Starting the intent activity for result
            //the request code will be used on the method onActivityResult
            startActivityForResult(intent1, PAYPAL_REQUEST_CODE);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If the result is from paypal
        if (requestCode == PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.i("paymentExample", paymentDetails);
                        String paymentId = confirm.toJSONObject()
                                .getJSONObject("response").getString("id");

                        String payment_client = confirm.getPayment()
                                .toJSONObject().toString();

                        Log.e("info ", "paymentId: " + paymentId
                                + ", payment_json: " + payment_client);

                        JsonObject params = new JsonObject();
                        params.addProperty("package_id", packageId);
                        params.addProperty("source_token", paymentId);
                        params.addProperty("payment_from", packageType);
                        params.addProperty("payment_client", payment_client);

                        adforest_CheckOut(params);

                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }


    }

    public void adforest_CheckOut(JsonObject params) {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());
            Log.d("info  object", params.toString());
            Call<ResponseBody> myCall = restService.postCheckout(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info Checkout Resp", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            Log.d("info Checkout object", "" + response.toString());
                            if (response.getBoolean("success")) {
                                settingsMain.setPaymentCompletedMessage(response.get("message").toString());
                                adforest_getDataForThankYou();
                            } else
                                Toast.makeText(getContext(), response.get("message").toString(), Toast.LENGTH_SHORT).show();

                        }
                        SettingsMain.hideDilog();
                    } catch (JSONException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    } catch (IOException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (t instanceof TimeoutException) {
                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info Checkout ", "NullPointert Exception" + t.getLocalizedMessage());
                        SettingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info Checkout err", String.valueOf(t));
                        Log.d("info Checkout err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), settingsMain.getAlertDialogTitle("error"), Toast.LENGTH_SHORT).show();
        }
    }

    public void adforest_getDataForThankYou() {
        if (SettingsMain.isConnectingToInternet(getActivity())) {
            Call<ResponseBody> myCall = restService.getPaymentCompleteData(UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info ThankYou Details", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                JSONObject responseData = response.getJSONObject("data");

                                Log.d("info ThankYou object", "" + response.getJSONObject("data"));

                                Intent intent = new Intent(getActivity(), Thankyou.class);
                                intent.putExtra("data", responseData.getString("data"));
                                intent.putExtra("order_thankyou_title", responseData.getString("order_thankyou_title"));
                                intent.putExtra("order_thankyou_btn", responseData.getString("order_thankyou_btn"));
                                startActivity(intent);
                                SettingsMain.hideDilog();
                            } else {
                                SettingsMain.hideDilog();
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        SettingsMain.hideDilog();
                    } catch (IOException e) {
                        e.printStackTrace();
                        SettingsMain.hideDilog();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    SettingsMain.hideDilog();
                    Log.d("info ThankYou error", String.valueOf(t));
                    Log.d("info ThankYou error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }


}

