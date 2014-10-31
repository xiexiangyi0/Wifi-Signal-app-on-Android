package com.xiangyi.wifisignal;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.xiangyi.wifisignal.R;

import java.util.List;

public class MainActivity extends Activity {
    static final String TAG = "MainActivity";

    WifiControl m_wifi = null;

    WifiList m_list = null;

    Handler handler = null;
    Runnable runnable = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        FrameLayout cur = (FrameLayout) findViewById(R.id.wifi_cur);
        ListView list = (ListView)findViewById(R.id.wifi_list);


        m_list = new WifiList(this, list, cur);
        m_wifi = new WifiControl((WifiManager)getSystemService(Context.WIFI_SERVICE));


        handler = new Handler();
        runnable = new Runnable(){
            public void run () {
                update();
                handler.postDelayed(runnable,1000);
            }
        };

        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable,1000);

    }


    private void update() {
        m_wifi.scan();
        m_list.clean();
        List<WifiControl.Info> infos = m_wifi.getInfos();

        Log.d(TAG, "list size = " + infos.size());

        m_list.updateWifiList(infos);
        m_list.updateCurConn(m_wifi.getCurConn());
    }

    public void onClickScan(View v) {
        update();
    }

}

