package com.example.billsplit;

import android.app.Application;

import com.example.billsplit.local.TokenManager;

public class BillSplitApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TokenManager.init(this);
    }
}