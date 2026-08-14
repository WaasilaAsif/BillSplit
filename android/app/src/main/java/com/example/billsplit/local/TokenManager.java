package com.example.billsplit.local;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Stores the JWT in EncryptedSharedPreferences (per the original
 * architecture doc — JWT-only, no Firebase). Must be initialized once,
 * in BillSplitApplication.onCreate(), before anything else touches it.
 */
public class TokenManager {

    private static final String PREFS_NAME = "secure_auth_prefs";
    private static final String KEY_TOKEN = "jwt_token";

    private static TokenManager instance;
    private final SharedPreferences prefs;

    private TokenManager(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            prefs = EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Could not initialize encrypted token storage", e);
        }
    }

    public static synchronized void init(Context context) {
        if (instance == null) {
            instance = new TokenManager(context.getApplicationContext());
        }
    }

    public static TokenManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("TokenManager.init(context) was never called — do it in BillSplitApplication.onCreate()");
        }
        return instance;
    }

    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    public void clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply();
    }
}