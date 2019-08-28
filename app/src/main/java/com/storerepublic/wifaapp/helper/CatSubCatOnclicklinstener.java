package com.storerepublic.wifaapp.helper;

import android.view.View;

import com.storerepublic.wifaapp.modelsList.catSubCatlistModel;

public interface CatSubCatOnclicklinstener {
    void onItemClick(catSubCatlistModel item);

    void onItemTouch(catSubCatlistModel item);

    void addToFavClick(View v, String position);

}
