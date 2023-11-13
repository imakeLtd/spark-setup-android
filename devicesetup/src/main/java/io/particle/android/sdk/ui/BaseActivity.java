package io.particle.android.sdk.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RestrictTo;
import androidx.appcompat.app.AppCompatActivity;

import io.particle.android.sdk.cloud.SDKGlobals;
import io.particle.android.sdk.devicesetup.R;

/**
 * This class exists solely to avoid requiring SDK users to have to define
 * anything in an Application subclass.  By (ab)using this custom Activity,
 * we can at least be sure that the custom fonts in the device setup screens
 * work correctly without any special instructions.
 */
// this is a base activity, it shouldn't be registered.
@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public static boolean setupOnly = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        SDKGlobals.init(this);
    }

}
