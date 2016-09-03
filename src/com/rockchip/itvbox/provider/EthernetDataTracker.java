package com.rockchip.itvbox.provider;

public class EthernetDataTracker {
    public static final String ETHERNET_STATE_CHANGED_ACTION = "android.net.ethernet.ETHERNET_STATE_CHANGED";
    public static final String EXTRA_ETHERNET_STATE = "ethernet_state";

    public static final String ETHERNET_IFACE_STATE_CHANGED_ACTION = "android.net.ethernet.ETHERNET_IFACE_STATE_CHANGED";
    public static final String EXTRA_ETHERNET_IFACE_STATE = "ethernet_iface_state";

    public static final int ETHER_STATE_DISCONNECTED=0;
    public static final int ETHER_STATE_CONNECTING=1;
    public static final int ETHER_STATE_CONNECTED=2;
}
