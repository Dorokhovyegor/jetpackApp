package com.dorokhov.jetpackapp.network

import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities

interface NetworkState {

    val isConnected: Boolean

    val network: Network?

    val networkCapabilities: NetworkCapabilities?

    val linkProperties: LinkProperties?

    val isWifi: Boolean
        get() = networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false

    /**
     * Check if the network is Mobile ( shortcut )
     */
    val isMobile: Boolean
        get() = networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ?: false

    /**
     * Get the interface name ( shortcut )
     */
    val interfaceName: String?
        get() = linkProperties?.interfaceName

}