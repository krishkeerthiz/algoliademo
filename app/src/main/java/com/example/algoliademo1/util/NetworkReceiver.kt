package com.example.algoliademo1.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast


class NetworkReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, p1: Intent?) {
        var status = NetworkUtil.getConnectivityStatus(context!!)
        if (status.isEmpty()) {
            status = "No Internet Connection"
            Toast.makeText(context, status, Toast.LENGTH_LONG).show();
        }

    }
}