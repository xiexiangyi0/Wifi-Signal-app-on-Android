package com.xiangyi.wifisignal;

/**
 * Created by Xiangyi on 9/25/2014.
 */
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;


public class WifiControl {

    WifiManager m_wifi_mgr = null;
    WifiInfo m_cur_conn = null;

    public List<Info> getInfos() {
        List<ScanResult> list = m_wifi_mgr.getScanResults();
        ArrayList<Info> ret = new ArrayList<Info>();
        for(ScanResult result : list) {
            if(m_cur_conn != null && result.BSSID.equals(m_cur_conn.getBSSID())) {
                Info info = new Info(result);
                Log.d("HIT", "HIT");
                info.m_level = m_cur_conn.getRssi();
                ret.add(info);
            } else {
                ret.add(new Info(result));
            }
        }

        return ret;
    }

    class InfoCur {
        WifiInfo m_info = null;
        public InfoCur(WifiInfo info) {
            m_info = info;
        }

        public int getNetworkId() {
            return m_info.getNetworkId();
        }

        public String toString() {
            return m_info.toString();
        }

        public String getNetworkName() {
            return m_info.getSSID();
        }

        public int getIpAddr() {

            return m_info.getIpAddress();
        }

        public int getLevel() {
            int rssi = m_info.getRssi();
            return rssi;
        }

        public int getSpeed() {
            return m_info.getLinkSpeed();
        }

        public String getMacAddr() {
            return m_info.getMacAddress();
        }

        public String getBssid() {
            return m_info.getBSSID();
        }
    }

    InfoCur getCurConn() {
        WifiInfo info = null;
        if(m_cur_conn == null) {
            info = m_wifi_mgr.getConnectionInfo();
            m_cur_conn = info;
        } else {
            info = m_cur_conn;
        }
        return new InfoCur(info);
    }

    class Info {

        ScanResult m_result;
        int m_level = -1;

        public Info(ScanResult result) {
            m_result = result;
            m_level = result.level;
        }

        public String getNetworkName() {
            return m_result.SSID;
        }

        public String getAddress() {
            return m_result.BSSID;
        }

        public String getCapabilities() {
            return m_result.capabilities;
        }

        public int getFreq() {
            return m_result.frequency;
        }

        public int getLevel() {
            return m_level;
        }

        public String toString() {
            return m_result.toString();
        }

    }

    public WifiControl(WifiManager wifi_mgr) {
        m_wifi_mgr = wifi_mgr;
    }

    public void scan() {
        Log.d("Control", m_wifi_mgr.toString());

        m_wifi_mgr.startScan();

        m_cur_conn = m_wifi_mgr.getConnectionInfo();
    }
}
