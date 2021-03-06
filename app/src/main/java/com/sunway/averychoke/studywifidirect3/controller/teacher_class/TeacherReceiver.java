package com.sunway.averychoke.studywifidirect3.controller.teacher_class;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;

/**
 * Created by AveryChoke on 8/4/2017.
 */

public class TeacherReceiver extends BroadcastReceiver {
    public interface WifiDirectListener {
        void onWifiStateChanged(boolean isOn);
        void onWifiDirectDisconnection();
    }

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WifiDirectListener mListener;

    public TeacherReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, WifiDirectListener listener) {
        mManager = manager;
        mChannel = channel;
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (mManager == null || mChannel == null) {
            return;
        }

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            mListener.onWifiStateChanged(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED);

        } else if (WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION.equals(action)) {
            // constantly discover peers so that it is searchable by other players
            int discoveryState = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, -1);
            if (discoveryState == WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED) {
                mManager.discoverPeers(mChannel, null);
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // get calls the first time
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (!networkInfo.isConnected()) {
                mListener.onWifiDirectDisconnection();
            }
        }
    }
}
