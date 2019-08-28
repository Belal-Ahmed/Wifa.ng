package com.storerepublic.wifaapp.userAndSellers;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.storerepublic.wifaapp.R;
import com.storerepublic.wifaapp.modelsList.blockUserModel;
import com.storerepublic.wifaapp.userAndSellers.adapter.ItemBlockUserAdapter;
import com.storerepublic.wifaapp.utills.Network.RestService;
import com.storerepublic.wifaapp.utills.SettingsMain;
import com.storerepublic.wifaapp.utills.UrlController;

/**
 * A simple {@link Fragment} subclass.
 */
public class Blocked_UserFragment extends Fragment {

    RecyclerView recyclerView;
    RestService restService;
    SettingsMain settingsMain;
    ItemBlockUserAdapter itemBlockUserAdapter;
    private ArrayList<blockUserModel> blockUserModelArrayList = new ArrayList<>();

    public Blocked_UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blocked_user, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settingsMain = new SettingsMain(getActivity());

        recyclerView = view.findViewById(R.id.blockedUserRecylerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        final GridLayoutManager MyLayoutManager = new GridLayoutManager(getActivity(), 1);
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(MyLayoutManager);
        restService = UrlController.createService(RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), getActivity());
        adforest_getBlockedUser();
    }

    private void adforest_getBlockedUser() {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());


            //post Type Mehtod for get Bid Details
            Call<ResponseBody> myCall = restService.getBlockedUsers(UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info blockUser Respon", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                Log.d("info blockUser Data", "" + response.getJSONObject("data"));

                                getActivity().setTitle(response.getJSONObject("data").getString("page_title"));


                                if (response.getJSONObject("data").getJSONArray("users").length() > 0) {
                                    adforest_initializeList(response.getJSONObject("data").getJSONArray("users"));
                                } else {
//                                    textViewEmptyData.setVisibility(View.VISIBLE);
//                                    textViewEmptyData.setText(response.getJSONObject("data").get("no_top_bidders").toString());
                                }
                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
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
                    if (t instanceof TimeoutException) {
                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info blockUser Excptn ", "NullPointert Exception" + t.getLocalizedMessage());
                        SettingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info blockUser error", String.valueOf(t));
                        Log.d("info blockUser error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    public void adforest_initializeList(JSONArray jsonArray) {
        blockUserModelArrayList.clear();

        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                blockUserModel item = new blockUserModel();
                item.setId(jsonObject.getString("id"));
                item.setImage(jsonObject.getString("image"));
                item.setLocaiton(jsonObject.getString("location"));
                item.setName(jsonObject.getString("name"));
                item.setText(jsonObject.getString("text"));

                blockUserModelArrayList.add(item);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        itemBlockUserAdapter = new ItemBlockUserAdapter(getActivity(), blockUserModelArrayList);
        recyclerView.setAdapter(itemBlockUserAdapter);
        itemBlockUserAdapter.setOnItemClickListener((item, position) -> adforest_unBlockUser(item.getId(), position));
    }

    void adforest_unBlockUser(String userId, final int position) {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("user_id", userId);
            Log.d("info send blockUser", position + "" + params.toString());

            Call<ResponseBody> myCall = restService.postUnblockUser(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info blockUser Respon", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                blockUserModelArrayList.remove(position);
                                itemBlockUserAdapter.notifyItemRemoved(position);
                                itemBlockUserAdapter.notifyItemRangeChanged(position, blockUserModelArrayList.size());
                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
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
                    if (t instanceof TimeoutException) {
                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info blockUser Excptn ", "NullPointert Exception" + t.getLocalizedMessage());
                        SettingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info blockUser error", String.valueOf(t));
                        Log.d("info blockUser error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }
}
