package com.example.keynes.rollcall.adapter;

import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.keynes.rollcall.R;
import com.example.keynes.rollcall.TeacherActivity;

import java.util.ArrayList;

/**
 * Created by Keynes on 2017/5/5.
 */

public class TeacherAdapter extends ArrayAdapter <NsdServiceInfo> {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<NsdServiceInfo> mServiceInfo;

    public TeacherAdapter(Context context, ArrayList<NsdServiceInfo> serviceInfo) {
        super(context, R.layout.teacher_item, serviceInfo);
        mInflater = LayoutInflater.from(context);
        mServiceInfo = serviceInfo;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mServiceInfo.size();
    }

    @Override
    public NsdServiceInfo getItem(int i) {
        return mServiceInfo.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mServiceInfo.indexOf(getItem(i));
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View mView = view;

        if (mView == null) {
            LayoutInflater viewInflater;
            viewInflater = LayoutInflater.from(mContext);
            mView = viewInflater.inflate(R.layout.teacher_item, null);
        }

        NsdServiceInfo serviceInfo = getItem(i);

        if (serviceInfo != null) {
            TextView nameTextView = (TextView)mView.findViewById(R.id.teacher_name);
            TextView ipTextView = (TextView)mView.findViewById(R.id.teacher_ip);

            nameTextView.setText(serviceInfo.getServiceName());
            ipTextView.setText(serviceInfo.getHost().getHostAddress() + ":" + serviceInfo.getPort());
        }
        
        return mView;
    }
}
