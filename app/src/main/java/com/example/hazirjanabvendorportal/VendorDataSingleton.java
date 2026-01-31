package com.example.hazirjanabvendorportal;


import org.json.JSONException;
import org.json.JSONObject;

public class VendorDataSingleton {
    private static VendorDataSingleton instance;
    private JSONObject vendorData;

    private VendorDataSingleton() {}

    public void updateVendorData(String key, String value) {
        if (vendorData != null) {
            try {
                vendorData.put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static synchronized VendorDataSingleton getInstance() {
        if (instance == null) {
            instance = new VendorDataSingleton();
        }
        return instance;
    }

    public void setVendorData(JSONObject data) {
        this.vendorData = data;
    }

    public JSONObject getVendorData() {
        return this.vendorData;
    }
}
