package com.punuo.sys.net.delay;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.LinkProperties;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.punuo.sys.net.push.Constant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Iterator;

import static android.content.Context.WIFI_SERVICE;

public class BadNetworkCase {
    private final static String TAG = BadNetworkCase.class.getSimpleName();

    public static void showNetworkInfo(Context context) {
        badCase1(context);
        badCase2(context);
    }

    /**
     * BAD CASE #1
     *
     * @param context
     */
    private static void badCase1(Context context) {
        WifiManager my_wifiManager;
        WifiInfo wifiInfo;
        DhcpInfo dhcpInfo;

        my_wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        dhcpInfo = my_wifiManager.getDhcpInfo();
        wifiInfo = my_wifiManager.getConnectionInfo();

        String badTag = "-badcase#1";
        Log.e(TAG + badTag, "ipAddress: " + intToIp(dhcpInfo.ipAddress));
        Log.e(TAG + badTag, "netmask: " + intToIp(dhcpInfo.netmask));
        Log.e(TAG + badTag, "gateway: " + intToIp(dhcpInfo.gateway));
        Log.e(TAG + badTag, "serverAddress: " + intToIp(dhcpInfo.serverAddress));
        Log.e(TAG + badTag, "dns1: " + intToIp(dhcpInfo.dns1));
        Log.e(TAG + badTag, "dns2: " + intToIp(dhcpInfo.dns2));
        Log.e(TAG + badTag, "leaseDuration: " + String.valueOf(dhcpInfo.leaseDuration));
        Log.e(TAG + badTag, "ipAddress: " + intToIp(wifiInfo.getIpAddress()));
        Log.e(TAG + badTag, "macAddress: " + wifiInfo.getMacAddress());

        Constant.serverAddress = intToIp(dhcpInfo.serverAddress);
    }

    /**
     * BAD CASE #2
     *
     * @param context
     */
    private static void badCase2(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            try {
                Method e = ConnectivityManager.class.getMethod("getActiveLinkProperties", new Class[0]);
                LinkProperties prop = (LinkProperties) e.invoke(cm, new Object[0]);
                Inet4Address addresses = formatIpAddresses(prop);
                Constant.serverAddress = addresses.getHostAddress();
                Log.e(TAG + "-badcase#2", "address: " + addresses.toString());
            } catch (NoSuchMethodException var4) {
                var4.printStackTrace();
            } catch (InvocationTargetException var5) {
                var5.printStackTrace();
            } catch (IllegalAccessException var6) {
                var6.printStackTrace();
            }
        }
    }

    private static Inet4Address formatIpAddresses(LinkProperties prop) {
        if (prop == null) {
            return null;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            try {
                Method e = LinkProperties.class.getMethod("getAddresses", new Class[0]);
                e.setAccessible(true);
                Collection addresses = (Collection) e.invoke(prop, new Object[0]);
                Iterator var4 = addresses.iterator();
                while (var4.hasNext()) {
                    InetAddress address = (InetAddress) var4.next();
                    if (address instanceof Inet4Address) {
                        return (Inet4Address) address;
                    }
                }
            } catch (NoSuchMethodException var5) {
                var5.printStackTrace();
            } catch (InvocationTargetException var6) {
                var6.printStackTrace();
            } catch (IllegalAccessException var7) {
                var7.printStackTrace();
            }
        }

        return null;
    }

    private static String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }
}
