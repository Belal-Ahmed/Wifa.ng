package com.storerepublic.wifaapp.helper;

import android.view.View;

import com.storerepublic.wifaapp.modelsList.myAdsModel;

public interface MyAdsOnclicklinstener {

    void onItemClick(myAdsModel item);

    void delViewOnClick(View v, int position);

    void editViewOnClick(View v, int position);

}
