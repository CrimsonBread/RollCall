package com.example.keynes.rollcall;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.keynes.rollcall.nsd.NsdHelper;

import java.util.ArrayList;

public class StudentActivity extends AppCompatActivity {

    private NsdManager mNsdManager;
    private NsdManager.ResolveListener mResolveListener;
    private NsdManager.DiscoveryListener mDiscoveryListener;
    private NsdManager.RegistrationListener mRegistrationListener;

    private static final String SERVICE_TYPE = "_http._tcp.";
    private static final String TAG = "StudentActivity";
    private String mServiceName = "B0242081";
    private String mTargetServiceName;

    private NsdServiceInfo mService;
    private NsdHelper mNsdHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        mNsdHelper = new NsdHelper(this);
        mNsdHelper.initializeNsd();

    }

    @Override
    protected void onStart() {
        super.onStart();

        mNsdHelper.discoverServices();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "Pausing.");
        if (mNsdHelper != null) {
            mNsdHelper.stopDiscovery();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "Resuming.");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Being stopped.");
        mNsdHelper.tearDown();
        mNsdHelper = null;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
