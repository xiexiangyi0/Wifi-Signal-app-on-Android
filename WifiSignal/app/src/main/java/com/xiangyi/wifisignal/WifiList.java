package com.xiangyi.wifisignal;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class WifiList {
    static final String TAG = "WifiList";

    Context m_context = null;

    ListView m_wifi_list = null;
    FrameLayout m_wifi_cur = null;

    public WifiList(Context context, ListView list, FrameLayout desc) {
        m_context = context;
        m_wifi_cur = desc;
        m_wifi_list = list;
    }
    public void clean() {

    }

    public void updateWifiList(List<WifiControl.Info> infos) {

        List<Map<String, Object>> data = generateData(infos);

        String [] field = new String [] {"name", "address", "auth", "freq", "level"};
        int [] view_id = new int []
                {R.id.wifi_name, R.id.wifi_addr,R.id.wifi_auth, R.id.wifi_freq, R.id.wifi_level};


        SimpleAdapter adpt = new SimpleAdapter(
                m_context, data, R.layout.wifi_list, field, view_id);

        adpt.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object o, String s) {
                if(view.getId()!= R.id.wifi_level) {
                    return false;
                }

                ProgressBar bar = (ProgressBar) view.findViewById(R.id.PROGRESS_BAR);
                TextView text =(TextView) view.findViewById(R.id.PROGRESS_BAR_TEXT);
                Integer level = (Integer) o;
                text.setText(level.toString()+"dBm");

                //-113 ~ -30

                int progress = calculateProgress(level.intValue(), bar.getMax());
                bar.setProgress(progress);
                return true;
            }

        });

        m_wifi_list.setAdapter(adpt);
    }

    private static int calculateProgress(int i, int max) {
        final int min_level = -113;
        final int max_level = -30;
        if(i < min_level) {
            return 0;
        } else if(i > max_level) {
            return max;
        }

        return (i-min_level) * 100 / (max_level-min_level);
    }


    private List<Map<String, Object>> generateData(List<WifiControl.Info> infos) {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for(WifiControl.Info info : infos) {

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", info.getNetworkName());
            map.put("address", "BSSID: " + info.getAddress());
            map.put("auth", noBreakLine(info.getCapabilities()));
            map.put("freq", info.getFreq()+"MHz");
            map.put("level", new Integer(info.getLevel()));
            list.add(map);
        }
        return list;
    }



    private static String noBreakLine(String str) {
        return str;
    }



    public void updateCurConn(WifiControl.InfoCur conn) {
        if(conn == null || conn.getNetworkId() == -1) {

            if(m_wifi_cur.findViewById(R.id.connected_wifi_name) != null) {
                m_wifi_cur.removeAllViews();

                TextView text = new TextView(m_context);
                text.setText("No Connection");

                m_wifi_cur.addView(text);
            }
            return;
        }

        if(m_wifi_cur.findViewById(R.id.connected_wifi_name) == null) {
            m_wifi_cur.removeAllViews();
            LayoutInflater inflater = (LayoutInflater)m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout detail = new LinearLayout(m_context);
            inflater.inflate(R.layout.connected_wifi, detail);
            m_wifi_cur.addView(detail);
        }

        TextView name = (TextView) m_wifi_cur.findViewById(R.id.connected_wifi_name);
        name.setText("SSID: " + conn.getNetworkName());

        TextView ip = (TextView) m_wifi_cur.findViewById(R.id.connected_wifi_ip);
        Log.d("IP", convertIpToString(conn.getIpAddr()));
        ip.setText("IP Address: " + convertIpToString(conn.getIpAddr()));

        TextView mac = (TextView) m_wifi_cur.findViewById(R.id.connected_wifi_mac);
        mac.setText("Mac Address: " + conn.getMacAddr());

        TextView bssid = (TextView) m_wifi_cur.findViewById(R.id.connected_wifi_bssid);
        bssid.setText("BSSID: " + conn.getBssid());


        ProgressBar bar = (ProgressBar) m_wifi_cur.findViewById(R.id.connected_wifi_bar);
        bar.setProgress(calculateProgress(conn.getLevel(), bar.getMax()));
        TextView level = (TextView) m_wifi_cur.findViewById(R.id.connected_wifi_level);
        level.setText(conn.getLevel() + "dBm");

        TextView speed = (TextView) m_wifi_cur.findViewById(R.id.connected_wifi_speed);
        speed.setText("Speed: " + conn.getSpeed() + "Mbps");

    }


    private static String convertIpToString(int i) {
        return       ( i & 0xFF) + "."
                + ((i >> 8 ) & 0xFF) + "."
                +((i >> 16 ) & 0xFF) + "."
                +((i >> 24 ) & 0xFF);
    }
}

