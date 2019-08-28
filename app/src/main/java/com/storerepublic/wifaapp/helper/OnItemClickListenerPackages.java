package com.storerepublic.wifaapp.helper;

import com.storerepublic.wifaapp.modelsList.PackagesModel;

public interface OnItemClickListenerPackages {
    void onItemClick(PackagesModel item);

    void onItemTouch();

    void onItemSelected(PackagesModel packagesModel, int spinnerPosition);
}
