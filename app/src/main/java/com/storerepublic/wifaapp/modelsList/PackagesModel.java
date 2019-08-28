package com.storerepublic.wifaapp.modelsList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PackagesModel {

    private String validaty;
    private String freeAds;
    private String featureAds;
    private String btnText;
    private String btnTag;
    private String price;
    private String planType;
    private ArrayList<String> spinnerData;
    private ArrayList<String> spinnerValue;
    private String packagesPrice;
    private String bumupAds;
    private JSONObject jsonObject;

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getBumupAds() {
        return bumupAds;
    }

    public void setBumupAds(String bumupAds) {
        this.bumupAds = bumupAds;
    }

    public String getFeatureAds() {
        return featureAds;
    }

    public void setFeatureAds(String featureAds) {
        this.featureAds = featureAds;
    }

    public String getBtnText() {
        return btnText;
    }

    public void setBtnText(String btnText) {
        this.btnText = btnText;
    }

    public String getBtnTag() {
        return btnTag;
    }

    public void setBtnTag(String btnTag) {
        this.btnTag = btnTag;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }


    public String getValidaty() {
        return validaty;
    }

    public void setValidaty(String validaty) {
        this.validaty = validaty;
    }

    public String getFreeAds() {
        return freeAds;
    }

    public void setFreeAds(String freeAds) {
        this.freeAds = freeAds;
    }

    public ArrayList<String> getSpinnerData() {
        return this.spinnerData;
    }

    public void setSpinnerData(JSONArray spinnerData) {
        ArrayList<String> arrayList = new ArrayList<>();
        if (spinnerData != null) {
            for (int i = 0; i < spinnerData.length(); i++) {
                try {
                    arrayList.add(spinnerData.getJSONObject(i).getString("value"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        this.spinnerData = arrayList;
    }

    public ArrayList<String> getSpinnerValue() {
        return this.spinnerValue;
    }

    public void setSpinnerValue(JSONArray spinnerData) {
        ArrayList<String> arrayList = new ArrayList<>();
        if (spinnerData != null) {
            for (int i = 0; i < spinnerData.length(); i++) {
                try {
                    arrayList.add(spinnerData.getJSONObject(i).getString("key"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        this.spinnerValue = arrayList;
    }

    public String getPackagesPrice() {
        return this.packagesPrice;
    }

    public void setPackagesPrice(String packagePrice) {

        this.packagesPrice = packagePrice;
    }
}
